package mas.simulator.user

import mas.simulator.dumbImpl.DumbImpl
import mas.simulator.agent.Agents
import mas.simulator.agent.event.Events
import mas.simulator.env.OuterWorld

/**
 * User: Lugzan
 */
object TestMain {
  def start(agentNum: Int, logger: String => Unit, turns: Int, linearSize: Int, recLimit: Int) {
    val interpreter =
      new DumbImpl(
        agentNum: Int, logger: String => Unit, turns: Int, linearSize: Int, recLimit: Int
      ) with Agents with Events with OuterWorld

    interpreter.go()
  }

  def main(args: Array[String]) {
    start(3, println, 50, 100, 3)
  }
}
