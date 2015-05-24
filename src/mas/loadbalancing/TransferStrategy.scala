package mas.loadbalancing

/**
 * User: Dmitry.Naydanov
 * Date: 25.05.15.
 */
trait TransferStrategy {
  /**
   * @param me Кто отдает задачу
   * @param receiver Кто получает задачу (канал <Пропускная способность, Агент на другом конце>)
   * @param task задача
   * @return Следует ли отдавать задачу
   */
  def shouldTransfer(me: AbstractAgent, receiver: (Int, AbstractAgent), task: Task, info: InfoStatus): Boolean

  /**
   * Видимо, не нужен
   * @return При превышении какого значения нам будет выгодно отдать задачу
   */
//  def thresholdM(info: InfoStatus): Int
}
