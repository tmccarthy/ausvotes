package au.id.tmm.ausvotes.backend.persistence.daos.enumconverters

import au.id.tmm.ausvotes.core.model.parsing.PollingPlace.PollingPlaceType

private[daos] object PollingPlaceTypeEnumConverter extends EnumConverter[PollingPlaceType] {
  override def apply(enumVal: PollingPlaceType): String = enumVal match {
    case PollingPlaceType.PollingPlace => "polling_place"
    case PollingPlaceType.SpecialHospitalTeam => "special_hospital_team"
    case PollingPlaceType.RemoteMobileTeam => "remote_mobile_team"
    case PollingPlaceType.OtherMobileTeam => "other_mobile_team"
    case PollingPlaceType.PrePollVotingCentre => "pre_poll_voting_centre"
  }

  override def apply(stringVal: String): PollingPlaceType = stringVal match {
    case "polling_place" => PollingPlaceType.PollingPlace
    case "special_hospital_team" => PollingPlaceType.SpecialHospitalTeam
    case "remote_mobile_team" => PollingPlaceType.RemoteMobileTeam
    case "other_mobile_team" => PollingPlaceType.OtherMobileTeam
    case "pre_poll_voting_centre" => PollingPlaceType.PrePollVotingCentre
  }
}
