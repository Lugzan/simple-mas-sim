package mas.simulator.agent.event

/**
 * User: Lugzan
 */
trait EventEmitter {
  def register(consumer: EventConsumer)
}
