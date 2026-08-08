import type { TournamentOption } from "./TournamentOption.ts";

export interface TournamentOptionsResponse {
    numberOfPlayers : number,
    options : TournamentOption[]
}
