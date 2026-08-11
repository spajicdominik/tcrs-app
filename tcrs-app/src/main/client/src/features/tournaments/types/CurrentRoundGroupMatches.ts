import type { MatchResponse } from "./MatchResponse.ts";

export interface CurrentRoundGroupMatches {
    groupId: number | null;
    groupName: string | null;
    matches: MatchResponse[];
}
