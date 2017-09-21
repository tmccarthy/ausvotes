package au.id.tmm.senatedb.api.persistence.entities.stats

import au.id.tmm.senatedb.core.model.SenateElection
import au.id.tmm.senatedb.core.model.parsing.{Division, JurisdictionLevel, VoteCollectionPoint}
import au.id.tmm.senatedb.core.tallies._
import au.id.tmm.utilities.collection.Rank
import au.id.tmm.utilities.geo.australia.State

private[stats] object StatComputations {

  def nationalStatsFor(statClass: StatClass,
                       primaryTallier: Tallier1[SenateElection],
                       capitaTallier: Option[Tallier1[SenateElection]],
                       tallyBundle: TallyBundle): Stream[Stat[SenateElection]] = {
    val primaryTally = tallyBundle.tallyProducedBy(primaryTallier)
    val capitaTally = capitaTallier.map(tallyBundle.tallyProducedBy(_))
    val perCapitaTally = capitaTally.map(primaryTally / _)

    primaryTally.asMap
      .toStream
      .map { case (election, totalFormalBallotsInElection) =>
        Stat(
          statClass,
          JurisdictionLevel.Nation,
          election,
          totalFormalBallotsInElection.value,
          Map(),
          perCapitaTally.map(_(election).value),
          Map()
        )
      }

  }

  def stateStatsFor(statClass: StatClass,
                    primaryTallier: Tallier2[SenateElection, State],
                    capitaTallier: Option[Tallier2[SenateElection, State]],
                    tallyBundle: TallyBundle): Stream[Stat[State]] = {
    val primaryTally = tallyBundle.tallyProducedBy(primaryTallier)
    val capitaTally = capitaTallier.map(tallyBundle.tallyProducedBy(_))
    val perCapitaTally = capitaTally.map(primaryTally / _)

    primaryTally.asMap
      .toStream
      .flatMap { case (election, talliesByStateForThisElection) =>

        val rankNationally = ranksFromTallies(talliesByStateForThisElection.asMap)
        val rankNationallyPerCapita = perCapitaTally.map(_(election)).map(_.asMap).map(ranksFromTallies)

        talliesByStateForThisElection
          .asStream
          .map { case (state, tallyForThisState) =>
            Stat(
              statClass,
              JurisdictionLevel.State,
              state,
              tallyForThisState,
              Map(JurisdictionLevel.Nation -> rankNationally(state)),
              perCapitaTally.map(_(election)(state).value),
              Stream(rankNationallyPerCapita.map(_(state)).map(rank => JurisdictionLevel.Nation -> rank)).flatten.toMap
            )
          }
      }
  }

  def divisionStatsFor(statClass: StatClass,
                       primaryTallier: Tallier3[SenateElection, State, Division],
                       capitaTallier: Option[Tallier3[SenateElection, State, Division]],
                       tallyBundle: TallyBundle): Stream[Stat[Division]] = {
    def divisionRanksWithinState(tally: Tally2[State, Division]): Map[Division, Rank] = {
      tally.asMap
        .flatMap { case (state, talliesByDivisionForState) =>
          ranksFromTallies(talliesByDivisionForState.asMap)
        }
    }

    def divisionRanksNationally(tally: Tally2[State, Division]): Map[Division, Rank] = {
      val formalBallotsPerDivision = tally.asMap
        .flatMap { case (state, talliesByDivisionForState) =>
          talliesByDivisionForState.asMap
        }

      ranksFromTallies(formalBallotsPerDivision)
    }

    val primaryTally = tallyBundle.tallyProducedBy(primaryTallier)
    val capitaTally = capitaTallier.map(tallyBundle.tallyProducedBy(_))
    val perCapitaTally = capitaTally.map(primaryTally / _)

    primaryTally.asMap
      .toStream
      .flatMap { case (election, talliesByStateForElection) =>

        val rankInStatePerDivision = divisionRanksWithinState(talliesByStateForElection)
        val rankPerCapitaInStatePerDivision = perCapitaTally.map(_(election)).map(divisionRanksWithinState)

        val rankNationallyPerDivision = divisionRanksNationally(talliesByStateForElection)
        val rankPerCapitaNationallyPerDivision = perCapitaTally.map(_(election)).map(divisionRanksNationally)

        talliesByStateForElection
          .asStream
          .map { case (state, division, numFormalBallots) =>
            Stat(
              statClass,
              JurisdictionLevel.Division,
              division,
              numFormalBallots,
              Map(
                JurisdictionLevel.State -> rankInStatePerDivision(division),
                JurisdictionLevel.Nation -> rankNationallyPerDivision(division)
              ),
              perCapitaTally.map(_(election)(state)(division).value),
              Stream(
                rankPerCapitaInStatePerDivision.map(_(division)).map(rank => JurisdictionLevel.State -> rank),
                rankPerCapitaNationallyPerDivision.map(_(division)).map(rank => JurisdictionLevel.Nation -> rank)
              )
                .flatten
                .toMap
            )
          }
      }
  }

  def vcpStatsFor(statClass: StatClass,
                  primaryTallier: Tallier4[SenateElection, State, Division, VoteCollectionPoint],
                  capitaTallier: Option[Tallier4[SenateElection, State, Division, VoteCollectionPoint]],
                  tallyBundle: TallyBundle): Stream[Stat[VoteCollectionPoint]] = {

    def vcpRanksWithinDivision(tally: Tally3[State, Division, VoteCollectionPoint]) = {
      tally.asMap
        .flatMap { case (state, talliesByDivisionForThisState) =>
          val formalBallotsPerVcpInThisState = talliesByDivisionForThisState.asStream
            .map { case (_, voteCollectionPoint, amount) =>
              voteCollectionPoint -> amount
            }
            .toMap

          Rank.ranksFrom(formalBallotsPerVcpInThisState)
        }
    }

    def vcpRanksWithinState(tally: Tally3[State, Division, VoteCollectionPoint]) = {
      tally.asMap
        .flatMap { case (state, talliesByDivisionForThisState) =>

          talliesByDivisionForThisState.asMap
            .flatMap { case (division, talliesPerVcpForThisDivision) =>

              val formalBallotsPerVcpInThisDivision = talliesPerVcpForThisDivision.asStream
                .map { case (voteCollectionPoint, amount) =>
                  voteCollectionPoint -> amount
                }
                .toMap

              Rank.ranksFrom(formalBallotsPerVcpInThisDivision)
            }
        }
    }

    def vcpRanksNationally(tally: Tally3[State, Division, VoteCollectionPoint]) = {
      val amountsPerVcp = tally.asStream
        .map { case (_, _, vcp, amount) =>
          vcp -> amount
        }
        .toMap

      Rank.ranksFrom(amountsPerVcp)
    }

    val primaryTally = tallyBundle.tallyProducedBy(primaryTallier)
    val capitaTally = capitaTallier.map(tallyBundle.tallyProducedBy(_))
    val perCapitaTally = capitaTally.map(primaryTally / _)

    primaryTally.asMap
      .toStream
      .flatMap { case (election, talliesByStateForThisElection) =>

        val rankInDivisionPerVcp = vcpRanksWithinDivision(talliesByStateForThisElection)
        val rankPerCapitaInDivisionPerVcp = perCapitaTally.map(_(election)).map(vcpRanksWithinDivision)

        val rankInStatePerVcp = vcpRanksWithinState(talliesByStateForThisElection)
        val rankPerCapitaInStatePerVcp = perCapitaTally.map(_(election)).map(vcpRanksWithinState)

        val rankNationallyPerVcp = vcpRanksNationally(talliesByStateForThisElection)
        val rankPerCapitaNationallyPerVcp = perCapitaTally.map(_(election)).map(vcpRanksNationally)

        talliesByStateForThisElection.asStream
          .map { case (state, division, vcp, amount) =>
            Stat(
              statClass,
              JurisdictionLevel.VoteCollectionPoint,
              vcp,
              amount,
              Map(
                JurisdictionLevel.Division -> rankInDivisionPerVcp(vcp),
                JurisdictionLevel.State -> rankInStatePerVcp(vcp),
                JurisdictionLevel.Nation -> rankNationallyPerVcp(vcp)
              ),
              perCapitaTally.map(_(election)(state)(division)(vcp).value),
              Stream(
                rankPerCapitaInDivisionPerVcp.map(_(vcp)).map(rank => JurisdictionLevel.Division -> rank),
                rankPerCapitaInStatePerVcp.map(_(vcp)).map(rank => JurisdictionLevel.State -> rank),
                rankPerCapitaNationallyPerVcp.map(_(vcp)).map(rank => JurisdictionLevel.Nation -> rank)
              )
                .flatten
                .toMap
            )
          }
      }

  }

  private def ranksFromTallies[A](values: Map[A, Tally0]): Map[A, Rank] = {
    val valuesWithResolvedTallies = values.map { case (key, tally) =>
      key -> tally.value
    }

    Rank.ranksFrom(valuesWithResolvedTallies)
  }
}
