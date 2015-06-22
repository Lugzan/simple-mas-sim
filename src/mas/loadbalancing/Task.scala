package mas.loadbalancing

/**
 * User: Dmitry.Naydanov
 * Date: 25.05.15.
 */
trait Task {
  def start()
  def isStarted: Boolean
  def isEnded: Boolean

  def iterate(agent: AbstractAgent): Boolean = iterate(agent, 1.0)
  def iterate(agent: AbstractAgent, t: Double): Boolean

  def getComputationalComplexity(agent: AbstractAgent): Double
  def getTransferCapacity: Double
}
