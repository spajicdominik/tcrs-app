import { apiClient } from "../../../lib/apiClient";
import type { CreateTournamentRequest } from "../types/CreateTournamentRequest.ts";
import type { TournamentResponse } from "../types/TournamentResponse.ts";

// POST /api/v1/tournaments -> creates the tournament, its groups and the group players
export async function createTournament(
    request: CreateTournamentRequest,
): Promise<TournamentResponse> {
    const { data } = await apiClient.post<TournamentResponse>("/tournaments", request);
    return data;
}
