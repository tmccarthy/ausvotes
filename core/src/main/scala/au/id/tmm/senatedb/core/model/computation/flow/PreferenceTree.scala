package au.id.tmm.senatedb.core.model.computation.flow

import scala.collection.mutable

final case class PreferenceTree[A](key: A,
                                   tally: Int,
                                   children: Set[PreferenceTree[A]]) extends PreferenceTreeLike[A] {

  checkTalliesConsistent()
  checkChildKeysNotThisKey()

  def +(that: PreferenceTree[A]): PreferenceTree[A] = {
    require(this.key == that.key)

    if (that.isEmpty) return this
    if (this.isEmpty) return that

    val newTally = this sumTallyWith that
    val newChildren = this sumChildrenWith that

    PreferenceTree(key, newTally, newChildren)
  }

  def treeAt(path: Vector[A]): PreferenceTree[A] = childAtPath(path).getOrElse(this)

  def pruneWhere(condition: PreferenceTree[A] => Boolean): PreferenceTree[A] =
    this.copy(children = childrenPrunedWhere(condition))

  private def checkTalliesConsistent() = require(tally >= children.toStream.map(_.tally).sum,
    "Tally is less than the sum of child tallies")

  private def checkChildKeysNotThisKey() = require(!children.exists(_.key == key), "Children contain key of parent")
}

object PreferenceTree {

  final class Builder[A] private (var key: Option[A] = None) extends mutable.Builder[Vector[A], PreferenceTree[A]] {

    private var tally = 0

    private val children = mutable.Map[A, Builder[A]]()

    override def +=(keysToAdd: Vector[A]): this.type = {

      if (keysToAdd.isEmpty) return this

      key match {
        case None => key = Some(keysToAdd.head)
        case Some(_) => require(key contains keysToAdd.head)
      }

      tally += 1

      if (keysToAdd.size > 1) {
        childBuilderFor(keysToAdd(1)) += keysToAdd.drop(1)
      }

      this
    }

    private def childBuilderFor(key: A): Builder[A] = {
      if (children.contains(key)) {
        children(key)
      } else {
        val newBuilder = new Builder(Some(key))

        children.put(key, newBuilder)

        newBuilder
      }
    }

    override def clear(): Unit = {
      children.clear()
      tally = 0
      key = None
    }

    override def result(): PreferenceTree[A] = {
      if (key.isDefined) {
        val builtChildren = children.values.map(_.result()).toSet

        PreferenceTree(key.get, tally, builtChildren)
      } else {
        throw new IllegalStateException("Must provide at least one set of keys.")
      }
    }
  }

  object Builder {
    def apply[A](): Builder[A] = new Builder[A]()
  }

  def emptyFor[A](key: A): PreferenceTree[A] = PreferenceTree(key, 0, Set())

}