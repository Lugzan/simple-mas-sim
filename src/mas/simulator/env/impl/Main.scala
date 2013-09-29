package mas.simulator.env.impl


/**
 * User: Lugzan
 */
object Main {
  def main(args: Array[String]) {
    val s = new Starter(3, println, 50, 100, 3)
    s.go()
  }
}
