export interface ReservationResponse {
    id : number,
    timeStart : string,
    timeEnd : string,   
    matchType : string,
    canceled : boolean,
    mainPlayerId : number,
    mainPlayerName : string,
    partnerId : number | null,
    partnerName : string | null
}