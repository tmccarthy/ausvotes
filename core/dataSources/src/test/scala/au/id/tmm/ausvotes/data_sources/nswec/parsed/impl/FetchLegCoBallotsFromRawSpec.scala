package au.id.tmm.ausvotes.data_sources.nswec.parsed.impl

import au.id.tmm.ausvotes.data_sources.nswec.raw.FetchRawLegCoPreferences
import au.id.tmm.ausvotes.model.Party
import au.id.tmm.ausvotes.model.nsw.legco._
import au.id.tmm.ausvotes.model.nsw.{NswElection, _}
import au.id.tmm.ausvotes.model.stv.BallotGroup
import au.id.tmm.bfect.catsinterop._
import au.id.tmm.bfect.testing.BState
import au.id.tmm.countstv.normalisation.Preference
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import fs2.Stream

class FetchLegCoBallotsFromRawSpec extends ImprovedFlatSpec {

  private type TestIO[+E, +A] = BState[Vector[FetchRawLegCoPreferences.Row], E, A]

  private implicit val fetchRawLegCoPreferences: FetchRawLegCoPreferences[TestIO] =
    _ => BState(rows => (rows, Right(Stream.emits(rows))))

  private val rows = Vector(
    FetchRawLegCoPreferences.Row(1,   "Albury", "PP", "Albury High",           "11304",     Some("1"),  Some(1),    None,                      Some("A"), None,     formal = true, Some("SATL")),
    FetchRawLegCoPreferences.Row(2,   "Albury", "PP", "Albury High",           "11305",     Some("1"),  Some(1),    None,                      Some("A"), None,     formal = true, Some("SATL")),
    FetchRawLegCoPreferences.Row(3,   "Albury", "PP", "Albury High",           "11306",     Some("1"),  Some(1),    None,                      Some("A"), None,     formal = true, Some("SATL")),
    FetchRawLegCoPreferences.Row(749, "Albury", "PP", "Albury High",         "2726053",     Some("/"),  None,       None,                      Some("J"), None,     formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(750, "Albury", "PP", "Albury High",         "2726053",     Some("3"),  Some(3),    Some("DONNELLY Greg"),     Some("J"), Some(3),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(751, "Albury", "PP", "Albury High",         "2726053",     Some("10"), Some(10),   Some("VO Tri"),            Some("J"), Some(10), formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(752, "Albury", "PP", "Albury High",         "2726053",     Some("13"), Some(13),   Some("CHANDRALA Aruna"),   Some("J"), Some(13), formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(753, "Albury", "PP", "Albury High",         "2726053",     Some("11"), Some(11),   Some("SITOU Sally"),       Some("J"), Some(11), formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(754, "Albury", "PP", "Albury High",         "2726053",     Some("9"),  Some(9),    Some("MIRAN Michelle"),    Some("J"), Some(9),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(755, "Albury", "PP", "Albury High",         "2726053",     Some("7"),  Some(7),    Some("BUTTIGIEG Mark"),    Some("J"), Some(7),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(756, "Albury", "PP", "Albury High",         "2726053",     Some("6"),  Some(6),    Some("PRIMROSE Peter"),    Some("J"), Some(6),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(757, "Albury", "PP", "Albury High",         "2726053",     Some("5"),  Some(5),    Some("MOOKHEY Daniel"),    Some("J"), Some(5),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(758, "Albury", "PP", "Albury High",         "2726053",     Some("15"), Some(15),   Some("KIM Peter"),         Some("J"), Some(15), formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(759, "Albury", "PP", "Albury High",         "2726053",     Some("1"),  Some(1),    Some("MORIARTY Tara"),     Some("J"), Some(1),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(760, "Albury", "PP", "Albury High",         "2726053",     Some("4"),  Some(4),    Some("D'ADAM Anthony"),    Some("J"), Some(4),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(761, "Albury", "PP", "Albury High",         "2726053",     Some("12"), Some(12),   Some("SHEAHAN Charlie"),   Some("J"), Some(12), formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(762, "Albury", "PP", "Albury High",         "2726053",     Some("2"),  Some(2),    Some("SHARPE Penny"),      Some("J"), Some(2),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(763, "Albury", "PP", "Albury High",         "2726053",     Some("14"), Some(14),   Some("SEKFY Paul"),        Some("J"), Some(14), formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(764, "Albury", "PP", "Albury High",         "2726053",     Some("8"),  Some(8),    Some("SIBRAA Julie"),      Some("J"), Some(8),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(765, "Albury", "PP", "Albury High",         "2726054",     Some("1"),  Some(1),    None,                      Some("J"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(766, "Albury", "PP", "Albury High",         "2726054",     Some("2"),  Some(2),    None,                      Some("D"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(767, "Albury", "PP", "Albury High",         "2726055",     Some("4"),  Some(4),    None,                      Some("R"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(768, "Albury", "PP", "Albury High",         "2726055",     Some("5"),  Some(5),    None,                      Some("E"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(769, "Albury", "PP", "Albury High",         "2726055",     Some("3"),  Some(3),    None,                      Some("N"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(770, "Albury", "PP", "Albury High",         "2726055",     Some("2"),  Some(2),    None,                      Some("D"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(771, "Albury", "PP", "Albury High",         "2726055",     Some("1"),  Some(1),    None,                      Some("J"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(772, "Albury", "PP", "Albury High",         "2726056",     Some("1"),  Some(1),    None,                      Some("K"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(773, "Albury", "PP", "Albury High",         "2726056",     Some("2"),  Some(2),    None,                      Some("T"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(774, "Albury", "PP", "Albury High",         "2726057",     Some("1"),  Some(1),    None,                      Some("K"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(775, "Albury", "PP", "Albury High",         "2726057",     Some("2"),  Some(2),    None,                      Some("Q"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(776, "Albury", "PP", "Albury High",         "2726058",     Some("1"),  Some(1),    None,                      Some("K"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(777, "Albury", "PP", "Albury High",         "2726058",     Some("2"),  Some(2),    None,                      Some("Q"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(1199758, "Bega", "PP", "Batemans Bay SLSC", "2720284",     Some("2"),  Some(2),    None,                      Some("J"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(1199759, "Bega", "PP", "Batemans Bay SLSC", "2720284",     Some("1"),  Some(1),    None,                      Some("K"), None,     formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(1199760, "Bega", "PP", "Batemans Bay SLSC", "2720284",     Some("/"),  None,       Some("D'ADAM Anthony"),    Some("J"), Some(4),  formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(1199761, "Bega", "PP", "Batemans Bay SLSC", "2720284",     Some("/"),  None,       Some("MITCHELL Sarah"),    Some("K"), Some(5),  formal = true,  Some("RATL")),
    FetchRawLegCoPreferences.Row(1252505, "Bega", "PP", "Quaama Hall",       "2719576",     Some("X"),  Some(1),    None,                      Some("R"), None,     formal = true,  Some("SATL")),
    FetchRawLegCoPreferences.Row(1252506, "Bega", "PP", "Quaama Hall",       "2719576",     Some("X"),  None,       Some("LATHAM Mark"),       Some("T"), Some(1),  formal = true,  Some("SATL")),
    FetchRawLegCoPreferences.Row(1252507, "Bega", "PP", "Quaama Hall",       "2719576",     Some("X"),  None,       Some("BLAIR Niall"),       Some("K"), Some(2),  formal = true,  Some("SATL")),
    FetchRawLegCoPreferences.Row(1200658, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("X"),  None,       None,                      Some("M"), None,     formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200659, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("12"), Some(12),   Some("PALMER Igor"),       Some("M"), Some(12), formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200660, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("2"),  Some(2),    Some("IRAWAN Ben"),        Some("M"), Some(2),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200661, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("8"),  Some(8),    Some("BINNS Aaron"),       Some("M"), Some(8),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200662, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("1"),  Some(1),    Some("WALSH Greg"),        Some("M"), Some(1),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200663, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("15"), Some(15),   Some("WIDJAJA Hengki"),    Some("M"), Some(15), formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200664, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("11"), Some(11),   Some("VINCENT Sally-Anne"),Some("M"), Some(11), formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200665, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("5"),  Some(5),    Some("HIBBERT Sonia"),     Some("M"), Some(5),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200666, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("4"),  Some(4),    Some("ELLIOTT Robert"),    Some("M"), Some(4),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200667, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("3"),  Some(3),    Some("GRIGG Colin"),       Some("M"), Some(3),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200668, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("14"), Some(14),   Some("RIBEIRO Miguel"),    Some("M"), Some(14), formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200669, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("7"),  Some(7),    Some("LEE Eric"),          Some("M"), Some(7),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200670, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("13"), Some(13),   Some("SARDAR Cheryl"),     Some("M"), Some(13), formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200671, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("6"),  Some(6),    Some("CAIRNS Tim"),        Some("M"), Some(6),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200672, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("9"),  Some(9),    Some("DE VRIES Daniel"),   Some("M"), Some(9),  formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1200673, "Bega", "PP", "Batemans Bay SLSC", "2720108",     Some("10"), Some(10),   Some("SAMRANI Samantha"),  Some("M"), Some(10), formal = true,  Some("BTL")),
    FetchRawLegCoPreferences.Row(1727,    "Albury", "PP", "Albury High",     "INF_1123936", Some("4"),  None,       Some("SMITH Benjamin"),    Some("A"), Some(9),  formal = false, None),
    FetchRawLegCoPreferences.Row(1728,    "Albury", "PP", "Albury High",     "INF_1123936", Some("7"),  None,       Some("RICHARDS Peter"),    Some("A"), Some(7),  formal = false, None),
    FetchRawLegCoPreferences.Row(1729,    "Albury", "PP", "Albury High",     "INF_1123936", Some("9"),  None,       Some("NOUJAIM Alain"),     Some("A"), Some(5),  formal = false, None),
    FetchRawLegCoPreferences.Row(1730,    "Albury", "PP", "Albury High",     "INF_1123936", Some("15"), None,       Some("COOKE Brett"),       Some("A"), Some(2),  formal = false, None),
    FetchRawLegCoPreferences.Row(1731,    "Albury", "PP", "Albury High",     "INF_1123936", Some("3"),  None,       Some("SINGLE Kirsty"),     Some("A"), Some(10), formal = false, None),
    FetchRawLegCoPreferences.Row(1732,    "Albury", "PP", "Albury High",     "INF_1123936", Some("6"),  None,       Some("SPEARS Daniel"),     Some("A"), Some(8),  formal = false, None),
    FetchRawLegCoPreferences.Row(1733,    "Albury", "PP", "Albury High",     "INF_1123936", Some("2"),  None,       Some("LESAGE Jason"),      Some("A"), Some(11), formal = false, None),
    FetchRawLegCoPreferences.Row(1734,    "Albury", "PP", "Albury High",     "INF_1123936", Some("5"),  None,       Some("WOOD Jacqui"),       Some("A"), Some(12), formal = false, None),
    FetchRawLegCoPreferences.Row(1735,    "Albury", "PP", "Albury High",     "INF_1123936", Some("8"),  None,       Some("MULLIGAN Raymond"),  Some("A"), Some(6),  formal = false, None),
    FetchRawLegCoPreferences.Row(1736,    "Albury", "PP", "Albury High",     "INF_1123936", Some("1"),  None,       Some("FARRELL Howard"),    Some("A"), Some(13), formal = false, None),
    FetchRawLegCoPreferences.Row(1737,    "Albury", "PP", "Albury High",     "INF_1123936", Some("10"), None,       Some("COTRONEO Diane"),    Some("A"), Some(4),  formal = false, None),
    FetchRawLegCoPreferences.Row(1738,    "Albury", "PP", "Albury High",     "INF_1123936", Some("11"), None,       Some("THOMAS Holli"),      Some("A"), Some(3),  formal = false, None),
    FetchRawLegCoPreferences.Row(1739,    "Albury", "PP", "Albury High",     "INF_1123950", None,       None,       None,                      None,      None,     formal = false, None),
    FetchRawLegCoPreferences.Row(1758,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("SINGLE Kirsty"),     Some("A"), Some(10), formal = false, None),
    FetchRawLegCoPreferences.Row(1759,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("YUE Xiaowei"),       Some("C"), Some(3),  formal = false, None),
    FetchRawLegCoPreferences.Row(1760,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("POINTING Gregory"),  Some("E"), Some(3),  formal = false, None),
    FetchRawLegCoPreferences.Row(1761,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("NOONAN Alison"),     Some("C"), Some(9),  formal = false, None),
    FetchRawLegCoPreferences.Row(1762,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("GROSS Leon"),        Some("E"), Some(5),  formal = false, None),
    FetchRawLegCoPreferences.Row(1763,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("COTTEE Mike"),       Some("C"), Some(14), formal = false, None),
    FetchRawLegCoPreferences.Row(1764,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("HAWKINS Ray"),       Some("A"), Some(15), formal = false, None),
    FetchRawLegCoPreferences.Row(1765,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("RODEN Duncan"),      Some("B"), Some(14), formal = false, None),
    FetchRawLegCoPreferences.Row(1766,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("ASHBY Sam"),         Some("B"), Some(3),  formal = false, None),
    FetchRawLegCoPreferences.Row(1767,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("WARD Charlotte"),    Some("E"), Some(7),  formal = false, None),
    FetchRawLegCoPreferences.Row(1768,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("RICHARDS Peter"),    Some("A"), Some(7),  formal = false, None),
    FetchRawLegCoPreferences.Row(1769,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("HURST Emma"),        Some("E"), Some(1),  formal = false, None),
    FetchRawLegCoPreferences.Row(1770,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("GOLDIE Jenny"),      Some("C"), Some(6),  formal = false, None),
    FetchRawLegCoPreferences.Row(1771,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("HINMAN Pip"),        Some("B"), Some(7),  formal = false, None),
    FetchRawLegCoPreferences.Row(1772,    "Albury", "PP", "Albury High",     "INF_1124229", Some("1"),  None,       Some("BOURKE William"),    Some("C"), Some(1),  formal = false, None),
  )

  private def parsedRows: Vector[Ballot] = new FetchLegCoBallotsFromRaw[TestIO]
    .legCoBallotsFor(NswLegCoElection(NswElection.`2019`))
    .flatMap(_.compile.toVector)
    .runEither(rows)
    .fold(fail(_), identity)

  "converting NSW legco rows" should "parse a formal SATL ballot" in {
    assert(parsedRows contains Ballot(
      NswLegCoElection(NswElection.`2019`),
      BallotJurisdiction(
        District(NswElection.`2019`, "Albury"),
        NswVoteCollectionPoint.PollingPlace(
          NswElection.`2019`,
          District(NswElection.`2019`, "Albury"),
          "Albury High",
          NswVoteCollectionPoint.PollingPlace.Type.VotingCentre,
        ),
      ),
      BallotId(1),
      groupPreferences = Map(
        Group(NswLegCoElection(NswElection.`2019`), BallotGroup.Code("A").right.get, Some(Party("SHOOTERS, FISHERS AND FARMERS"))).right.get -> Preference.Numbered(0),
      ),
      candidatePreferences = Map.empty,
    ))
  }

}
