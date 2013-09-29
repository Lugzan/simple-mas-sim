package mas.simulator.agent

import mas.simulator.env.EnvPart
import mas.simulator.agent.event.EventConsumer

/**
 * User: Lugzan
 */
trait Agent extends EventConsumer {
  /**
   * Основные устройства на борту БПЛА, с которыми могут взаимодействовать мозги агента
   */
  protected val mainComputer: TripComputer
  protected val autopilot: Autopilot
  protected val processingUnit: ProcessingUnit

  def getId: Int

  final def react(env: EnvPart) {
    mainComputer update env
    autopilot update env
    processingUnit update env
  }
}
