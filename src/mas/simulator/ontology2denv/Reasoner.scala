package mas.simulator.ontology2denv

import mas.alc.Ontology
import mas.alc.Concepts.Concept
import mas.simulator.ontology2denv

/**
 * Data format  upper-left-x|upper-left-y|right-lower-x|right-lower-y
 *
 * User: Lugzan
 */
abstract class Reasoner {
  private val ontology = new Ontology

  def addData(ind: String, concept: Concept) = {
    val area = EnvArea.from(ind)
    ontology.abox.reverseFindAll(concept) filter {
      case i => area.isInside(EnvArea from i.name)
    }
  }
}
