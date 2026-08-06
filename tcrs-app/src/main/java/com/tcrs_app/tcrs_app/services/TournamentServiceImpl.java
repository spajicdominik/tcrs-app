package com.tcrs_app.tcrs_app.services;

import com.tcrs_app.tcrs_app.entities.Tournament;
import com.tcrs_app.tcrs_app.entities.User;
import com.tcrs_app.tcrs_app.enums.TournamentPhase;
import com.tcrs_app.tcrs_app.exception.TournamentException;
import com.tcrs_app.tcrs_app.payload.request.CreateTournamentRequest;
import com.tcrs_app.tcrs_app.payload.request.TournamentOptionsRequest;
import com.tcrs_app.tcrs_app.payload.response.TournamentOptionsResponse;
import com.tcrs_app.tcrs_app.payload.response.TournamentResponse;
import com.tcrs_app.tcrs_app.repositories.TournamentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService{

    private final TournamentRepository tournamentRepository;

    @Override
    public TournamentResponse createTournament(CreateTournamentRequest request, User currentUser) {
        List<Tournament> activeTournaments = tournamentRepository.findByPhaseNot(TournamentPhase.CLOSED);
        if (!activeTournaments.isEmpty()) {
            throw new TournamentException("There is a currently active tournament.", HttpStatus.BAD_REQUEST);
        }

        Tournament tournament = Tournament.builder()
                .name(request.getName())
                .phase(TournamentPhase.GROUP)
                .qualifiersPerGroup(request.getQualifiersPerGroup())
                .build();

        tournamentRepository.save(tournament);
        return null;
    }

    @Override
    public TournamentOptionsResponse generateTournamentOptions(TournamentOptionsRequest request, User currentUser) {
        Integer numberOfPlayers = request.getPlayerIds().size();
        return null;
    }
}
