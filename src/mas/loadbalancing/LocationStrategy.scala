package mas.loadbalancing

/**
 * User: Dmitry.Naydanov
 * Date: 25.05.15.
 */
trait LocationStrategy {
  /**
   * @return Список узлов, упорядоченных по увеличению степени загрузки
   */
  def getEligibleNodes(hints: Hints.TransferHint, info: InfoStatus): Hints.LocationHint
}
