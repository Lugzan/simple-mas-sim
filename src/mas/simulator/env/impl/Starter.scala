package mas.simulator.env.impl

import mas.simulator.dumbImpl.Agents
import Agents._
import mas.simulator.agent.Agent

/**
 * User: Lugzan
 * Date: 18.09.13
 * Time: 1:00
 */
class Starter(agentNum: Int, logger: String => Unit, turns: Int, linearSize: Int, recLimit: Int) {
  def this (agentNum: Int, turns: Int, linearSize: Int, recLimit: Int) {
    this(agentNum, println, turns, linearSize, recLimit)
  }

  def go() {
    val env = new EnvironmentImpl(linearSize, recLimit, logger)

    for (i <- 0 until agentNum) env addAgent (getAgent(i), (0, 0))
    env.init()
    for (i <- 0 until turns) env.iterate()
  }

  def getAgent(id: Int): Agent = new MyAgent(id, (0, 0))
}
