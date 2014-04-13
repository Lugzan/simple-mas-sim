package mas.simulator.agent

import mas.simulator.env._
import mas.simulator.agent.event.Events
import mas.simulator.env.Message

/**
 * User: Lugzan
 */
trait Agents {
  this: OuterWorld with Events =>

  trait Agent extends EventConsumer {
    /**
     * Основные устройства на борту БПЛА, с которыми могут взаимодействовать мозги агента
     */
    protected val mainComputer: TripComputer
    protected val autopilot: Autopilot
    protected val processingUnit: ProcessingUnit

    def getId: Int

    final def react(env: EnvPart) {
      mainComputer update env
      autopilot update env
      processingUnit update env
    }
  }

  trait Autopilot extends EventEmitter {
    /**
     * Удалить все точки маршрута
     */
    def clear()

    /**
     * Добавить точку к маршруту
     * @param point Координаты точки
     * @param index Номер точки. Если -1, то точка будет добавлена последней
     */
    def addPoint(point: Point, index: Int = -1)

    /**
     * @return Текущая позиция
     */
    def getPosition: Point

    def update(env: EnvPart)
  }

  trait ProcessingUnit extends EventEmitter {
    /**
     * Пока что заглушка
     * @param data Data
     */
    def process(data: Data)

    def update(env: EnvPart)

    /**
     * Получает "характеристику" точки поверхности, в которой сейчас находится агент. Характеристика измеряется с
     * некоторым шумом
     * @return
     */
    def measure(): Int

    /**
     * То же, но характеристика измеряется в квадрате 3х3 с центорм в позиции агента. Шум больше
     * @return
     */
    def measureWide(): WideMeasure
  }

  trait TripComputer extends EventEmitter {
    val id: Int

    /**
     * Отправить сообщение в среду.
     */
    def send[T](message: Message[T])

    /**
     * Получить все сообщения, которые поймал приемник
     * @return
     */
    def getAllReceived: Iterable[Message[_]]

    def update(env: EnvPart)

    /**
     * Получить сообщения. предназначенные агенту
     * @return
     */
    final def getMyReceived = getAllReceived filter (_.receiverId == id)

    /**
     * Дать команду компьютеру создать TimerEvent через `clock` тактов. См. [[mas.simulator.agent.event.Events]]
     */
    def startTimer(clock: Int, msg: String)
  }
}
