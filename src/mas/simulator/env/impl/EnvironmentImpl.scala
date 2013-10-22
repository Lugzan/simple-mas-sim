package mas.simulator.env.impl

import mas.simulator.env._
import java.util
import mas.simulator.agent.Agent
import scala.collection.JavaConversions._
import mas.simulator.env.Message
import mas.simulator.env.AgentWithPos

import EnvironmentImpl._
import scala.collection.mutable
import scala.util.Random
import mas.simulator.agent.event.Events.InitEvent

/**
 * User: Lugzan
 */
class EnvironmentImpl(val linearSize: Int, val receiverLimit: Int,
                      val logger: String => Unit, val measureMax: Int = 1000,
                      val noise: Noise = new SimpleNoiseImpl(2)) extends Environment {
  private val surface = new mutable.HashMap[(Int, Int), Int]()

  private var flag = true
  private var turn = 0

  private val messagesY1 = new util.TreeMap[Int, Message[_]]()
  private val messagesX1 = new util.TreeMap[Int, Message[_]]()

  private val messagesY2 = new util.TreeMap[Int, Message[_]]()
  private val messagesX2 = new util.TreeMap[Int, Message[_]]()

  private val agents = new util.ArrayList[AgentWithPos]()
  private val cmds = new util.HashMap[Int, Int]()

  private def move(agentId: Int, move: Int) {
    cmds put (agentId, move)
  }

  def addAgent(agent: Agent, initPos: (Int, Int)) {
    agents add AgentWithPos(agent, initPos)
  }

  def iterate() {
    flag = !flag
    turn += 1

    logger(s"Iteration #$turn")
    agents foreach {
      case info =>
        val id = info.agent.getId
        val pos = info.pos

        cmds get id match {
          case EnvironmentImpl.up => if (pos._1 < linearSize) info.pos = (pos._1 + 1, pos._2)
          case EnvironmentImpl.down => if (pos._1 > 0) info.pos = (pos._1 - 1, pos._2)
          case EnvironmentImpl.left => if (pos._2 > 0) info.pos = (pos._1, pos._2 - 1)
          case EnvironmentImpl.right => if (pos._2 < linearSize) info.pos = (pos._1, pos._2 + 1)
          case _ =>
        }

        //logger(s"Agent #$id is in ${info.pos} now")
    }

    agents foreach {
      case info =>
        val agent = info.agent
        val (x, y) = info.pos

        val visible = agents collect {
          case i if Math.abs(i.pos._1 - x) < receiverLimit && Math.abs(i.pos._2 - y) < receiverLimit =>
            AgentInfo(i.agent.getId, i.pos)
        }
        agent react new EnvPartImpl(info, visible.toSeq)
    }
  }

  private[impl] def processMessage(message: Message[_]) {
    val id: Int = message.senderId
    agents find (_.agent.getId == id) map {
      case a =>
        val (x, y) = a.pos
        val (setX, setY) = if (flag) (messagesX1, messagesY1) else (messagesX2, messagesY2)

        setX put (x, message)
        setY put (y, message)
    }
  }

  private[impl] def getAllMessagesFor(id: Int): Iterable[Message[_]] = {
    agents find (_.agent.getId == id) map {
      case a =>
        val (x, y) = a.pos
        val (setX, setY) = if (!flag) (messagesX1, messagesY1) else (messagesX2, messagesY2)
        val xM = setX subMap (x - receiverLimit, x + receiverLimit + 1) map {
          case (_, l) => l
        }//превращаем код в нечитаемое гавно
        setY subMap (y - receiverLimit, y + receiverLimit + 1) collect {
          case (_, l) if xM contains l => l
        }
    } getOrElse Seq.empty
  }

  private[impl] def measureFor(point: (Int, Int)): Int = surface get point getOrElse {
    val value = Random nextInt measureMax
    surface put (point, value)
    value
  }

  private class EnvPartImpl(myAgent: AgentWithPos, agents: Seq[AgentInfo]) extends EnvPart {
    val id = myAgent.agent.getId
    val env = EnvironmentImpl.this
    val logger = env.logger
    lazy val messages = env getAllMessagesFor id

    def getAllVisibleAgents: Iterable[AgentInfo] = agents

    def addMessage(m: Message[_]) {
      logger(s"Agent #$id sends message $m to agent #${m.receiverId}")
      env processMessage m
    }

    def getAllMessages = messages

    def getMyPosition = myAgent.pos

    def moveLeft() {
      env move (id, left)
    }

    def moveRight() {
      env move (id, right)
    }

    def moveUp() {
      env move (id, up)
    }

    def moveDown() {
      env move (id, down)
    }

    def measure(): Int = env.measureFor(myAgent.pos) + noise.noise(myAgent.pos)

    def measureWide(): WideMeasure = {
      val builder = new MeasureBuilder
      val x: Int = myAgent.pos._1

      for (i <- x - 1 to x + 1; y = myAgent.pos._2; j <- y - 1 to y + 1) {
        builder.add(env.measureFor((i, j)) + noise.areaNoise(myAgent.pos))
      }
      builder.get
    }
  }

  def init() {
    val targets = Array((43, 29), (11, 45), (33, 1), (21, 21), (17, 5), (13, 43), (1, 49), (9, 30), (44, 19), (17, 9))

    val qt : Int = ((targets.size) / agents.size())
    val rmd : Int = ((targets.size) % agents.size())
    val msg = new mutable.StringBuilder("")
    for(i <- 0 to agents.size() - 1) {
      msg.clear()
      for(j <- 0 to qt - 1) {
        msg.append(targets(j + i * qt)._1)
        msg.append(" ")
        msg.append(targets(j + i * qt)._2)
        msg.append(" ")
      }
      if(i < rmd) {
        msg.append(targets(targets.size - 1 - i)._1)
        msg.append(" ")
        msg.append(targets(targets.size - 1 - i)._2)
        msg.append(" ")
      }
      agents.get(i).agent.react(InitEvent(msg.toString()))
    }
    //agents.foreach(_.agent.react(InitEvent("")))
  }
}

object EnvironmentImpl {
  val (up, down, left, right) = (1, 2, 3, 4)
}
