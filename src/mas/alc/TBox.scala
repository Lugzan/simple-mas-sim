package mas.alc

import mas.alc.Concepts.{AtomicConcept, Concept}
import mas.alc.TBox.Axiom
import scala.collection.mutable
import scala.collection.immutable.Stream

/**
 * User: Hasp
 */
sealed trait TBox {
  sealed abstract class AddResult
  sealed case class Ok() extends AddResult
  sealed case class WillBeCyclicFail() extends AddResult
  sealed case class AlreadyExistsFail(sameLeftPart: Axiom) extends AddResult

  def add(axiom: Axiom): AddResult
  def delete(axiom: Axiom): Boolean

  def isDefinitionSystem: Boolean

  /**
   * If isDefinitionSystem == false result is undefined
   */
  def isBasic(concept: Concept): Boolean

  def isFeasible(concept: Concept): Boolean
}

object TBox {
  sealed trait Axiom {
    val left: Concept
    val right: Concept
  }
  sealed case class Definition(left: Concept, right: Concept) extends Axiom
  sealed case class Inclusion(left: Concept, right: Concept) extends Axiom

  implicit def pair2Definition(cs: (Concept, Concept)): Axiom = Definition(cs._1, cs._2)

  private class TBoxImpl extends TBox {
    private var defs = true

    private val myAxioms                = mutable.HashMap[Axiom, (Array[AtomicConcept], Array[AtomicConcept])]()
    private val myDefinedAtomicConcepts = mutable.HashMap[AtomicConcept, Int]()
    private val myBasicAtomicConcepts   = mutable.HashMap[AtomicConcept, Int]()

    private val straightImage = mutable.HashMap[AtomicConcept, mutable.HashMap[AtomicConcept, Int]]()
    private val reverseImage = mutable.HashMap[AtomicConcept, mutable.HashMap[AtomicConcept, Int]]()

    private def checkBecomeCyclic(from: AtomicConcept, to: Array[AtomicConcept]): Boolean = to exists {
      case c => reverseImage get c exists {
        case cs => cs contains from
      }
    }

    private def addGraphEdges(from: AtomicConcept, to: Array[AtomicConcept]) {
      def initConceptMap(cs: Array[AtomicConcept]): mutable.HashMap[AtomicConcept, Int] =
        mutable.HashMap(to.zip(Stream.continually(1)): _*)

      def addToMap(mp: mutable.HashMap[AtomicConcept, Int], what: AtomicConcept) {
        mp get what map {
          case i => mp.put(what, i + 1)
        } getOrElse mp.put(what, 1)
      }

      to foreach {
        case c => reverseImage get c map {
          case cs => addToMap(cs, from)
        } getOrElse reverseImage.put(c, mutable.HashMap(from -> 1))
      }

      to foreach { //don't merge with prev
        case c => straightImage get c map {
          case cs => cs foreach {
            case (updated, _) => reverseImage get updated map {
              case ucs => addToMap(ucs, from)
            } getOrElse {
              reverseImage.put(updated, mutable.HashMap(from -> 1))
            }
          }
        }
      }

      straightImage get from map {
        case cs => to foreach (addToMap(cs, _))
      } getOrElse straightImage.put(from, initConceptMap(to))

      reverseImage get from map {
        case cs => cs foreach {
          case (c, _) => straightImage get c map {
            case updated => to foreach (addToMap(updated, _))
          } getOrElse {
            straightImage.put(c, initConceptMap(to))
          }
        }
      }
    }

    def add(axiom: Axiom): AddResult = {
      val left = axiom.left

      val clash = myAxioms find {
        case (deff: Definition, _) => deff.left == left
        case _ => false
      }
      
      if (clash.isDefined) return AlreadyExistsFail(clash.get._1)

      val decomposedRight: Array[AtomicConcept] = decompose(axiom.right)

      (axiom, left) match {
        case (_: Definition, atomic: AtomicConcept) =>
          if (!checkBecomeCyclic(atomic, decomposedRight)) {
            addGraphEdges(atomic, decomposedRight)
          } else return WillBeCyclicFail()
        case _ =>
      }

      val decomposedLeft: Array[AtomicConcept] = decompose(left)

      axiom match {
        case _: Definition =>
        case _ => defs = false
      }

      def introduce(concepts: Array[AtomicConcept], storage: mutable.HashMap[AtomicConcept, Int]) {
        concepts foreach {
          case c => storage get c map {
            case count: Int => storage.put(c, count + 1)
          } getOrElse {
            storage.put(c, 1)
          }
        }
      }

      introduce(decomposedLeft, myDefinedAtomicConcepts)
      introduce(decomposedRight filter (r => !myDefinedAtomicConcepts.get(r).isDefined), myBasicAtomicConcepts)


      myAxioms.put(axiom, (decomposedLeft, decomposedRight))

      Ok()
    }

    def delete(axiom: Axiom): Boolean = myAxioms get axiom exists {
      case (decomposedLeft, decomposedRight) =>
        def clearConcepts(cs: Array[AtomicConcept], concepts: mutable.HashMap[AtomicConcept, Int]) {
          cs foreach {
            case c => concepts get c map {
              case count => if (count == 1) concepts.remove(c) else concepts.put(c, count - 1)
            }
          }
        }

        clearConcepts(decomposedLeft, myDefinedAtomicConcepts)
        clearConcepts(decomposedRight, myBasicAtomicConcepts)

        (axiom, axiom.left) match {
          case (_: Definition, atomic: AtomicConcept) =>
            def deleteFrom(from: mutable.HashMap[AtomicConcept, Int], what: AtomicConcept): Option[AtomicConcept] =
              from get what flatMap {
                case count => if (count > 1) {
                  from.put(what, count - 1)
                  None
                } else {
                  from remove what
                  Some(what)
                }
              }

            def recursiveDeleteFrom(start: Array[AtomicConcept],
                                    from : mutable.HashMap[AtomicConcept, Int],
                                    what: AtomicConcept, extractor: AtomicConcept => Option[mutable.HashMap[AtomicConcept, Int]]) {
              val next = start map (deleteFrom(from, _)) collect { case Some(a) => a }

              next foreach { case c => extractor(c) map ( l => recursiveDeleteFrom(next, l, what, extractor) ) }
            }

            straightImage get atomic foreach {
              case cs => recursiveDeleteFrom(decomposedRight, cs, atomic, (l: AtomicConcept) => straightImage get l)
            }

            decomposedRight foreach {
              case c =>
                reverseImage get c map {
                  case cs => recursiveDeleteFrom(Array(atomic), cs, c, (l: AtomicConcept) => reverseImage get l)
                }
            }
          case _ =>
        }

        myAxioms remove axiom

        true
    }

    def isDefinitionSystem = defs

    /**
     * If isDefinitionSystem == false result is undefined
     */
    def isBasic(concept: Concept) = concept match {
      case atomic: AtomicConcept => !myDefinedAtomicConcepts.get(atomic).isDefined
      case _ => false
    }

    private def decompose(concept: Concept): Array[AtomicConcept] = {
      val result = mutable.HashSet[AtomicConcept]()

      def decomposeInner(c: Concept) {
        c match {
          case atomic: AtomicConcept => result add atomic
          case another => another.underlying() foreach decomposeInner
        }
      }

      result.toArray
    }

    def isFeasible(concept: Concept): Boolean = {
      def normalize(c: Concept): Concept = c

      false
    }
  }
}
