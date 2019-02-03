package au.id.tmm.ausvotes.analysis.utilities.charts

import cats.Monoid
import plotly.element.{Color, Marker, Orientation}
import plotly.layout.{Axis, BarMode, Layout}
import plotly.{Bar, Plotly}

object VerticalGroupedBar {

  def make[T_GROUP, T_BAR, T_COUNT : Monoid]
  (
    chartTitle: String,
    chartDiv: String,
    xAxisTitle: Option[String],
    yAxisTitle: String,

    barName: T_BAR => String,
    groupName: T_GROUP => String,

    countToDouble: T_COUNT => Double,

    tallies: Map[T_GROUP, Map[T_BAR, T_COUNT]],

    allBarKeys: Set[T_BAR],

    groupOrdering: Ordering[T_GROUP],
    barOrdering: Ordering[T_BAR],

    colourForBar: T_BAR => Option[Color] = (_: T_BAR) => None,
  ): Unit = {

    val sortedGroups: List[T_GROUP] = tallies.keys.toList.sorted(groupOrdering)

    val traces = allBarKeys.toList.sorted(barOrdering).map { barGroup =>
      Bar(
        x = sortedGroups.map(groupName),
        y = sortedGroups.map { group => countToDouble(tallies.getOrElse(group, Map.empty).getOrElse(barGroup, Monoid[T_COUNT].empty)) },
        name = barName(barGroup),
        orientation = Orientation.Vertical,
        marker = colourForBar(barGroup).map(colour => Marker(color = colour)).orNull,
      )
    }

    val layout = Layout(
      title = chartTitle,
      xaxis = Axis(
        title = xAxisTitle.orNull,
        automargin = true,
      ),
      yaxis = Axis(
        title = yAxisTitle,
      ),
      barmode = BarMode.Group,
      autosize = true,
      showlegend = true,
    )

    println(Plotly.jsSnippet(chartDiv, traces, layout))
    Plotly.plot(s"/tmp/$chartDiv", traces, layout)
  }

}
