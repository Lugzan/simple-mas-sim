package mas.simulator.env

import mas.simulator.agent.Agent

/**
 * User: Lugzan
 */
case class AgentInfo(id: Int, pos: (Int, Int))
case class AgentWithPos(agent: Agent, var pos: (Int, Int))
