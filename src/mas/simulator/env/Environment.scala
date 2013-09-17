package mas.simulator.env

import mas.simulator.agent.Agent

/**
 * User: Lugzan
 */
trait Environment {
  def addAgent(agent: Agent, initPos: (Int, Int))

  def iterate()
}
