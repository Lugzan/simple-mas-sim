package mas.simulator.agent

import mas.simulator.env.EnvPart
import mas.simulator.agent.event.EventEmitter

/**
 * User: Lugzan
 *
 * Автопилот
 */
trait Autopilot extends EventEmitter {
  /**
   * Удалить все точки маршрута
   */
  def clear()

  /**
   * Добавить точку к маршруту
   * @param point Координаты точки
   * @param index Номер точки. Если -1, то точка будет добавлена последней
   */
  def addPoint(point: (Int, Int), index: Int = -1)

  /**
   * @return Текущая позиция
   */
  def getPosition: (Int, Int)

  def update(env: EnvPart)
}
