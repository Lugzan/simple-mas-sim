package mas.simulator.agent

import mas.simulator.env.{EnvPart, Message}

/**
 * User: Lugzan
 */
trait TripComputer {
  val id: Int

  def send[T](message: Message[T])
  def getAllReceived: Iterable[Message[_]]

  def update(env: EnvPart)

  final def getMyReceived = getAllReceived filter (_.receiverId == id)
}
