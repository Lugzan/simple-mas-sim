package mas.simulator.agent

import mas.simulator.env.EnvPart
import mas.simulator.agent.event.EventConsumer

/**
 * User: Lugzan
 * Date: 16.09.13
 * Time: 0:07
 */
trait Agent extends EventConsumer {
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
