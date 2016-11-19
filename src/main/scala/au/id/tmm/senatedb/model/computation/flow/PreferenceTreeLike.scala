package au.id.tmm.senatedb.model.computation.flow

trait PreferenceTreeLike[A] {

  checkChildKeysUnique()

  protected def children: Set[PreferenceTree[A]]

  protected def tally: Int

  private val lookupChildByKey: Map[A, PreferenceTree[A]] = children.map(tree => tree.key -> tree).toMap

  def isEmpty: Boolean = children.isEmpty && tally == 0

  protected def sumChildrenWith(that: PreferenceTreeLike[A]): Set[PreferenceTree[A]] = {
    def newChildForKey(key: A): PreferenceTree[A] = {
      Stream(
        this.lookupChildByKey.get(key),
        that.lookupChildByKey.get(key)
      ).flatten
        .reduce(_ + _)
    }

    val allNewChildKeys = this.lookupChildByKey.keySet ++ that.lookupChildByKey.keySet

    val newChildren = allNewChildKeys.map(newChildForKey)

    newChildren
  }

  protected def sumTallyWith(that: PreferenceTreeLike[A]): Int = this.tally + that.tally

  protected def childAtPath(path: Vector[A]): Option[PreferenceTree[A]] = {
    if (path.isEmpty) {
      None
    } else {
      val childMatchingPath = lookupChildByKey.get(path.head)

      if (childMatchingPath.isEmpty) {
        Some(PreferenceTree.emptyFor(path.head))
      } else {
        Some(childMatchingPath.get.treeAt(path.tail))
      }
    }
  }

  protected def childrenPrunedWhere(p: PreferenceTree[A] => Boolean): Set[PreferenceTree[A]] =
    children.filterNot(p).map(_.pruneWhere(p))

  private def checkChildKeysUnique() = {
    val allChildKeys = children.toStream.map(_.key).toVector

    val numTimesKeyOccursInChildren: Map[A, Int] = allChildKeys.groupBy(k => k).mapValues(_.size)

    val duplicatedChildKeys = numTimesKeyOccursInChildren.collect {
      case (childKey, countInChildren) if countInChildren > 1 => childKey
    }.toSet

    require(duplicatedChildKeys.isEmpty, s"Children contain duplicate keys $duplicatedChildKeys")
  }
}

