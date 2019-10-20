package au.id.tmm.ausvotes.core.fixtures

import au.id.tmm.ausvotes.model.federal.senate._
import au.id.tmm.ausgeo.State

object BallotFixture {

  trait BallotsFixture {
    def senateElection: SenateElection = SenateElection.`2016`
    def state: State
    def election: SenateElectionForState = senateElection.electionForState(state).get

    def ballotMaker: BallotMaker
  }

  object ACT extends BallotsFixture {
    override val state: State.ACT.type = State.ACT
    override val ballotMaker = BallotMaker(CandidateFixture.ACT)

    import ballotMaker._

    private def makeBallot(atlPrefs: AtlPreferences,
                           btlPrefs: BtlPreferences
                          ) = ballotMaker.makeBallot(atlPrefs, btlPrefs, DivisionFixture.ACT.CANBERRA, PollingPlaceFixture.ACT.BARTON)

    val formalAtlAndBtl: SenateBallot = {
      val atlPrefs = orderedAtlPreferences("A", "B", "C", "D", "E", "F")
      val btlPrefs = orderedBtlPreferences("J0", "J1", "I0", "I1", "H0", "H1", "G0", "G1", "F0", "F1", "E0", "E1")

      makeBallot(atlPrefs, btlPrefs)
    }

    val formalAtlInformalBtl: SenateBallot = {
      val atlPrefs = orderedAtlPreferences("A", "B", "C", "D", "E", "F")
      val btlPrefs = btlPreferences("J0" -> "42")

      makeBallot(atlPrefs, btlPrefs)
    }

    val formalAtl: SenateBallot = {
      val atlPrefs = orderedAtlPreferences("A", "B", "C", "D", "E", "F")

      makeBallot(atlPrefs, Map.empty)
    }

    def donkeyVote: SenateBallot = formalAtl

    val oneAtl: SenateBallot = {
      val atlPrefs = orderedAtlPreferences("A")

      makeBallot(atlPrefs, Map.empty)
    }

    val tickedAtl: SenateBallot = {
      val atlPrefs = atlPreferences("A" -> "/", "B" -> "2", "C" -> "3", "D" -> "4", "E" -> "5", "F" -> "6")

      makeBallot(atlPrefs, Map.empty)
    }

    val oneTickAtl: SenateBallot = {
      val atlPrefs = atlPreferences("A" -> "/")

      makeBallot(atlPrefs, Map.empty)
    }

    val oneCrossAtl: SenateBallot = {
      val atlPrefs = atlPreferences("A" -> "*")

      makeBallot(atlPrefs, Map.empty)
    }

    val crossedAtl: SenateBallot = {
      val atlPrefs = atlPreferences("A" -> "*", "B" -> "2", "C" -> "3", "D" -> "4", "E" -> "5", "F" -> "6")

      makeBallot(atlPrefs, Map.empty)
    }

    val atlWithRepeatedNumbers: SenateBallot = {
      val atlPrefs = atlPreferences("A" -> "1", "B" -> "2", "C" -> "2", "D" -> "3", "E" -> "4", "F" -> "5", "G" -> "6")

      makeBallot(atlPrefs, Map.empty)
    }

    val atlMissedNumbers: SenateBallot = {
      val atlPrefs = atlPreferences("A" -> "1", "B" -> "3", "C" -> "4", "D" -> "5", "E" -> "6", "F" -> "7")

      makeBallot(atlPrefs, Map.empty)
    }

    val atl1Repeated: SenateBallot = {
      val atlPrefs = atlPreferences("A" -> "1", "B" -> "1", "C" -> "2", "D" -> "3", "E" -> "4", "F" -> "5", "G" -> "6")

      makeBallot(atlPrefs, Map.empty)
    }

    val formalBtl: SenateBallot = {
      val btlPrefs = orderedBtlPreferences("A0", "B1", "B0", "J0", "UG0", "UG1", "A1", "I0", "C1", "D0", "D1", "E0", "E1", "F1")

      makeBallot(Map.empty, btlPrefs)
    }

    val sixNumberedBtl: SenateBallot = {
      val btlPrefs = orderedBtlPreferences("A0", "A1", "B0", "B1", "C0", "C1")

      makeBallot(Map.empty, btlPrefs)
    }

