package com.tcrs_app.tcrs_app.repositories;

import com.tcrs_app.tcrs_app.entities.Match;
import com.tcrs_app.tcrs_app.entities.Tournament;
import com.tcrs_app.tcrs_app.entities.TournamentGroup;
import com.tcrs_app.tcrs_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    /** Guard against generating a tournament's fixtures twice. */
    boolean existsByGroupIn(List<TournamentGroup> groups);

    /** Every fixture of one group, played or not - the standings need both. */
    List<Match> findByGroup(TournamentGroup group);

    List<Match> findByGroupInOrderByRoundNumberAscIdAsc(List<TournamentGroup> groups);

    /**
     * True while any group match of this tournament is still unplayed - i.e. the group
     * phase is not finished yet. Reaches elimination matches too, which is why it can
     * only be written now that match points at its tournament directly.
     */
    boolean existsByTournamentAndGroupIsNotNullAndWinnerIsNull(Tournament tournament);

    /**
     * The (at most two) matches whose winners feed the given tie. Ordered by id, which
     * is creation order, so the first is always the upper slot of the pairing.
     */
    List<Match> findByNextMatchOrderByIdAsc(Match nextMatch);

    /** Elimination matches are exactly the ones with no group - the bracket exists already. */
    boolean existsByTournamentAndGroupIsNull(Tournament tournament);

    /**
     * Every match of the tournament, group and elimination alike. The group-based
     * variant above cannot see elimination matches, whose group is null.
     */
    List<Match> findByTournamentOrderByRoundNumberAscIdAsc(Tournament tournament);

    /**
     * Matches a player still has to play. Not a stored state - an unplayed match is
     * simply one without a winner, whatever round it belongs to.
     */
    @Query("""
            SELECT m FROM Match m
            WHERE m.winner IS NULL
              AND (m.player1 = :player OR m.player2 = :player)
            ORDER BY m.roundNumber ASC, m.id ASC
            """)
    List<Match> findUnplayedMatchesForPlayer(@Param("player") User player);
}
