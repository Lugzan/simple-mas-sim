package mas.simulator.agent.event

/**
 * User: Lugzan
 */
trait EventConsumer {
  def react(ev: Events.AgentEvent)
}
