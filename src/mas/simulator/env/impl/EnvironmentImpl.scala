package mas.simulator.env.impl

import mas.simulator.env._
import java.util
import mas.simulator.agent.Agent
import scala.collection.JavaConversions._
import mas.simulator.env.Message
import mas.simulator.env.AgentWithPos

import EnvironmentImpl._

/**
 * User: Lugzan
 */
class EnvironmentImpl(val linearSize: Int, val receiverLimit: Int, val logger: String => Unit) extends Environment {
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

        logger(s"Agent #$id is in ${info.pos} now")
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
  }
}

object EnvironmentImpl {
  val (up, down, left, right) = (1, 2, 3, 4)
}
