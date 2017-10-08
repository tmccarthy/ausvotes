package au.id.tmm.ausvotes.api.persistence.daos

import scalikejdbc.{TypeBinder, WrappedResultSet}

object ResultSetMocking {

  def mockWrappedResultSets(columnNames: Vector[String])(values: Product*): Vector[WrappedResultSet] = {
    values
      .zipWithIndex
      .map { case (tuple, index) =>
        DummyWrappedResultSet(index, columnNames)(tuple)
      }
      .toVector
  }

  private final class DummyWrappedResultSet(override val index: Int, columnNames: Seq[String])
                                                (values: Product) extends WrappedResultSet(underlying = null, cursor = null, index) {
    private val asVector = values.productIterator.toVector

    require(columnNames.size == asVector.size)

    private val asMap = (columnNames zip asVector).toMap

    override def get[A](columnIndex: Int)(implicit evidence$1: TypeBinder[A]): A = asVector(columnIndex).asInstanceOf[A]

    override def get[A](columnLabel: String)(implicit evidence$2: TypeBinder[A]): A = asMap(columnLabel).asInstanceOf[A]
  }

  object DummyWrappedResultSet {
    def apply(index: Int, columnNames: Seq[String])(values: Product): WrappedResultSet =
      new DummyWrappedResultSet(index, columnNames)(values)
  }
}
