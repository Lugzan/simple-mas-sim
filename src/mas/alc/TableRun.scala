package mas.alc

import mas.alc.Concepts._
import scala.util.Random
import mas.alc.Concepts.ExistsConcept
import mas.alc.Concepts.AndConcept
import mas.alc.Concepts.OrConcept
import mas.alc.ABox.RoleTenancy
import mas.alc.Individuals.Individual
import mas.alc.ABox.ConceptTenancy
import mas.alc.TBox.{Inclusion, Definition}
import scala.collection.mutable

/**
 * User: Hasp
 */
object TableRun {
  private val RULES = Array(new CapRule, new CupRule, new ExistsRule, new StatefulForAllRule)

  private trait Rule {
    def canBeApplied(ind: Individual,  concept: Concept, abox: ABox): Boolean
    def apply(ind: Individual, concept: Concept, abox: ABox): Either[ABox, (ABox, ABox)]

    final def applyIfValid(ind: Individual, concept: Concept, abox: ABox): Option[Either[ABox, (ABox, ABox)]] =
      if (canBeApplied(ind, concept, abox)) Some(apply(ind, concept, abox)) else None
  }

  private class CapRule extends Rule {
    def canBeApplied(ind: Individual, concept: Concept, abox: ABox): Boolean = concept match {
      case and@AndConcept(left, right) =>
        abox.check(ConceptTenancy(ind, and)) && (!abox.check(ConceptTenancy(ind, left)) || !abox.check(ConceptTenancy(ind, right)))
      case _ => false
    }

    def apply(ind: Individual, concept: Concept, abox: ABox) = concept match {
      case and@AndConcept(left, right) => Left(abox addFact ConceptTenancy(ind, left) addFact ConceptTenancy(ind, right))
      case _ => Left(abox)
    }
  }

  private class CupRule extends Rule {
    def canBeApplied(ind: Individual, concept: Concept, abox: ABox) = concept match {
      case or@OrConcept(left, right) =>
        abox.check(ConceptTenancy(ind, or)) && (!abox.check(ConceptTenancy(ind, left)) || !abox.check(ConceptTenancy(ind, right)))
      case _ => false
    }

    def apply(ind: Individual, concept: Concept, abox: ABox) = concept match {
      case or@OrConcept(left, right) => Right((abox addFact ConceptTenancy(ind, left), abox addFact ConceptTenancy(ind, right)))
      case _ => Left(abox)
    }
  }

  private class ExistsRule extends Rule {
    def canBeApplied(ind: Individual, concept: Concept, abox: ABox) = concept match {
      case ExistsConcept(role, right) => !abox.projection(ind, role).exists {
        case a => abox.check(ConceptTenancy(a, right))
      }
      case _ => false
    }

    def apply(ind: Individual, concept: Concept, abox: ABox) = concept match {
      case ExistsConcept(role, right) =>
        Left(abox addFact ConceptTenancy(ind, right) addFact RoleTenancy(Individual(createName()), ind, role))
      case _ => Left(abox)
    }
  }

  private class StatelessForAllRule extends Rule {
    def canBeApplied(ind: Individual, concept: Concept, abox: ABox) = concept match {
      case AllConcept(role, right) => abox.projection(ind, role) exists {
        case a => !abox.check(ConceptTenancy(a, right))
      }
      case _ => false
    }

    def apply(ind: Individual, concept: Concept, abox: ABox) = concept match {
      case AllConcept(role, right) => Left {
        abox.projection(ind, role) find {
          case a => !abox.check(ConceptTenancy(a, right))
        } map {
          case y => abox addFact ConceptTenancy(y, right)
        } getOrElse abox
      }
      case _ => Left(abox)
    }
  }

  private class StatefulForAllRule extends Rule {
    private var y: Option[ConceptTenancy] = None

    def canBeApplied(ind: Individual, concept: Concept, abox: ABox) = concept match {
      case AllConcept(role, right) =>
        abox.projection(ind, role) find {
          case a => !abox.check(ConceptTenancy(a, right))
        } map {
          case yc => y = Some(ConceptTenancy(yc, right))
        }

        y.isDefined
      case _ => false
    }

    def apply(ind: Individual, concept: Concept, abox: ABox) =  Left(y map (yc => abox addFact yc) getOrElse abox)
  }


  def inferWithoutTBox(concept: Concept): Boolean = {
    val x = Individual("x")

    def processABox(aboxChild: ABox) = aboxChild.consistentByConcept() && aboxChild.getLast.exists {
      case ConceptTenancy(ind, c) => inferInner(ind, c, aboxChild)
    }

    def inferInner(start: Individual, concept: Concept, abox: ABox): Boolean = {
      RULES exists {
        case rule => rule.applyIfValid(start, concept, abox) match {
          case Some(Left(aboxChild)) => processABox(aboxChild)
          case Some(Right((aboxChildLeft, aboxChildRight))) => processABox(aboxChildLeft) || processABox(aboxChildRight)
          case None => true
        }
      }
    }

    inferInner(x, concept, ABox.newImmutable)
  }

  def inferRun(concept: Concept, tbox: TBox, abox: ABox) = {
    val x = Individual("x")

    def processABox(aboxChild: ABox) = aboxChild.consistentByConcept() && aboxChild.getLast.exists {
      case ConceptTenancy(ind, c) => inferInner(ind, c, aboxChild)
    }

    def inferInner(start: Individual, concept: Concept, abox0: ABox): Boolean = {
      RULES exists {
        case rule => rule.applyIfValid(start, concept, abox0) match {
          case Some(Left(aboxChild)) => processABox(aboxChild)
          case Some(Right((aboxChildLeft, aboxChildRight))) => processABox(aboxChildLeft) || processABox(aboxChildRight)
          case None =>
            val toReplace = new mutable.HashMap[Concept, Concept]()

            def recursiveUpdate(l: Concept, r: Concept) {
              toReplace.put(l, r) foreach(e => recursiveUpdate(e, r))
            }

            tbox.getAll foreach {
              case Definition(Top, right) => if (abox0.reverseFind(NotConcept(right)).isDefined) return false
              case Definition(Bottom, right) => if (abox0.reverseFind(right).isDefined) return false
              case Definition(left, right) => recursiveUpdate(left, right)
              case Inclusion(_, _) =>
            }

            true
        }
      }
    }

    inferInner(x, concept, ABox newImmutableFrom abox)
  }

  private def createName() = "x" +  Random.nextString(15) // hope it won't clash
}
