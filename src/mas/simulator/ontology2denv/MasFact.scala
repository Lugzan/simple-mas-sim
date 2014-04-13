package mas.simulator.ontology2denv

import mas.alc.Concepts.Concept
import mas.alc.Roles.Role

/**
 * User: Lugzan
 */
class MasFact[T](t: T) {
  def getIndividual = t.toString
}

case class ConceptFact[T](t: T, concept: Concept) extends MasFact(t)

case class RoleFact[T](t: T, role: Role) extends MasFact(t)
