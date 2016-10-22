package au.id.tmm.senatedb.fixtures

import au.id.tmm.senatedb.model.SenateElection
import au.id.tmm.senatedb.model.parsing.Ballot
import au.id.tmm.senatedb.model.parsing.Ballot._
import au.id.tmm.utilities.geo.australia.State

object Ballots {

  trait BallotsFixture {
    def election: SenateElection = SenateElection.`2016`

    def state: State

    def ballotMaker: BallotMaker
  }

  object ACT extends BallotsFixture {
    override val state = State.ACT
    override val ballotMaker = BallotMaker(Candidates.ACT)

    import ballotMaker._

    private def makeBallot(atlPrefs: AtlPreferences,
                           btlPrefs: BtlPreferences
                          ) = ballotMaker.makeBallot(atlPrefs, btlPrefs, Divisions.ACT.CANBERRA, PollingPlaces.ACT.BARTON)

    val formalAtlAndBtl: Ballot = {
      val atlPrefs = orderedAtlPreferences("A", "B", "C", "D", "E", "F")
      val btlPrefs = orderedBtlPreferences("J0", "J1", "I0", "I1", "H0", "H1", "G0", "G1", "F0", "F1", "E0", "E1")

      makeBallot(atlPrefs, btlPrefs)
    }

    val formalAtlInformalBtl: Ballot = {
      val atlPrefs = orderedAtlPreferences("A", "B", "C", "D", "E", "F")
      val btlPrefs = btlPreferences("J0" -> "42")

      makeBallot(atlPrefs, btlPrefs)
    }
    val formalAtl: Ballot = {
      val atlPrefs = orderedAtlPreferences("A", "B", "C", "D", "E", "F")

      makeBallot(atlPrefs, Map.empty)
    }

    val oneAtl: Ballot = {
      val atlPrefs = orderedAtlPreferences("A")

      makeBallot(atlPrefs, Map.empty)
    }

    val tickedAtl: Ballot = {
      val atlPrefs = atlPreferences("A" -> "/", "B" -> "2", "C" -> "3", "D" -> "4", "E" -> "5", "F" -> "6")

      makeBallot(atlPrefs, Map.empty)
    }

    val crossedAtl: Ballot = {
      val atlPrefs = atlPreferences("A" -> "*", "B" -> "2", "C" -> "3", "D" -> "4", "E" -> "5", "F" -> "6")

      makeBallot(atlPrefs, Map.empty)
    }

    val atlWithRepeatedNumbers: Ballot = {
      val atlPrefs = atlPreferences("A" -> "1", "B" -> "2", "C" -> "2", "D" -> "3", "E" -> "4", "F" -> "5", "G" -> "6")

      makeBallot(atlPrefs, Map.empty)
    }

    val atlMissedNumbers: Ballot = {
      val atlPrefs = atlPreferences("A" -> "1", "B" -> "3", "C" -> "4", "D" -> "5", "E" -> "6", "F" -> "7")

      makeBallot(atlPrefs, Map.empty)
    }

    val atl1Repeated: Ballot = {
      val atlPrefs = atlPreferences("A" -> "1", "B" -> "1", "C" -> "2", "D" -> "3", "E" -> "4", "F" -> "5", "G" -> "6")

      makeBallot(atlPrefs, Map.empty)
    }

    val formalBtl: Ballot = {
      val btlPrefs = orderedBtlPreferences("A0", "B1", "B0", "J0", "UG0", "UG2", "A1", "I0", "C1", "D0", "D1", "E0", "E1", "F1")

      makeBallot(Map.empty, btlPrefs)
    }

    val sixNumberedBtl: Ballot = {
      val btlPrefs = orderedBtlPreferences("A0", "A1", "B0", "B1", "C0", "C1")

      makeBallot(Map.empty, btlPrefs)
    }

    val btlRepeatedNumberBelow6: Ballot = {
      val btlPrefs = btlPreferences("A0" -> "1", "A1" -> "2", "B0" -> "3", "B1" -> "3", "C0" -> "4", "C1" -> "5", "D0" -> "6")

      makeBallot(Map.empty, btlPrefs)
    }

    val btlMissedNumberBelow6: Ballot = {
      val btlPrefs = btlPreferences("A0" -> "1", "A1" -> "2", "B0" -> "3", "B1" -> "5", "C0" -> "6", "C1" -> "7")

      makeBallot(Map.empty, btlPrefs)
    }

    val tickedBtl: Ballot = {
      val btlPrefs = btlPreferences("A0" -> "/", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6")

      makeBallot(Map.empty, btlPrefs)
    }

    val crossedBtl: Ballot = {
      val btlPrefs = btlPreferences("A0" -> "*", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6")

      makeBallot(Map.empty, btlPrefs)
    }

    val btlRepeatedNumberAfter6: Ballot = {
      val btlPrefs = btlPreferences("A0" -> "*", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6", "D0" -> "7", "D1" -> "7")

      makeBallot(Map.empty, btlPrefs)
    }

    val btlMissedNumberAfter6: Ballot = {
      val btlPrefs = btlPreferences("A0" -> "*", "A1" -> "2", "B0" -> "3", "B1" -> "4", "C0" -> "5", "C1" -> "6", "D0" -> "8")

      makeBallot(Map.empty, btlPrefs)
    }

    val donkeyAtlFormalBtl: Ballot = {
      ballotMaker.makeBallot(
        atlPreferences = ballotMaker.orderedAtlPreferences("A", "B", "C", "D", "E", "F"),
        btlPreferences = ballotMaker.orderedBtlPreferences("C0", "C1", "A0", "A1", "F1", "I0")
      )
    }

    val btlFirstPrefUngrouped: Ballot = {
      val btlPrefs = orderedBtlPreferences("UG1", "UG0", "A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1")

      makeBallot(Map.empty, btlPrefs)
    }

    val oneAtlFormalBtl: Ballot = {
      val atlPrefs = orderedAtlPreferences("A")
      val btlPrefs = orderedBtlPreferences("J0", "J1", "I0", "I1", "H0", "H1", "G0", "G1", "F0", "F1", "E0", "E1")

      makeBallot(atlPrefs, btlPrefs)
    }
  }

  object NT extends BallotsFixture {
    override val state = State.NT
    override val ballotMaker = BallotMaker(Candidates.NT)

    val firstPreferenceUngroupedIndy = {
      ballotMaker.makeBallot(
        atlPreferences = Map.empty,
        btlPreferences = ballotMaker.orderedBtlPreferences("UG0", "UG1", "A0", "A1", "B0", "B1", "C0", "C1", "D0", "D1", "E0", "E1"),
        division = Divisions.NT.LINGIARI,
        pollingPlace = PollingPlaces.NT.ALICE_SPRINGS
      )
    }
  }
}
