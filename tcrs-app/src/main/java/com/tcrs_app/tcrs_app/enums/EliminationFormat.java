package com.tcrs_app.tcrs_app.enums;

/** How the knockout stage is drawn once the groups are finished. */
public enum EliminationFormat {

    /**
     * One bracket. The top qualifiersPerGroup of every group go through, seeded across
     * groups; everyone else is done for the season.
     */
    SINGLE_BRACKET,

    /**
     * One bracket per finishing position: all the group winners play off for first
     * place, all the runners-up for second, and so on. Nobody is eliminated at the end
     * of the group phase - every player keeps playing for the place they earned.
     *
     * Bracket size is the number of groups, and the number of brackets is the group
     * size, so this format needs equal groups and a power-of-two group count.
     */
    PER_POSITION
}
