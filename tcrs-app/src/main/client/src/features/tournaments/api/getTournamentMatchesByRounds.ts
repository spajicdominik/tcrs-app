import { apiClient } from "../../../lib/apiClient";
import type { MatchRoundsResponse } from "../types/MatchRoundsResponse.ts";

export async function getTournamentMatchesByRounds(tournamentId: number): Promise<MatchRoundsResponse[]> {
    const response = await apiClient.get<MatchRoundsResponse[]>(`/tournaments/${tournamentId}/matches`);
    return response.data;
}
