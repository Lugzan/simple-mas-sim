package mas.alc

import mas.alc.Concepts.Concept

/**
 * User: Hasp
 */
object Roles {
  trait Role {
    def name: String
    def sat(c1: Concept, c2: Concept): Boolean
  }

  class AtomicRole(id: String, fun: (Concept, Concept) => Boolean) extends Role {
    def name = id

    override def sat(c1: Concept, c2: Concept): Boolean = fun(c1, c2)
  }
}
