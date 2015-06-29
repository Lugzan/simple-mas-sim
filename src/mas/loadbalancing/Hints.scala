package mas.loadbalancing

/**
 * Created by Hasp on 22.06.15.
 */
object Hints {
  trait LocationHint

  trait TransferHint

  trait AgentHint extends LocationHint {
    def getAgent: AbstractAgent
  }

  trait TaskHint extends TransferHint {
    def getTask: Task
  }
}
