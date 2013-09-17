package mas.simulator.agent

import mas.simulator.env.Data
/**
 * User: Lugzan
 */
trait ProcessingUnit {
  def process(data: Data)
}
