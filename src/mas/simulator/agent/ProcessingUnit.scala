package mas.simulator.agent

import mas.simulator.env.{WideMeasure, EnvPart, Data}
import mas.simulator.agent.event.EventEmitter

/**
 * User: Lugzan
 */
trait ProcessingUnit extends EventEmitter {
  /**
   * Пока что заглушка
   * @param data
   */
  def process(data: Data)

  def update(env: EnvPart)

  /**
   * Получает "характеристику" точки поверхности, в которой сейчас находится агент. Характеристика измеряется с
   * некоторым шумом [[mas.simulator.env.impl.Noise]]
   * @return
   */
  def measure(): Int

  /**
   * То же, но характеристика измеряется в квадрате 3х3 с центорм в позиции агента. Шум больше
   * @return
   */
  def measureWide(): WideMeasure
}
