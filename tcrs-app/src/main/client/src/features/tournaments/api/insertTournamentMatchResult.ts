import { apiClient } from "../../../lib/apiClient";
import type { MatchInsertRequest } from "../types/MatchInsertRequest";
import type { MatchResponse } from "../types/MatchResponse";

export interface MatchInsertResponse {
    match: MatchResponse;
}

export async function insertTournamentMatchResult(tournamentId: number, request: MatchInsertRequest): Promise<MatchInsertResponse> {
    const response = await apiClient.put<MatchInsertResponse>(`/tournaments/${tournamentId}/matches`, request);
    return response.data;
}
