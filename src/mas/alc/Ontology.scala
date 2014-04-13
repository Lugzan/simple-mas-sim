package mas.alc

import mas.alc.Concepts.Concept

/**
 * User: Lugzan
 */
class Ontology {
  val abox = ABox.newMutable
  val tbox = TBox.newEmpty

  def prove(concept: Concept) = TableRun.inferRun(concept, tbox, abox)
}
