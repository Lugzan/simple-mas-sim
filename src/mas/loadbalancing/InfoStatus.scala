package mas.loadbalancing

/**
 * User: Dmitry.Naydanov
 * Date: 25.05.15.
 */
trait InfoStatus {
  def me: AgentInfo
  def getData[A]: Map[Key[A], A]

  trait Key[T]
}
