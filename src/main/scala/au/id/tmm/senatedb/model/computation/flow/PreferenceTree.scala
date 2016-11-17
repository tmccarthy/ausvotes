package au.id.tmm.senatedb.model.computation.flow

import scala.collection.mutable

final case class PreferenceTree[A](key: A,
                                   tally: Int,
                                   children: Set[PreferenceTree[A]]) {

  checkTalliesConsistent()
  checkChildKeysUnique()
  checkChildKeysNotThisKey()

  private val lookupChildByKey: Map[A, PreferenceTree[A]] = children.map(tree => tree.key -> tree).toMap

  def +(that: PreferenceTree[A]): PreferenceTree[A] = {
    require(this.key == that.key)

    if (that.isEmpty) return this
    if (this.isEmpty) return that

    def newChildForKey(key: A): PreferenceTree[A] = {
      Stream(
        this.lookupChildByKey.get(key),
        that.lookupChildByKey.get(key)
      ).flatten
        .reduce(_ + _)
    }

    val newTally = this.tally + that.tally

    val allNewChildKeys = this.lookupChildByKey.keySet ++ that.lookupChildByKey.keySet

    val newChildren = allNewChildKeys.map(newChildForKey)

    PreferenceTree(key, newTally, newChildren)
  }

  @scala.annotation.tailrec
  def treeAt(path: Vector[A]): PreferenceTree[A] = {
    if (path.isEmpty) {
      this
    } else {
      val childMatchingPath = lookupChildByKey.get(path.head)

      if (childMatchingPath.isEmpty) {
        PreferenceTree.emptyFor(path.head)
      } else {
        childMatchingPath.get.treeAt(path.tail)
      }
    }
  }

  def pruneWhere(condition: PreferenceTree[A] => Boolean): PreferenceTree[A] = {
    val newChildren = children.filterNot(condition).map(_.pruneWhere(condition))

    this.copy(children = newChildren)
  }

  def isEmpty: Boolean = children.isEmpty

  private def checkTalliesConsistent() = require(tally >= children.toStream.map(_.tally).sum,
    "Tally is less than the sum of child tallies")

  private def checkChildKeysUnique() = {
    val allChildKeys = children.toStream.map(_.key).toVector

    val numTimesKeyOccursInChildren: Map[A, Int] = allChildKeys.groupBy(k => k).mapValues(_.size)

    val duplicatedChildKeys = numTimesKeyOccursInChildren.collect {
      case (childKey, countInChildren) if countInChildren > 1 => childKey
    }.toSet

    require(duplicatedChildKeys.isEmpty, s"Children contain duplicate keys $duplicatedChildKeys")
  }

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

    override def clear(): Unit = children.clear()

    override def result(): PreferenceTree[A] = {
      val builtChildren = children.values.map(_.result()).toSet

      PreferenceTree(key.get, tally, builtChildren)
    }
  }

  object Builder {
    def apply[A](): Builder[A] = new Builder[A]()
  }

  def emptyFor[A](key: A): PreferenceTree[A] = PreferenceTree(key, 0, Set())

}