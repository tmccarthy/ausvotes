package au.id.tmm.ausvotes.shared.recountresources

import au.id.tmm.ausvotes.core.model.SenateElection
import au.id.tmm.ausvotes.shared.aws.data.S3ObjectKey
import au.id.tmm.utilities.geo.australia.State

object EntityLocations {

  private def locationOfDirectoryFor(election: SenateElection, state: State): S3ObjectKey =
    S3ObjectKey("recountData", election.id, state.abbreviation)

  def locationOfGroupsObject(election: SenateElection, state: State): S3ObjectKey =
    locationOfDirectoryFor(election, state) / "groups.json"

  def locationOfCandidatesObject(election: SenateElection, state: State): S3ObjectKey =
    locationOfDirectoryFor(election, state) / "candidates.json"

  def locationOfPreferenceTree(election: SenateElection, state: State): S3ObjectKey =
    locationOfDirectoryFor(election, state) / "preferences.tree"

  def locationOfCanonicalRecount(election: SenateElection, state: State): S3ObjectKey =
    locationOfDirectoryFor(election, state) / "canonicalRecountResult.json"

}
