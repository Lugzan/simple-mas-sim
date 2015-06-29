package mas.loadbalancing

/**
 * User: Dmitry.Naydanov
 * Date: 25.05.15.
 */
trait InformationStrategy {
  def shouldUpdate(): Boolean
  def update(): InfoStatus
}
