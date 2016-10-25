package au.id.tmm.senatedb.parsing.countdata

import au.id.tmm.senatedb.model.CountStep
import au.id.tmm.senatedb.model.CountStep.DistributionSource
import au.id.tmm.senatedb.model.parsing.Candidate

private[countdata] class DistributionSourceCalculator (candidates: Set[Candidate]) {

  private val candidateByShortName = candidates.groupBy(ShortCandidateName.fromCandidate)
    .map {
      case (name, candidatesWithSameShortName)  => {
        if (candidatesWithSameShortName.size > 1) {
          throw new IllegalStateException(s"More than one candidate with name $name")
        } else {
          name -> candidatesWithSameShortName.head
        }
      }
    })

  override def apply(rawDistributionComment: String,
                     preceedingCountSteps: Vector[CountStep]
                    ): Option[DistributionSource] = {
    val parsedComment = DistributionComment.from(rawDistributionComment)

    ???
  }


}
