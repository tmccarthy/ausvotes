package au.id.tmm.ausvotes.analysis.utilities.rendering

import au.id.tmm.ausvotes.analysis.models.{PartyGroup, ValueTypes}
import au.id.tmm.ausgeo.State

trait MarkdownRenderable[-A] {

  def render(a: A): String

}

object MarkdownRenderable {

  def render[A : MarkdownRenderable](a: A): String = implicitly[MarkdownRenderable[A]].render(a)

  implicit val forString: MarkdownRenderable[String] = s => s
  implicit val forDouble: MarkdownRenderable[Double] = d => d.formatted("%.2f")
  implicit val forInt: MarkdownRenderable[Int] = i => i.formatted("%,d")

  implicit val forPartyGroup: MarkdownRenderable[PartyGroup] = p => p.name
  implicit val forState: MarkdownRenderable[State] = s => s.name

  implicit val forVotedFormally: MarkdownRenderable[ValueTypes.VotedFormally] = c => forInt.render(c.asInt)
  implicit val forNominalUsedHtv: MarkdownRenderable[ValueTypes.UsedHtv.Nominal] = c => forInt.render(c.asInt)
  implicit val forPercentUsedHtv: MarkdownRenderable[ValueTypes.UsedHtv.Percentage] = c => forDouble.render(c.asDouble) + "%"

}
