package mas.simulator.agent.event

import mas.simulator.agent.Agents
import mas.simulator.env.OuterWorld

/**
 * User: Lugzan
 */
trait Events {
  this: Agents with OuterWorld =>

  abstract class AgentEvent

  /**
   * Некое абстрактное событие с данными
   */
  case class DataEvent[T](data: T) extends AgentEvent

  /**
   * Событие, порождаемое автопилотом, когда агент оказывается в одной из заданных точек маршрута
   */
  case class LocationEvent(point: Point, msg: String) extends AgentEvent

  /**
   * Событие, порождаемое бортовым компьютером, когда он принимает какие-то сообщения
   */
  case class MessagesEvent(msg: String) extends AgentEvent

  /**
   * Cобытие, порождаемое при сигнале таймера
   */
  case class TimerEvent(msg: String) extends AgentEvent

  /**
   * Событие, порождаемое в самом начале при инициализации системы
   */
  case class InitEvent(msg: String) extends AgentEvent

  trait EventEmitter {
    def register(consumer: EventConsumer)
  }

  trait EventConsumer {
    def react(ev: AgentEvent)
  }

  trait SingleWireEmitter extends EventEmitter {
    var myConsumer: Option[EventConsumer] = None

    def register(consumer: EventConsumer) {
      myConsumer = Option(consumer)
    }
  }
}
