export interface CreateTournamentRequest {
    name : string,
    numberOfGroups : number,
    /** Largest group size; the server derives the real split from playerIds + numberOfGroups. */
    playersPerGroup : number,
    qualifiersPerGroup : number,
    playerIds : number[]
}
