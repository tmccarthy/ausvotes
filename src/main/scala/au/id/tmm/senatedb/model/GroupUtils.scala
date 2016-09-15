package au.id.tmm.senatedb.model

object GroupUtils {
  val UNGROUPED = "UG"

  // TODO this should be cached
  def indexOfGroup(group: String): Int = {
    if (group == UNGROUPED) {
      return Int.MaxValue
    }

    def charValue(char: Char) = char.toUpper - 'A'

    if (group.length == 1) {
      charValue(group.charAt(0))
    } else {
      (26 * (1 + charValue(group.charAt(0)))) + charValue(group.charAt(1))
    }
  }

  val groupOrdering: Ordering[String] = new Ordering[String] {
    override def compare(left: String, right: String): Int = indexOfGroup(left) compare indexOfGroup(right)
  }
}
