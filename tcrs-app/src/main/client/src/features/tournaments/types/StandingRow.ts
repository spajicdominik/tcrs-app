/** One player's line in a group table. Mirrors StandingRowResponse on the server. */
export interface StandingRow {
    position : number,
    playerId : number,
    playerName : string,
    /** Matches actually played - players progress at different speeds. */
    played : number,
    /** Matches scheduled for this player in the group, played or not. */
    scheduled : number,
    points : number,
    gamesWon : number,
    gamesLost : number,
    gameDifference : number
}
