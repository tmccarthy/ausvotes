package au.id.tmm.ausvotes.analysis.utilities.themes

import au.id.tmm.ausvotes.analysis.models.PartyGroup
import plotly.element.Color

object PlotlyTheme {

  val primaryColour = Color.StringColor("#ac4142")

  def colourFor(partyGroup: PartyGroup): Color = partyGroup match {
    case PartyGroup.Coalition => Color.StringColor("#1c4f9c")
    case PartyGroup.Labor => Color.StringColor("#e53440")
    case PartyGroup.Greens => Color.StringColor("#00953d")
    case PartyGroup.OneNation => Color.StringColor("#f36d24")
    case PartyGroup.Other => Color.StringColor("#66665d")
  }

  val inactiveColour = Color.StringColor("LightGray")

}
