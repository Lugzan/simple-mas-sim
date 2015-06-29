package mas.loadbalancing

/**
 * User: Dmitry.Naydanov
 * Date: 25.05.15.
 */
trait AbstractAgent extends AgentInfo {
  def locationStrategy: LocationStrategy
  def informationStrategy: InformationStrategy
  def transferStrategy: TransferStrategy

  /**
   * Одна итерация жизненного цикла агента
   */
  def iterate(): Unit = {
//    informationStrategy.advance()
//    if (informationStrategy.shouldUpdate()) informationStrategy.update(this, getAllPipes.map(_._2))
//
//    getExecutingTask match {
//      case Some(task) =>
//        task.iterate(this)
//        if (task.isEnded) getNextTask.foreach(a => setExecutingTask(a))
//      case _ => getNextTask.foreach(a => setExecutingTask(a))
//    }
//
//    val status = informationStrategy.getStatus
//
//    val receivers = locationStrategy.getEligibleNodes(status)
//    val pp = getAllPipes
//
//    val pipes = receivers.flatMap {
//      case rr => pp.find(a => a._2 == rr)
//    }
//
//    val result = pipes map {
//      case pipe => (getCurrentTasks.find {
//        case task => transferStrategy.shouldTransfer(this, pipe, task, status)
//      }, pipe)
//    }
//
//    result foreach {
//      case (Some(task), (_, agent)) if getCurrentTasks.exists(_ == task) =>
//        startSend(task, agent)
//        excludeTask(task)
//      case _ =>
//    }
//
//    //TODO continue/finish sending
  }


  def startAccept(task: Task, from: AbstractAgent): Boolean
  def continueAccept(task: Task, from: AbstractAgent): Boolean
  def endAccept(task: Task, from: AbstractAgent): Boolean

  def startSend(task: Task, to: AbstractAgent): Boolean
  def continueSend(task: Task, to: AbstractAgent): Boolean
  def endSend(task: Task, to: AbstractAgent): Boolean


  /**
   * Exclude task - we start sending or executing task
   */
  def excludeTask(task: Task): Unit

  /**
   * Remove task - we sent or executed task
   */
  def removeTask(task: Task): Unit

  def setExecutingTask(task: Task): Unit
  def getNextTask: Option[Task]

  def getTime: Double
  def incrementTime(t: Double): Unit

  private val events = new EventQueue
}
