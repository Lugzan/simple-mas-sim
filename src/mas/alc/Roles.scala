package mas.alc

import mas.alc.Concepts.Concept

/**
 * User: Hasp
 */
object Roles {
  trait Role {
    def sat(c1: Concept, c2: Concept): Boolean
  }
}
