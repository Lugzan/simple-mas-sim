package mas.simulator.env

/**
 * User: Lugzan
 */
case class Message[T](data: T, senderId: Int, receiverId: Int)
