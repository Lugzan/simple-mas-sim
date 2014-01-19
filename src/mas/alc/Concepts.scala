package mas.alc

import mas.alc.Roles.Role

/**
 * User: Hasp
 */
object Concepts {
  trait Concept {
    def intersection(another: Concept): Concept = AndConcept(this, another)
    def union(another: Concept): Concept = OrConcept(this, another)
    def complement(): Concept = NotConcept(this)

    def forAll(role: Role): Concept = AllConcept(role, this)
    def exists(role: Role): Concept = ExistsConcept(role, this)

    def underlying(): Array[Concept] = Array.empty[Concept]
  }

  case class AtomicConcept(name: String) extends Concept

  case class OrConcept(left: Concept, right: Concept) extends Concept {
    override def underlying(): Array[Concept] = Array(left, right)
  }

  case class AndConcept(left: Concept, right: Concept) extends Concept {
    override def underlying(): Array[Concept] = Array(left, right)
  }

  case class NotConcept(neg: Concept) extends Concept {
    override def underlying(): Array[Concept] = Array(neg)
  }

  case class AllConcept(role: Role, right: Concept) extends Concept {
    override def underlying(): Array[Concept] = Array(right)
  }

  case class ExistsConcept(role: Role, right: Concept) extends Concept {
    override def underlying(): Array[Concept] = Array(right)
  }

  object Bottom extends Concept {
    override def intersection(another: Concept): Concept = Bottom
    override def union(another: Concept): Concept = another
    override def complement(): Concept = Top
    override def exists(role: Role): Concept = Bottom
  }

  object Top extends Concept {
    override def intersection(another: Concept): Concept = another
    override def union(another: Concept): Concept = Top
    override def complement(): Concept = Bottom
    override def forAll(role: Role): Concept = Top
  }

  implicit def name2Concept(name: String): Concept = AtomicConcept(name)
}
