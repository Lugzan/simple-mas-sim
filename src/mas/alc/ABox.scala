package mas.alc

import mas.alc.Concepts.{Bottom, Concept}
import mas.alc.Roles.Role
import mas.alc.ABox.Fact
import scala.collection.mutable
import scala.collection.immutable
import mas.alc.Concepts.AtomicConcept
import mas.alc.Concepts.NotConcept
import mas.alc.ABox.RoleTenancy
import mas.alc.Individuals.Individual
import mas.alc.ABox.ConceptTenancy
import scala.Some

/**
 * User: Hasp
 */
trait ABox {
  protected var last: Option[Fact] = None

  def addFact(fact: Fact) = {
    last = Some(fact)
    addFactImpl(fact)
  }

  def removeFact(fact: Fact) = {
    if (last exists (_ == fact)) last = None
    removeFactImpl(fact)
  }

  def getLabelSet(ind: Individual): Iterable[Concept] = getAll collect {
    case ConceptTenancy(i, c) if i == ind => c
  }

  def getLast = last

  def check(fact: Fact): Boolean

  def getPoints: Set[Individual]

  def projection(ind: Individual, role: Role) = getAll collect {
    case RoleTenancy(left, right, role1) if role1 == role && left == ind => right
    case RoleTenancy(left, right, role1) if role1 == role && right == ind => left
  }

  def consistentByConcept(): Boolean = !getAll.exists{
    case ConceptTenancy(_, Bottom) => true
    case ConceptTenancy(ind, AtomicConcept(name)) => check(ConceptTenancy(ind, NotConcept(AtomicConcept(name))))
  }

  def reverseFind(concept: Concept) = getAll find {
    case ConceptTenancy(_, concept1) if concept == concept1 => true
    case _ => false
  }

  def reverseFindAll(concept: Concept) = getAll collect {
    case ConceptTenancy(a, concept1) if concept == concept1 => a
  }

  protected def getAll: Iterable[Fact]
  protected def addFactImpl(fact: Fact): ABox
  protected def removeFactImpl(fact: Fact): ABox
}

object ABox {
  def newMutable = new MutableABoxImpl
  def newImmutable = new ImmutableABoxImpl(immutable.HashSet.empty[Fact])
  def newImmutableFrom(abox: ABox) = new ImmutableABoxImpl(immutable.HashSet(abox.getAll.toSeq: _*))

  trait Fact

  case class ConceptTenancy(ind: Individual, concept: Concept) extends Fact
  case class RoleTenancy(left: Individual, right: Individual, role: Role) extends Fact

  trait MutableABox extends ABox
  trait ImmutableABox extends ABox

  private[alc] class MutableABoxImpl extends MutableABox {
    private val facts = mutable.HashSet[Fact]()

    protected def addFactImpl(fact: Fact) = {
      facts add fact
      this
    }

    protected def removeFactImpl(fact: Fact) = {
      facts remove fact
      this
    }

    def check(fact: Fact) = facts contains fact

    def getPoints = facts.flatMap {
      case ConceptTenancy(i, _) => List(i)
      case RoleTenancy(i, i0, _) => List(i, i0)
    }.toSet

    protected def getAll = facts
  }

  private[alc] case class ImmutableABoxImpl(facts: immutable.HashSet[Fact]) extends ImmutableABox {
    def check(fact: Fact) = facts contains fact

    def getPoints = facts.flatMap {
      case ConceptTenancy(i, _) => List(i)
      case RoleTenancy(i, i0, _) => List(i, i0)
    }

    protected def addFactImpl(fact: Fact) = ImmutableABoxImpl(facts + fact) withLast Some(fact)

    protected def removeFactImpl(fact: Fact) = ImmutableABoxImpl(facts - fact) withLast (last flatMap {case a if a != fact => last})

    protected def getAll = facts

    protected def withLast(fact: Option[Fact]) = {
      last = fact
      this
    }
  }
}
