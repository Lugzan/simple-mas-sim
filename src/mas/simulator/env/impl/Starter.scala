package mas.simulator.env.impl

import mas.simulator.dumbImpl.Agents
import Agents._

/**
 * User: Lugzan
 * Date: 18.09.13
 * Time: 1:00
 */
class Starter(agentNum: Int, logger: String => Unit, turns: Int, linearSize: Int, recLimit: Int) {
  def go() {
    val env = new EnvironmentImpl(linearSize, recLimit, logger)

    for (i <- 0 until agentNum) env addAgent (new MyAgent(i, (0, 0)), (0, 0))
    for (i <- 0 until turns) env.iterate()
  }
}
