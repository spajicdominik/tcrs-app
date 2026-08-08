import { apiClient } from "../../../lib/apiClient";
import type { TournamentOptionsRequest } from "../types/TournamentOptionsRequest.ts";
import type { TournamentOptionsResponse } from "../types/TournamentOptionsResponse.ts";

/**
 * POST /api/v1/tournaments/generate-options
 *
 * POST rather than GET because the input is a list of player ids and browsers drop the
 * body of a GET request. Nothing is persisted - it only computes the possible layouts.
 */
export async function generateTournamentOptions(
    request: TournamentOptionsRequest,
): Promise<TournamentOptionsResponse> {
    const { data } = await apiClient.post<TournamentOptionsResponse>(
        "/tournaments/generate-options",
        request,
    );
    return data;
}
