package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.model.federal.senate.SenateElectionForState
import au.id.tmm.ausvotes.shared.aws.data.S3ObjectKey

object EntityLocations {

  private def locationOfDirectoryFor(election: SenateElectionForState): S3ObjectKey =
    S3ObjectKey("recountData", election.election.id.asString, election.state.abbreviation)

  def locationOfGroupsObject(election: SenateElectionForState): S3ObjectKey =
    locationOfDirectoryFor(election) / "groups.json"

  def locationOfCandidatesObject(election: SenateElectionForState): S3ObjectKey =
    locationOfDirectoryFor(election) / "candidates.json"

  def locationOfPreferenceTree(election: SenateElectionForState): S3ObjectKey =
    locationOfDirectoryFor(election) / "preferences.tree"

  def locationOfCanonicalRecount(election: SenateElectionForState): S3ObjectKey =
    locationOfDirectoryFor(election) / "canonicalRecountResult.json"

}