    val btlRepeatedNumberBelow6: SenateBallot = {
      val btlPrefs = btlPreferences("A0" -> "1", "A1" -> "2", "B0" -> "3", "B1" -> "3", "C0" -> "4", "C1" -> "5", "D0" -> "6")

      makeBallot(Map.empty, btlPrefs)
    }

    val btlMissedNumberBelow6: SenateBallot = {
      val btlPrefs = btlPreferences("A0" -> "1", "A1" -> "2", "B0" -> "3", "B1" -> "5", "C0" -> "6", "C1" -> "7")

      makeBallot(Map.empty, btlPrefs)
    }

    val tickedBtl: SenateBallot = {
      val btlPrefs = btlPreferences("A0" -> "/", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6")

      makeBallot(Map.empty, btlPrefs)
    }

    val crossedBtl: SenateBallot = {
      val btlPrefs = btlPreferences("A0" -> "*", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6")

      makeBallot(Map.empty, btlPrefs)
    }

    val btlRepeatedNumberAfter6: SenateBallot = {
      val btlPrefs = btlPreferences("A0" -> "*", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6", "D0" -> "7", "D1" -> "7")

      makeBallot(Map.empty, btlPrefs)
    }

    val btlMissedNumberAfter6: SenateBallot = {
      val btlPrefs = btlPreferences("A0" -> "*", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6", "D0" -> "8")

      makeBallot(Map.empty, btlPrefs)
    }

    val donkeyAtlFormalBtl: SenateBallot = {
      ballotMaker.makeBallot(
        atlPreferences = ballotMaker.orderedAtlPreferences("A", "B", "C", "D", "E", "F"),
        btlPreferences = ballotMaker.orderedBtlPreferences("C0", "C1", "A0", "A1", "F1", "I0")
      )
    }

    val btlFirstPrefUngrouped: SenateBallot = {
      val btlPrefs = orderedBtlPreferences("UG1", "UG0", "A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1")

      makeBallot(Map.empty, btlPrefs)
    }

    val oneAtlFormalBtl: SenateBallot = {
      val atlPrefs = orderedAtlPreferences("A")
      val btlPrefs = orderedBtlPreferences("J0", "J1", "I0", "I1", "H0", "H1", "G0", "G1", "F0", "F1", "E0", "E1")

      makeBallot(atlPrefs, btlPrefs)
    }

    val usesHtv: SenateBallot = ballotMaker.makeBallot(
      ballotMaker.orderedAtlPreferences("H", "B", "J", "G", "C", "E"),
      Map.empty
    )

    val exhaustingBallot: SenateBallot = ballotMaker.makeBallot(
      atlPreferences = Map.empty,
      btlPreferences = ballotMaker.orderedBtlPreferences("C0", "UG1", "E1", "J1", "B1", "I1")
    )

    val nonExhaustingBallot: SenateBallot = ballotMaker.makeBallot(
      atlPreferences = Map.empty,
      btlPreferences = ballotMaker.orderedBtlPreferences("C0", "C1", "F0", "F1", "H0", "H1")
    )
  }

  object NT extends BallotsFixture {
    override val state: State.NT.type = State.NT
    override val ballotMaker = BallotMaker(CandidateFixture.NT)

    val firstPreferenceUngroupedIndy: SenateBallot = {
      ballotMaker.makeBallot(
        atlPreferences = Map.empty,
        btlPreferences = ballotMaker.orderedBtlPreferences("UG0", "UG1", "A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1"),
        division = DivisionFixture.NT.LINGIARI,
        pollingPlace = PollingPlaceFixture.NT.ALICE_SPRINGS
      )
    }
  }

  object WA extends BallotsFixture {
    override val state: State = State.WA
    override val ballotMaker = BallotMaker(CandidateFixture.WA)

    val firstPreferenceIneligible: SenateBallot = ballotMaker.makeBallot(
      btlPreferences = ballotMaker.orderedBtlPreferences("R0", "UG0", "UG1", "UG2", "UG3", "UG4")
    )

    val secondPreferenceIneligible: SenateBallot = ballotMaker.makeBallot(
      btlPreferences = ballotMaker.orderedBtlPreferences("UG0", "R0", "UG1", "UG2", "UG3", "UG4")
    )

    val onlyPreferencesIneligible: SenateBallot = ballotMaker.makeBallot(
      btlPreferences = ballotMaker.orderedBtlPreferences("R0")
    )
  }
}
