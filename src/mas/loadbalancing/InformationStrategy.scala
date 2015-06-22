package mas.loadbalancing

/**
 * User: Dmitry.Naydanov
 * Date: 25.05.15.
 */
trait InformationStrategy {
  def shouldUpdate(me: AbstractAgent): Boolean
  def update(me: AbstractAgent): Unit

  def getStatus: InfoStatus
}
