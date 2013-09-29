package mas.simulator.env

/**
 * User: Lugzan
 */
case class WideMeasure(a: (Int, Int, Int), b: (Int, Int, Int), c: (Int, Int, Int))

class MeasureBuilder {
  var i = 0
  val ms = new Array[Int](9)

  def add(m: Int) {
    if (i < 9) {
      ms(i) = m
      i += 1
    }
  }

  def get = WideMeasure((ms(0), ms(1), ms(2)),(ms(3), ms(4), ms(5)),(ms(6), ms(7), ms(8)))
}

object WideMeasure {
  def dumb = WideMeasure((-1, -1, -1),(-1, -1, -1),(-1, -1, -1))
}