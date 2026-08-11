import { apiClient } from "../../../lib/apiClient";
import type {GroupStandings} from "../types/GroupStandings.ts";

export async function getStandings( tournamentId : number ): Promise<GroupStandings[]> {
    const response = await apiClient.get<GroupStandings[]>(`/tournaments/${tournamentId}/standings`);
    return response.data;
}
