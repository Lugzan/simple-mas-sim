package mas.simulator.agent

import mas.simulator.env.{EnvPart, Message}
import mas.simulator.agent.event.EventEmitter

/**
 * User: Lugzan
 */
trait TripComputer extends EventEmitter {
  val id: Int

  def send[T](message: Message[T])
  def getAllReceived: Iterable[Message[_]]

  def update(env: EnvPart)

  final def getMyReceived = getAllReceived filter (_.receiverId == id)

  def startTimer(clock: Int, msg: String)
}
