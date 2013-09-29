package mas.simulator.env.impl

import scala.util.Random

/**
 * User: Lugzan
 */
class SimpleNoiseImpl(level: Int) extends Noise {
  def noise(p: (Int, Int)) = Random.nextInt(level) - Random.nextInt(level)

  def areaNoise(center: (Int, Int)) =
    Random.nextInt(level)*Random.nextInt(level) - Random.nextInt(level)*Random.nextInt(level)
}
