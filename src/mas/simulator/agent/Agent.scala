package mas.simulator.agent

import mas.simulator.env.EnvPart

/**
 * User: Lugzan
 * Date: 16.09.13
 * Time: 0:07
 */
trait Agent {
  protected val mainComputer: TripComputer
  protected val autopilot: Autopilot
  protected val processingUnit: ProcessingUnit

  def getId: Int

  def reactInner()

  final def react(env: EnvPart) {
    mainComputer update env
    autopilot update env

    reactInner()
  }
}
