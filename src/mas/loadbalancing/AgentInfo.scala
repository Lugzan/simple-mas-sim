package mas.loadbalancing

/**
 * Created by Hasp on 22.06.15.
 */
trait AgentInfo {
  /**
   * @return Список задач, которые отправляются или принимаются в виде (Задача, КомуИлиОтКого, ОтправляетсяЛи)
   */
  def getCurrentPipesLoad: Iterable[(Task, Long, Boolean)]

  /**
   * @return Задачи на агенте, которые еще НЕ НАЧАЛИ выполняться
   */
  def getQueuedTasks: Iterable[Task]
  def getAllTasks: Iterable[Task]
  def getTasksInTransfer: Iterable[Task]

  def getExecutingTask: Option[Task]

  def assessTaskComplexity(task: Task): Option[Double]

  /**
   * @return Список каналов агента, в виде списка (Пропускная способность, Агент на другом конце)
   */
  def getAllPipes: Iterable[(Double, Long)]
}
