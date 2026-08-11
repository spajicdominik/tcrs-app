import type { StandingRow } from "./StandingRow.ts";

/** Mirrors GroupStandingsResponse on the server. */
export interface GroupStandings {
    groupId : number,
    groupName : string,
    qualifiersPerGroup : number,
    /** True once every match in the group has a result. */
    complete : boolean,
    /** Sorted best-first by the ranking chain. */
    rows : StandingRow[]
}
