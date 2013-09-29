package mas.simulator.agent

import mas.simulator.env.{EnvPart, Message}
import mas.simulator.agent.event.EventEmitter

/**
 * User: Lugzan
 */
trait TripComputer extends EventEmitter {
  val id: Int

  /**
   * Отправить сообщение в среду.
   */
  def send[T](message: Message[T])

  /**
   * Получить все сообщения, которые поймал приемник
   * @return
   */
  def getAllReceived: Iterable[Message[_]]

  def update(env: EnvPart)

  /**
   * Получить сообщения. предназначенные агенту
   * @return
   */
  final def getMyReceived = getAllReceived filter (_.receiverId == id)

  /**
   * Дать команду компьютеру создать TimerEvent через `clock` тактов. См. [[mas.simulator.agent.event.Events]]
   */
  def startTimer(clock: Int, msg: String)
}
