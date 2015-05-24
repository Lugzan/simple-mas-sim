package mas.loadbalancing

/**
 * User: Dmitry.Naydanov
 * Date: 25.05.15.
 */
trait InformationStrategy {
  /**
   * Сообщить об очередной итерации (т.е., что время увеличилось на один)
   */
  def advance(): Unit
  def shouldUpdate(): Boolean
  def update(me: AbstractAgent, neighbours: Iterable[AbstractAgent]): Unit

  def getStatus: InfoStatus
}
