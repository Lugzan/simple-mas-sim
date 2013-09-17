package mas.simulator.agent

import mas.simulator.env.EnvPart

/**
 * User: Lugzan
 */
trait Autopilot {
  def clear()
  def addPoint(point: (Int, Int), index: Int = -1)
  def getPosition: (Int, Int)

  def update(env: EnvPart)
}
