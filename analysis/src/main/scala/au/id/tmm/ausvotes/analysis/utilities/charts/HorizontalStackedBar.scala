package au.id.tmm.ausvotes.analysis.utilities.charts

import au.id.tmm.ausvotes.analysis.models.TupleGroupInstances._
import au.id.tmm.ausvotes.analysis.utilities.data_processing.Aggregations.AggregationOps
import au.id.tmm.ausvotes.analysis.utilities.data_processing.Joins
import cats.Monoid
import plotly.element.{Color, Marker, Orientation}
import plotly.layout.{Axis, BarMode, Font, Layout}
import plotly.{Bar, Plotly}

import scala.collection.MapView

object HorizontalStackedBar {

  def make[T_Y_AXIS_KEY, T_ACTIVE_KEY, T_ACTIVE_COUNT : Monoid : Ordering, T_INACTIVE_COUNT : Monoid]
  (
    chartTitle: String,
    chartDiv: String,
    xAxisTitle: String,
    yAxisTitle: String,

    yAxisKeyName: T_Y_AXIS_KEY => String,
    activeKeyName: T_ACTIVE_KEY => String,
    inactiveKeyName: String,

    activeCountToDouble: T_ACTIVE_COUNT => Double,
    inactiveCountToDouble: T_INACTIVE_COUNT => Double,

    activeTallies: Map[(T_Y_AXIS_KEY, T_ACTIVE_KEY), T_ACTIVE_COUNT],
    inactiveTallies: Map[(T_Y_AXIS_KEY, T_ACTIVE_KEY), T_INACTIVE_COUNT],

    allActiveKeys: Set[T_ACTIVE_KEY],

    yAxisOrdering: XAxisOrdering[T_Y_AXIS_KEY],
    activeKeyOrdering: Ordering[T_ACTIVE_KEY],

    colourForActiveKey: T_ACTIVE_KEY => Option[Color] = (_: T_ACTIVE_KEY) => None,
    colourForInactiveKey: Option[Color] = None,

    height: Option[Int] = None,
    yAxisTickFontSize: Option[Int] = None,
  ): Unit = {

    val unorderedUnAggregatedTallies: List[((T_Y_AXIS_KEY, T_ACTIVE_KEY), (T_ACTIVE_COUNT, T_INACTIVE_COUNT))] = Joins.innerJoin(
      left = activeTallies,
      right = inactiveTallies,
    )(
      { case ((yAxis, activeKey), _) => (yAxis, activeKey) },
      { case ((yAxis, activeKey), _) => (yAxis, activeKey) },
    ).map { case ((yAxisKey, activeKey), (_, activeCount), (_, inactiveCount)) => (yAxisKey, activeKey) -> (activeCount, inactiveCount) }

    val activeTalliesMap: Map[T_Y_AXIS_KEY, Map[T_ACTIVE_KEY, T_ACTIVE_COUNT]] =
      unorderedUnAggregatedTallies
        .groupByAndAggregate { case (k, v) => k } { case (k, v) => v }
        .groupBy { case ((yAxisKey, activeKey), v) => yAxisKey }
        .map { case (yAxisKey, values) => yAxisKey -> values.map { case ((_, activeKey), (activeCount, _)) => activeKey -> activeCount }}

    val activeCountsPerYAxisKey: MapView[T_Y_AXIS_KEY, T_ACTIVE_COUNT] =
      activeTalliesMap.view.mapValues(activeCountPerKey => Monoid[T_ACTIVE_COUNT].combineAll(activeCountPerKey.values))

    val inactiveTalliesMap: Map[T_Y_AXIS_KEY, T_INACTIVE_COUNT] =
      unorderedUnAggregatedTallies
        .groupByAndAggregate { case ((yAxisKey, _), _) => yAxisKey } {
          case ((yAxisKey, _), (_, inactiveCount)) => inactiveCount
        }

    val allYAxisKeys = activeTalliesMap.keys.toList

    val yAxisKeysInOrder = yAxisOrdering match {
      case XAxisOrdering.AccordingTo(ordering) => allYAxisKeys.sorted(ordering)
      case XAxisOrdering.ByActiveCount => {

        allYAxisKeys.sortBy(activeCountsPerYAxisKey)
      }
    }

    val activeTraces = allActiveKeys.toList.sorted(activeKeyOrdering).map { activeKey =>
      Bar(
        x = yAxisKeysInOrder.map { yAxisKey => activeTalliesMap(yAxisKey).getOrElse(activeKey, Monoid[T_ACTIVE_COUNT].empty) }.map(activeCountToDouble),
        y = yAxisKeysInOrder.map { yAxisKey => yAxisKeyName(yAxisKey) },
        orientation = Orientation.Horizontal,
        marker = colourForActiveKey(activeKey).map { colour => Marker(color = colour) }.orNull,
        name = activeKeyName(activeKey),
      )
    }

    val inactiveTrace = Bar(
      x = yAxisKeysInOrder.map { yAxisKey => inactiveTalliesMap.getOrElse(yAxisKey, Monoid[T_INACTIVE_COUNT].empty) }.map(inactiveCountToDouble),
      y = yAxisKeysInOrder.map { yAxisKey => yAxisKeyName(yAxisKey) },
      orientation = Orientation.Horizontal,
      marker = colourForInactiveKey.map { colour => Marker(color = colour) }.orNull,
      name = inactiveKeyName,
    )

    val traces = activeTraces :+ inactiveTrace

    val layout = Layout(
      title = chartTitle,
      xaxis = Axis(
        title = xAxisTitle,
      ),
      yaxis = Axis(
        title = yAxisTitle,
        automargin = true,
        tickfont = yAxisTickFontSize.map(i => Font(size = Int.box(i))).orNull,
      ),
      barmode = BarMode.Stack,
      autosize = true,
      showlegend = true,
      height = height.map(Int.box).orNull,
    )

    println(Plotly.jsSnippet(chartDiv, traces, layout))
    Plotly.plot(s"/tmp/$chartDiv", traces, layout)
  }

  sealed trait XAxisOrdering[+A]

  object XAxisOrdering {
    final case class AccordingTo[A](ordering: Ordering[A]) extends XAxisOrdering[Nothing]
    case object ByActiveCount extends XAxisOrdering[Nothing]
  }

}
