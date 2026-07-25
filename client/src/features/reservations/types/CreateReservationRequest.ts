import { MatchType } from "../../match/types/MatchType";

export interface CreateReservationRequest {
    timeStart : string,
    matchType : MatchType,
    partnerId : number | null
}