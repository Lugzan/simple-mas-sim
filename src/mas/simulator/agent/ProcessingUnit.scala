package mas.simulator.agent

import mas.simulator.env.{WideMeasure, EnvPart, Data}
import mas.simulator.agent.event.EventEmitter

/**
 * User: Lugzan
 */
trait ProcessingUnit extends EventEmitter {
  def process(data: Data)

  def update(env: EnvPart)

  def measure(): Int
  def measureWide(): WideMeasure
}
