package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.payload.request.CreateTournamentRequest;
import com.tcrs_app.tcrs_app.payload.request.TournamentOptionsRequest;
import com.tcrs_app.tcrs_app.payload.response.TournamentMatchesResponse;
import com.tcrs_app.tcrs_app.payload.response.TournamentOptionsResponse;
import com.tcrs_app.tcrs_app.payload.response.TournamentResponse;
import jakarta.validation.Valid;

public interface TournamentService {
    TournamentResponse createTournament(@Valid CreateTournamentRequest request, User currentUser);

    TournamentOptionsResponse generateTournamentOptions(@Valid TournamentOptionsRequest request, User currentUser);

    TournamentMatchesResponse generateTournamentMatches(@Valid Long id, User currentUser);
}
