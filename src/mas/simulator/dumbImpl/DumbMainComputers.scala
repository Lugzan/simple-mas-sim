package mas.simulator.dumbImpl

import mas.simulator.env.{Message, OuterWorld}
import mas.simulator.agent.event.Events
import mas.simulator.agent.Agents
import java.util

/**
 * User: Lugzan
 */
trait DumbMainComputers {
  this: OuterWorld with Events with Agents with Dumb2DPoints with DumbAutoPilots with DumbAgents with
    DumbAgents with DumbProcessingUnits =>

  class MyMainComputer(myId: Int) extends TripComputer with SingleWireEmitter {
    val id = myId

    private val timers = new util.TreeSet[MySetEntry]()

    private var currentClock = 0
    private var currentEnv: Option[EnvPart] = None

    def send[T](message: Message[T]) {
      currentEnv map (_ addMessage message)
    }

    def getAllReceived = currentEnv map (_.getAllMessages) getOrElse scala.List.empty

    def update(env: EnvPart) {
      currentEnv = Some(env)
      currentClock += 1

      while (!timers.isEmpty && timers.first().clock == currentClock) {
        val min = timers.first()

        myConsumer map (_ react TimerEvent(min.msg))
        timers remove min
      }

      if (getAllReceived.size != 0) myConsumer map (_ react MessagesEvent(""))
    }

    def startTimer(clock: Int, msg: String) {
      timers.add(MySetEntry(currentClock + clock, msg))
    }

    private case class MySetEntry(clock: Int, msg: String) extends Comparable[MySetEntry] {
      def compareTo(o: MyMainComputer.this.type#MySetEntry): Int = {
        val i = this.clock - o.clock
        if (i == 0) this.msg compareTo o.msg else i
      }
    }
  }
}
