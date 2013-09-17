package mas.simulator.env

/**
 * User: Lugzan
 */
trait EnvPart {
  def getAllVisibleAgents: Iterable[AgentInfo]

  def addMessage(m: Message[_])
  def getAllMessages: Iterable[Message[_]]

  def getMyPosition: (Int, Int)

  def moveLeft()
  def moveRight()
  def moveUp()
  def moveDown()
}
