package mas.simulator.agent.event

/**
 * User: Lugzan
 */
trait SingleWireEmitter extends EventEmitter {
  var myConsumer: Option[EventConsumer] = None

  def register(consumer: EventConsumer) {
    myConsumer = Option(consumer)
  }
}
