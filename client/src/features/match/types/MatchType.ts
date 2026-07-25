export const MatchType = {
    Friendly : "FRIENDLY",
    Tournament : "TOURNAMENT",
} as const;

export type MatchType = (typeof MatchType)[keyof typeof MatchType];