export interface MatchInsertRequest {
    matchId: number;
    player1Id: number;
    player2Id: number;
    player1Games: number;
    player2Games: number;
    scoreEnteredBy?: number;
}
