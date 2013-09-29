package mas.simulator.agent.event

import mas.simulator.env.Message

/**
 * User: Lugzan
 */
object Events {
  abstract class AgentEvent

  case class DataEvent[T](data: T) extends AgentEvent
  case class LocationEvent(point: (Int, Int), msg: String) extends AgentEvent
  case class MessageEvent[T](msg: Message[T]) extends AgentEvent
  case class TimerEvent(msg: String) extends AgentEvent

  case class InitEvent(msg: String) extends AgentEvent
}
