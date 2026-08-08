/** One valid way to split the selected players into groups. */
export interface TournamentOption {
    numberOfGroups : number,
    /** Size of each group, largest first, e.g. [6, 6, 6, 5] for 23 players in 4 groups. */
    groupSizes : number[],
    /** False when some groups get one extra player. */
    evenGroups : boolean,
    /** Qualifiers-per-group values that produce a power-of-two bracket, e.g. [1, 2, 4]. */
    qualifiersPerGroupOptions : number[]
}
