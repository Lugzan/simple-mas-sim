package mas.loadbalancing

import java.util.Comparator

import scala.collection.mutable

/**
 * User: Dmitry.Naydanov
 * Date: 29.06.15.
 */
class EventQueue {
  private val events = new java.util.PriorityQueue[MyEventInfo](new EventComparator)

  def push(event: MyEventInfo) {
    events.add(event)
  }

  /**
   * @param time абсолютное время
   * @return
   */
  def poll(time: Double): Iterable[MyEventInfo] = {
    if (events.isEmpty) return Array.empty

    val res = mutable.ArrayBuilder.make[MyEventInfo]()
    while (!events.isEmpty && events.peek().myDelay <= time) res += events.poll()

    res.result()
  }

  def pollFirst(): MyEventInfo = events.poll()

  abstract class MyEventInfo {
    var isValid = true

    /**
     * Оценочное время выполнения события
     */
    def getTime: Double

    /**
     * Через сколько событие надо обработать
     */
    def myDelay: Double
  }

  private class EventComparator extends Comparator[MyEventInfo] {
    private val PRECISION = 0.0000000001

    override def compare(o1: MyEventInfo, o2: MyEventInfo): Int = {
      val e = o1.myDelay - o2.myDelay

      if (e < PRECISION) 0 else Math.round(Math.signum(e)).toInt
    }
  }
}
