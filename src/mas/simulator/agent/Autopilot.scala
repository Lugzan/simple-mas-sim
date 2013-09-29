package mas.simulator.agent

import mas.simulator.env.EnvPart
import mas.simulator.agent.event.EventEmitter

/**
 * User: Lugzan
 */
trait Autopilot extends EventEmitter {
  def clear()
  def addPoint(point: (Int, Int), index: Int = -1)
  def getPosition: (Int, Int)

  def update(env: EnvPart)
}
