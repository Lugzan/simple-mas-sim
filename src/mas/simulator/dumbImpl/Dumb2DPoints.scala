package mas.simulator.dumbImpl

import mas.simulator.env.OuterWorld
import mas.simulator.agent.event.Events
import mas.simulator.agent.Agents

/**
 * User: Lugzan
 */
trait Dumb2DPoints {
  this: OuterWorld with Events with Agents =>
  type Point = (Int, Int)
}
