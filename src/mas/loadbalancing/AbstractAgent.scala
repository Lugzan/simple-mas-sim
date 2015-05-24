package mas.loadbalancing

/**
 * User: Dmitry.Naydanov
 * Date: 25.05.15.
 */
trait AbstractAgent {
  def locationStrategy: LocationStrategy
  def informationStrategy: InformationStrategy
  def transferStrategy: TransferStrategy

  /**
   * Одна итерация жизненного цикла агента
   */
  def iterate(): Unit = {
    informationStrategy.advance()
    if (informationStrategy.shouldUpdate()) informationStrategy.update(this, getAllPipes.map(_._2))

    getExecutingTask match {
      case Some(task) =>
        task.iterate(this)
        if (task.isEnded) getNextTask.foreach(a => setExecutingTask(a))
      case _ => getNextTask.foreach(a => setExecutingTask(a))
    }

    val status = informationStrategy.getStatus

    val receivers = locationStrategy.getEligibleNodes(status)
    val pp = getAllPipes

    val pipes = receivers.flatMap {
      case rr => pp.find(a => a._2 == rr)
    }

    val result = pipes map {
      case pipe => (getCurrentTasks.find {
        case task => transferStrategy.shouldTransfer(this, pipe, task, status)
      }, pipe)
    }

    //TODO send selected in each pipe
  }


  def startAccept(task: Task, from: AbstractAgent): Boolean
  def continueAccept(task: Task, from: AbstractAgent): Boolean
  def endAccept(task: Task, from: AbstractAgent): Boolean

  def startSend(task: Task, to: AbstractAgent): Boolean
  def continueSend(task: Task, to: AbstractAgent): Boolean
  def endSend(task: Task, to: AbstractAgent): Boolean

  /**
   * @return Задачи на агенте, которые еще НЕ НАЧАЛИ выполняться
   */
  def getCurrentTasks: Iterable[Task]
  def getExecutingTask: Option[Task]
  def setExecutingTask(task: Task): Unit
  def getNextTask: Option[Task]

  def getTime: Int
  def incrementTime(): Unit

  /**
   * @return Список каналов агента, в виде списка (Пропускная способность, Агент на другом конце)
   */
  def getAllPipes: Iterable[(Int, AbstractAgent)]
}
