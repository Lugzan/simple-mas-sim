package mas.simulator.ontology2denv

/**
 * Data format  upper-left-x|upper-left-y|right-lower-x|right-lower-y
 *
 * User: Lugzan
 */
class EnvArea private (val x1: Int, val y1: Int, val x2: Int, val y2: Int) {
  assert(y1 >= y2)
  assert(x1 <= x2)

  def isInside(point: (Int, Int)) = {
    val x = point._1
    val y = point._2

    x1 <= x && x <= x2 && y1 >= y && y >= y2
  }

  def isInside(area: EnvArea) = y1 >= area.y1 && y2 <= area.y2 && x1 <= area.x1 && x2 >= area.x2
}

object EnvArea {
  def from(x1: Int, y1: Int, x2: Int, y2: Int) = new EnvArea(x1, y1, x2, y2)
  def from(data: String) = {
    val parts = data split '|' map Integer.parseInt
    if (parts.length != 4) throw new IllegalArgumentException("Incorrect format: " + data)
    new EnvArea(parts(0), parts(1), parts(2), parts(3))
  }
  def from(a: (Int, Int), b: (Int, Int)) = new EnvArea(a._1, a._2, b._1, b._2)

  def to(area: EnvArea) = Array(area.x1, area.y1, area.x2, area.y2) mkString "|"
}
