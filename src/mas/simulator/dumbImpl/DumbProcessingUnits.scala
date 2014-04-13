package mas.simulator.dumbImpl

import mas.simulator.env.{WideMeasure, Data, OuterWorld}
import mas.simulator.agent.event.Events
import mas.simulator.agent.Agents

/**
 * User: Lugzan
 */
trait DumbProcessingUnits {
  this: OuterWorld with Events with Agents with Dumb2DPoints with DumbAutoPilots with DumbAgents with
    DumbMainComputers with DumbAgents =>

  class MyProcessingUnit extends ProcessingUnit with SingleWireEmitter {
    private var currentEnv: Option[EnvPart] = None

    def process(data: Data) {}

    def update(env: EnvPart) {
      currentEnv = Some(env)
    }

    def measure() = currentEnv map (_.measure()) getOrElse -1

    def measureWide() = currentEnv map (_.measureWide()) getOrElse WideMeasure.dumb
  }
}
