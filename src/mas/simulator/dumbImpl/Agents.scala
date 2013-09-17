package mas.simulator.dumbImpl

import mas.simulator.agent.{TripComputer, ProcessingUnit, Autopilot, Agent}
import mas.simulator.env.{Message, Data, EnvPart}
import java.util

/**
 * User: Lugzan
 */
object Agents {
  class MyAgent(id: Int, initPos: (Int, Int)) extends Agent {
    protected val mainComputer: TripComputer = new MyMainComputer(id)
    protected val autopilot: Autopilot = new MyAutopilot(initPos)
    protected val processingUnit: ProcessingUnit = new MyProcessingUnit

    def reactInner() {

    }

    def getId = id
  }


  class MyAutopilot(initPos: (Int, Int)) extends Autopilot {
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
      val next = points.getFirst
      if (position == next) points.pollFirst()
      if (points.isEmpty) return

      val xDif = next._1 - position._1
      if (xDif > 0) env.moveUp() else if (xDif < 0) env.moveDown() else {
        val yDif = next._2 - position._2
        if (yDif > 0) env.moveRight() else env.moveLeft()
      }
    }
  }


  class MyProcessingUnit extends ProcessingUnit {
    def process(data: Data) {}
  }


  class MyMainComputer(myId: Int) extends TripComputer {
    private var currentEnv: Option[EnvPart] = None
    val id = myId

    def send[T](message: Message[T]) {
      currentEnv map (_ addMessage message)
    }

    def getAllReceived = currentEnv map (_.getAllMessages) getOrElse scala.List.empty

    def update(env: EnvPart) {
      currentEnv = Some(env)
    }
  }
}
