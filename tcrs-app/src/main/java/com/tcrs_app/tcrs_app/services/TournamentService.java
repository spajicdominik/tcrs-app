package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.payload.request.CreateTournamentRequest;
import com.tcrs_app.tcrs_app.payload.response.TournamentResponse;
import jakarta.validation.Valid;

public interface TournamentService {
    TournamentResponse createTournament(@Valid CreateTournamentRequest request, User currentUser);
}
