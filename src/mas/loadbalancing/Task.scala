package mas.loadbalancing

/**
 * User: Dmitry.Naydanov
 * Date: 25.05.15.
 */
trait Task {
  def start()
  def isStarted: Boolean
  def isEnded: Boolean
  def iterate(agent: AbstractAgent): Boolean

  def getComputationalComplexity(agent: AbstractAgent): Int
  def getTransferCapacity: Int
}
