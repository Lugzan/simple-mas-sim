package mas.simulator.dumbImpl

import mas.simulator.env.OuterWorld
import mas.simulator.agent.event.Events
import mas.simulator.agent.Agents
import java.util

/**
 * User: Lugzan
 */
trait DumbAutoPilots {
  this: OuterWorld with Events with Agents with Dumb2DPoints with DumbAgents with DumbEnvironments with
    DumbMainComputers with DumbProcessingUnits =>

  class MyAutopilot(initPos: Point) extends Autopilot with SingleWireEmitter {
    private var env: Option[EnvPart] = None
    private val points = new util.LinkedList[Point]

    def clear() {
      points.clear()
    }

    def addPoint(point: Point, index: Int = -1) {
      if (index == -1) points.add(point) else points.add(index, point)
    }

    def getPosition = env map (_.getMyPosition) getOrElse initPos

    def update(env: EnvPart) {
      this.env = Some(env)
      val position: Point = getPosition


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
}
