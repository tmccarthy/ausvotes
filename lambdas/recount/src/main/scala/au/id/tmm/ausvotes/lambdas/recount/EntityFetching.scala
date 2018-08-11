package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import argonaut.{CodecJson, Parse}
import au.id.tmm.ausvotes.core.model.codecs.GroupCodec
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, CandidatePosition, Group}
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.aws.{S3BucketName, S3Ops}
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.countstv.model.preferences.PreferenceTree.RootPreferenceTree
import au.id.tmm.countstv.model.preferences.PreferenceTreeSerialisation
import au.id.tmm.utilities.geo.australia.State
import scalaz.zio.IO

object EntityFetching {

  def fetchGroups(
                   bucketName: S3BucketName,
                   election: SenateElection,
                   state: State,
                 )(
                   implicit groupCodec: GroupCodec,
                 ): IO[RecountLambdaError.EntityFetchError, Set[Group]] = {
    val objectKey = EntityLocations.locationOfGroupsObject(election, state)

    for {
      groupsJson <- S3Ops.retrieveString(bucketName, objectKey).leftMap(RecountLambdaError.EntityFetchError.GroupFetchError)
      groups <- IO.fromEither {
        val decodeResult = Parse.decodeEither[Set[Group]](groupsJson)

        decodeResult.left.map(RecountLambdaError.EntityFetchError.GroupDecodeError)
      }
    } yield groups
  }

  def fetchCandidates(
                       bucketName: S3BucketName,
                       election: SenateElection,
                       state: State,
                     )(
                       implicit candidateCodec: CodecJson[Candidate],
                     ): IO[RecountLambdaError.EntityFetchError, Set[Candidate]] = {
    val objectKey = EntityLocations.locationOfCandidatesObject(election, state)

    for {
      candidatesJson <- S3Ops.retrieveString(bucketName, objectKey).leftMap(RecountLambdaError.EntityFetchError.GroupFetchError)
      candidates <- IO.fromEither {
        val decodeResult = Parse.decodeEither[Set[Candidate]](candidatesJson)

        decodeResult.left.map(RecountLambdaError.EntityFetchError.GroupDecodeError)
      }
    } yield candidates
  }

  def fetchPreferenceTree(
                           bucketName: S3BucketName,
                           election: SenateElection,
                           state: State,
                           candidates: Set[Candidate],
                         ): IO[RecountLambdaError.EntityFetchError, RootPreferenceTree[CandidatePosition]] = {
    val objectKey = EntityLocations.locationOfPreferenceTree(election, state)
    val allCandidatePositions = candidates.map(_.btlPosition)

    S3Ops.useInputStream(bucketName, objectKey) { inputStream =>
      IO.syncException {
        PreferenceTreeSerialisation.deserialise(allCandidatePositions, inputStream)
      }
    }.leftMap(RecountLambdaError.EntityFetchError.PreferenceTreeFetchError)
  }

  final case class Entities(
                             election: SenateElection,
                             state: State,

                             groupsAndCandidates: GroupsAndCandidates,
                             preferenceTree: RootPreferenceTree[CandidatePosition],
                           )

}
