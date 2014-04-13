package mas.simulator.dumbImpl

import java.util
import mas.simulator.env.OuterWorld
import mas.simulator.agent.event.Events
import mas.simulator.agent.Agents
import scala.util.Random

/**
 * User: Lugzan
 */
trait DumbAgents {
  this: OuterWorld with Events with Agents with Dumb2DPoints with DumbAutoPilots with DumbEnvironments with
    DumbMainComputers with DumbProcessingUnits =>


  class MyAgent(id: Int, initPos: Point) extends Agent {
    def this(id: Int) {
      this(id, (0, 0))
    }

    protected val mainComputer: TripComputer = new MyMainComputer(id)
    protected val autopilot: Autopilot = new MyAutopilot(initPos)
    protected val processingUnit: ProcessingUnit = new MyProcessingUnit

    mainComputer register this
    autopilot register this
    processingUnit register this

    def getId = id

    def react(ev: AgentEvent) {
      println( s"Agent #$id received $ev")

      ev match {
        case InitEvent(_) =>
          mainComputer.startTimer(1, "a")
          autopilot.addPoint((Random.nextInt(100), Random.nextInt(100)))
        case TimerEvent(a) =>
          println(a)
          mainComputer.startTimer(1, a + "a")
        case LocationEvent(_, _) =>
          autopilot.addPoint((Random.nextInt(100), Random.nextInt(100)))
        case _ =>
      }
    }
  }
}
