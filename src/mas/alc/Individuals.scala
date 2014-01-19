package mas.alc

import scala.collection.mutable

/**
 * User: Hasp
 */
object Individuals {
  case class Individual(name: String) {
    override def equals(obj: scala.Any) = obj match {
      case another: Individual => another.name == name
      case _ => false
    }

    override def hashCode() = name.hashCode
  }

  class InSet private () {
    private var names = mutable.HashSet[Individual]()

    def this(names: Iterable[String]) {
      this()
      this.names ++= names.map(Individual)
    }

    def add(ind: Individual) = names add ind

    def remove(ind: Individual) = names remove ind

    def contains(ind: Individual) = names contains ind

    def getAll = names.toSeq

    private[alc] def unsafeGet = names
  }

  implicit def name2Individual(name: String): Individual = Individual(name)
}
