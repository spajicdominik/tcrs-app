package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.payload.request.CreateTournamentRequest;
import com.tcrs_app.tcrs_app.payload.request.MatchInsertRequest;
import com.tcrs_app.tcrs_app.payload.request.TournamentOptionsRequest;
import com.tcrs_app.tcrs_app.payload.response.*;
import jakarta.validation.Valid;

import java.util.List;

public interface TournamentService {
    TournamentResponse createTournament(@Valid CreateTournamentRequest request, User currentUser);

    TournamentOptionsResponse generateTournamentOptions(@Valid TournamentOptionsRequest request, User currentUser);

    TournamentMatchesResponse generateTournamentMatches(@Valid Long id, User currentUser);

    TournamentStandingsResponse getCurrentTournamentStandings(@Valid Long id, User currentUser);

    ActiveTournamentResponse getActiveTournament(User currentUser);

    ActiveTournamentResponse finishTournament(@Valid Long id, User currentUser);

    MatchInsertResponse insertTournamentMatchResult(@Valid MatchInsertRequest request, User currentUser);

    List<MatchRoundsResponse> getTorunamentMatchesByRounds(@Valid Long id, User currentUser);
}
