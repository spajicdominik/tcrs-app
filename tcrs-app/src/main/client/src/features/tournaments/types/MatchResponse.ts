export interface MatchResponse {
    id: number;
    roundNumber: number;
    groupId: number | null;
    groupName: string | null;
    nextMatchId: number | null;
    player1Id: number | null;
    player1Name: string | null;
    player2Id: number | null;
    player2Name: string | null;
    player1Games: number | null;
    player2Games: number | null;
    winnerId: number | null;
    scoreEnteredById: number | null;
    canEnterScore: boolean;
}
