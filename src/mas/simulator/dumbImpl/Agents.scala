package mas.simulator.dumbImpl

import mas.simulator.agent.{TripComputer, ProcessingUnit, Autopilot, Agent}
import mas.simulator.env.{WideMeasure, Message, Data, EnvPart}
import java.util
import mas.simulator.agent.event.Events._
import mas.simulator.agent.event.{Events, SingleWireEmitter}
import mas.simulator.agent.event.Events.TimerEvent
import mas.simulator.agent.event.Events.InitEvent
import mas.simulator.env.Message
import mas.simulator.agent.event.Events.MessagesEvent
import scala.Some
import scala.util.Random
import java.util.StringTokenizer

/**
 * User: Lugzan
 */
object Agents {
  class MyAgent(id: Int, initPos: (Int, Int)) extends Agent {
    def this(id: Int) {
      this(id, (0, 0))
    }

    protected val mainComputer: TripComputer = new MyMainComputer(id)
    protected val autopilot: Autopilot = new MyAutopilot(initPos)
    protected val processingUnit: ProcessingUnit = new MyProcessingUnit

    protected val measurements = new util.HashMap[(Int, Int), Double]
    protected val lastSent = new util.HashMap[Int, Int]

    mainComputer register this
    autopilot register this
    processingUnit register this

    def getId = id

    def react(ev: AgentEvent) {
      ev match {
        case InitEvent(msg) =>
          mainComputer.startTimer(1, "a")
          val Coordinates = new StringTokenizer(msg)
          println (msg)
          while(Coordinates.hasMoreTokens()) {
            val X = Integer.parseInt(Coordinates.nextToken())
            val Y = Integer.parseInt(Coordinates.nextToken())
            autopilot.addPoint((X, Y))
          }
        //autopilot.addPoint((Random.nextInt(100), Random.nextInt(100)))
        case TimerEvent(a) =>
          //println(a)
          mainComputer.startTimer(1, a + "a")
        case LocationEvent(point, _) =>
          measurements.put(point, processingUnit.measure())
          println("Measured and saved: " + measurements.get(point) + " at " + point.toString())
        //autopilot.addPoint((Random.nextInt(100), Random.nextInt(100)))
        case _ =>
      }
    }
  }


  class MyAutopilot(initPos: (Int, Int)) extends Autopilot with SingleWireEmitter {
    private var env: Option[EnvPart] = None
    private val points = new util.LinkedList[(Int, Int)]

    def clear() {
      points.clear()
    }

    def addPoint(point: (Int, Int), index: Int = -1) {
      if (index == -1) points.add(point) else points.add(index, point)
    }

    def getPosition = env map (_.getMyPosition) getOrElse initPos

    def update(env: EnvPart) {
      this.env = Some(env)
      val position: (Int, Int) = getPosition


      if (points.isEmpty) return
      var next = points.getFirst

      while (!points.isEmpty && position == next) {
        myConsumer map (_ react LocationEvent(next, ""))
        points.pollFirst()
        if (!points.isEmpty) next = points.getFirst
      }

      val xDif = next._1 - position._1
      if (xDif > 0) env.moveUp() else if (xDif < 0) env.moveDown() else {
        val yDif = next._2 - position._2
        if (yDif > 0) env.moveRight() else env.moveLeft()
      }
    }
  }


  class MyProcessingUnit extends ProcessingUnit with SingleWireEmitter {
    private var currentEnv: Option[EnvPart] = None

    def process(data: Data) {}

    def update(env: EnvPart) {
      currentEnv = Some(env)
    }

    def measure() = currentEnv map (_.measure()) getOrElse -1

    def measureWide() = currentEnv map (_.measureWide()) getOrElse WideMeasure.dumb
  }


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

        myConsumer map (_ react Events.TimerEvent(min.msg))
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
