package mas.simulator.env.impl

/**
 * User: Lugzan
 */
trait Noise {
  def noise(p: (Int, Int)): Int
  def areaNoise(center: (Int, Int)): Int
}
