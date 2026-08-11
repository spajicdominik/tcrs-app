import type { CurrentRoundGroupMatches } from "./CurrentRoundGroupMatches.ts";

export interface MatchRoundsResponse {
    roundNumber: number;
    roundName: string | null;
    matches: CurrentRoundGroupMatches[];
}
