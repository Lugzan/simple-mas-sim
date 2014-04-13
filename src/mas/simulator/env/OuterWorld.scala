package mas.simulator.env

import mas.simulator.agent.Agents
import mas.simulator.agent.event.Events
import scala.util.Random

/**
 * User: Lugzan
 */
trait OuterWorld {
  this: Agents with Events =>

  type Point

  trait Environment {
    def addAgent(agent: Agent, initPos: Point)

    def iterate()
    def init()
  }

  trait EnvPart {
    def getAllVisibleAgents: Iterable[AgentInfo]

    def addMessage(m: Message[_])
    def getAllMessages: Iterable[Message[_]]

    def getMyPosition: Point

    def moveLeft()
    def moveRight()
    def moveUp()
    def moveDown()

    def measure(): Int
    def measureWide(): WideMeasure
  }

  case class AgentInfo(id: Int, pos: Point)
  case class AgentWithPos(agent: Agent, var pos: Point)

  trait Noise {
    def noise(p: Point): Int
    def areaNoise(center: Point): Int
  }

  class SimpleNoiseImpl(level: Int) extends Noise {
    def noise(p: Point) = Random.nextInt(level) - Random.nextInt(level)

    def areaNoise(center: Point) =
      Random.nextInt(level)*Random.nextInt(level) - Random.nextInt(level)*Random.nextInt(level)
  }
}
