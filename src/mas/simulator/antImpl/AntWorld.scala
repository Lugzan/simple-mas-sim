package mas.simulator.antImpl

import mas.simulator.env.{WideMeasure, Message, OuterWorld}
import mas.simulator.agent.event.Events
import mas.simulator.agent.Agents
import mas.simulator.dumbImpl.Dumb2DPoints
import scala.collection.mutable
import scala.util.Random

/**
 * User: Lugzan
 */
trait AntWorld {
  this: OuterWorld with Events with Agents with Dumb2DPoints=>

  def flatObject(i: Int) = if (i > 0) -4 else i

  class AntEnvironment(linearSize: Int/*1000+*/, rocks: Int, food: Int, foodStore: Int,
                       eventRate: Double, val logger: String => Unit) extends Environment {
    import AntEnvironment._

    val ants = mutable.HashSet[AgentWithPos]()
    val surface = Array[Array[Int]]() // Array(X COORDINATE)(Y COORDINATE)
    val cmds = mutable.HashMap[Agent, Int]()
    val messages = mutable.HashMap[Int, Message[String]]()
    val foodMap = mutable.HashMap[(Int, Int), Int]()


    private var turn = 0

    assert(linearSize > 999)
    assert(eventRate >= 0.0 && eventRate <= 1.0)

    override def init() {
      val minSize = linearSize / 1000
      val maxSize = linearSize / 100

      for (i <- 1 to rocks) shitOnSurface(minSize, maxSize)  //no check cuz area is empty


      var rest = food
      var hmm = linearSize * linearSize

      while (rest > 0 && hmm > 0) {
        val locationX = Random nextInt linearSize
        val locationY = Random nextInt linearSize

        hmm -= 1

        if (surface(locationX)(locationY) != 1) {
          rest -= 1
          surface(locationX)(locationY) = AntEnvironment.food
          foodMap((locationX, locationY)) = foodStore
        }
      }
    }

    override def iterate() {
      turn += 1

      ants foreach {
        case ap@AgentWithPos(ant, (x, y)) =>
          cmds get ant map {
            case AntEnvironment.up    =>
              val u = flatObject(surface(x)(y + 1))
              if (u != rock && u != otherAnt) ap.pos = (x, y + 1)
            case AntEnvironment.down  =>
              val u = flatObject(surface(x)(y - 1))
              if (u != rock && u != otherAnt) ap.pos = (x, y - 1)
            case AntEnvironment.left  =>
              val u = flatObject(surface(x - 1)(y))
              if (u != rock && u != otherAnt) ap.pos = (x - 1, y)
            case AntEnvironment.right =>
              val u = flatObject(surface(x + 1)(y))
              if (u != rock && u != otherAnt) ap.pos = (x + 1, y)
            case _ =>
          }
      }

      ants foreach {
        case ant =>
          ant.agent react new AntEnvironmentPart(ant, this)
      }

      if (Random.nextDouble() < eventRate) shitOnSurface(linearSize / 1500, linearSize / 150)
    }

    override def addAgent(agent: Agent, initPos: (Int, Int)) {
      val (x, y) = initPos
      assert(surface(x)(y) == 0)
      ants += AgentWithPos(agent, initPos)
      surface(x)(y) = 4
    }

    def cutSquare(pos: (Int, Int)): Array[(Int, (Int, Int))] = {
      val (x, y) = pos
      val points =
        Array((x, y), (x-1, y), (x-1, y-1), (x, y-1), (x+1, y-1), (x+1, y), (x+1, y+1), (x, y+1), (x-1, y+1)) filter {
          case (x0, y0) => 0 < x0 || 0 < y0 || x0 >= linearSize || y0 >= linearSize
        }

      points map {
        case pp@(x0, y0) => (surface(x0)(y0), pp)
      }
    }

    def gatherFood(agent: AgentInfo): Boolean = {
      val pos = agent.pos
      foodMap get pos exists {
        case count =>
          if (count == 0) foodMap remove pos else foodMap(pos) -= 1
          true
      }
    }

    private def shitOnSurface(minSize: Int, maxSize: Int): Boolean = {
      val size = minSize + Random.nextInt(maxSize - minSize)
      val locationX = AntEnvironment.border + Random.nextInt(linearSize - AntEnvironment.border*2)
      val locationY = AntEnvironment.border + Random.nextInt(linearSize - AntEnvironment.border*2)

      for {
        x <- locationX to locationX + size
        y <- locationY to locationY + size
      } {
        val tpe = surface(x)(y)
        if (tpe != AntEnvironment.empty && tpe != AntEnvironment.rock && tpe != AntEnvironment.pheromone) return false
      }

      for {
        x <- locationX to locationX + size
        y <- locationY to locationY + size
      } {
        surface(x)(y) = rock
      }

      true
    }
  }

  object AntEnvironment {
    val border = 10

    val (empty, rock, food, pheromone, otherAnt, antHill) = (0, -1, -2, -3, -4, -5)
    val (up, down, left, right) = (11, 12, 13, 14)
  }

  class AntEnvironmentPart(agentPos: AgentWithPos, env: AntEnvironment) extends EnvPart {
    override def measureWide(): WideMeasure = {
      val (x, y) = agentPos.pos
      val s = env.surface

      def f(i: Int) = flatObject(i) match {
        case r@AntEnvironment.rock => r
        case a@AntEnvironment.otherAnt => a
        case h@AntEnvironment.antHill => h
        case _ => AntEnvironment.empty
      }
      def g(t: (Int, Int, Int)) = t match {case (a, b, c) => (f(a), f(b), f(c))}

      WideMeasure.apply(g((s(x-1)(y+1), s(x)(y+1), s(x+1)(y+1))), g((s(x-1)(y), s(x)(y), s(x+1)(y))),
        g((s(x-1)(y-1), s(x)(y-1), s(x+1)(y-1))))
    }

    override def measure(): Int = {
      val (x, y) = agentPos.pos
      env.surface(x)(y)
    }

    override def moveDown() {
      env.cmds.put(agentPos.agent, AntEnvironment.down)
    }

    override def moveUp() {
      env.cmds.put(agentPos.agent, AntEnvironment.up)
    }

    override def moveRight(){
      env.cmds.put(agentPos.agent, AntEnvironment.right)
    }

    override def moveLeft() {
      env.cmds.put(agentPos.agent, AntEnvironment.left)
    }

    override def getMyPosition: (Int, Int) = agentPos.pos

    override def getAllMessages: Iterable[Message[_]] = env.messages.get(agentPos.agent.getId)

    override def addMessage(m: Message[_]) {
      val receiver = m.receiverId

      if (getAllVisibleAgents.exists{case AgentInfo(a, _) if a == receiver => true; case _ => false}) {
        env.messages put (receiver, m.asInstanceOf[Message[String]])
      }
    }

    override def getAllVisibleAgents: Iterable[AgentInfo] = env.cutSquare(agentPos.pos) filter {
      case (id, _) => id > 0
    } map {
      case (id, point) => AgentInfo(id, point)
    }
  }
}
