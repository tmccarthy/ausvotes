package au.id.tmm.ausvotes.lambdas.recount

import argonaut.Argonaut._
import argonaut.{CodecJson, Parse}
import au.id.tmm.ausvotes.core.model.codecs.GroupCodec
import au.id.tmm.ausvotes.core.model.parsing.{Candidate, CandidatePosition, Group}
import au.id.tmm.ausvotes.core.model.{GroupsAndCandidates, SenateElection}
import au.id.tmm.ausvotes.shared.aws.S3BucketName
import au.id.tmm.ausvotes.shared.aws.typeclasses.S3TypeClasses.ReadsS3
import au.id.tmm.ausvotes.shared.io.typeclasses.Functor.FunctorOps
import au.id.tmm.ausvotes.shared.io.typeclasses.Monad.MonadOps
import au.id.tmm.ausvotes.shared.io.typeclasses.{Monad, SyncEffects}
import au.id.tmm.ausvotes.shared.recountresources.EntityLocations
import au.id.tmm.countstv.model.preferences.PreferenceTree.RootPreferenceTree
import au.id.tmm.countstv.model.preferences.PreferenceTreeSerialisation
import au.id.tmm.utilities.geo.australia.State

object EntityFetching {

  def fetchGroups[F[+_, +_] : ReadsS3 : Monad](
                                                bucketName: S3BucketName,
                                                election: SenateElection,
                                                state: State,
                                              )(
                                                implicit groupCodec: GroupCodec,
                                              ): F[RecountLambdaError.EntityFetchError, Set[Group]] = {
    val objectKey = EntityLocations.locationOfGroupsObject(election, state)

    for {
      groupsJson <- ReadsS3.readAsString(bucketName, objectKey).leftMap(RecountLambdaError.EntityFetchError.GroupFetchError)
      groups <- Monad.fromEither {
        val decodeResult = Parse.decodeEither[Set[Group]](groupsJson)

        decodeResult.left.map(RecountLambdaError.EntityFetchError.GroupDecodeError)
      }
    } yield groups
  }

  def fetchCandidates[F[+_, +_] : ReadsS3 : Monad](
                       bucketName: S3BucketName,
                       election: SenateElection,
                       state: State,
                     )(
                       implicit candidateCodec: CodecJson[Candidate],
                     ): F[RecountLambdaError.EntityFetchError, Set[Candidate]] = {
    val objectKey = EntityLocations.locationOfCandidatesObject(election, state)

    for {
      candidatesJson <- ReadsS3.readAsString(bucketName, objectKey).leftMap(RecountLambdaError.EntityFetchError.GroupFetchError)
      candidates <- Monad.fromEither {
        val decodeResult = Parse.decodeEither[Set[Candidate]](candidatesJson)

        decodeResult.left.map(RecountLambdaError.EntityFetchError.GroupDecodeError)
      }
    } yield candidates
  }

  def fetchPreferenceTree[F[+_, +_] : ReadsS3 : Monad : SyncEffects](
                           bucketName: S3BucketName,
                           election: SenateElection,
                           state: State,
                           candidates: Set[Candidate],
                         ): F[RecountLambdaError.EntityFetchError, RootPreferenceTree[CandidatePosition]] = {
    val objectKey = EntityLocations.locationOfPreferenceTree(election, state)
    val allCandidatePositions = candidates.map(_.btlPosition)

    ReadsS3.useInputStream(bucketName, objectKey) { inputStream =>
      SyncEffects.syncException {
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
