package mas.simulator.dumbImpl

import mas.simulator.env.OuterWorld
import mas.simulator.agent.event.Events
import mas.simulator.agent.Agents

/**
 * User: Lugzan
 */
class DumbImpl(agentNum: Int, logger: String => Unit, turns: Int, linearSize: Int, recLimit: Int) extends DumbAgents with
  Dumb2DPoints with DumbAutoPilots with DumbEnvironments with DumbMainComputers with DumbProcessingUnits {
  this: OuterWorld with Events with Agents =>


  def go() {
    val env = new EnvironmentImpl(linearSize, recLimit, logger)

    for (i <- 0 until agentNum) env addAgent (getAgent(i), (0, 0))
    env.init()
    for (i <- 0 until turns) env.iterate()
  }

  def getAgent(id: Int): Agent = new MyAgent(id, (0, 0))
}
