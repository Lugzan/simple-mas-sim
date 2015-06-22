package mas.loadbalancing

/**
 * User: Dmitry.Naydanov
 * Date: 25.05.15.
 */
trait TransferStrategy {
  /**
   * @return Следует ли отдавать задачу
   */
  def shouldTransfer(hints: Hints.LocationHint, info: InfoStatus): Hints.TransferHint

  /**
   * Видимо, не нужен
   * @return При превышении какого значения нам будет выгодно отдать задачу
   */
//  def thresholdM(info: InfoStatus): Int
}
