import React, { useEffect, useState } from 'react';
import {
    Box,
    Button,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
} from '@mui/material';
import { Modal, InputNumber } from 'antd';
import { toast } from "react-toastify";
import { getErrorMessage } from "../../../lib/getErrorMessage.ts";
import { getTournamentMatchesByRounds } from "../api/getTournamentMatchesByRounds.ts";
import { insertTournamentMatchResult } from "../api/insertTournamentMatchResult.ts";
import type { MatchRoundsResponse } from "../types/MatchRoundsResponse.ts";
import type { MatchResponse } from "../types/MatchResponse.ts";

const TournamentMatchInsert: React.FC = () => {
    const [rounds, setRounds] = useState<MatchRoundsResponse[]>([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedMatch, setSelectedMatch] = useState<MatchResponse | null>(null);
    const [player1Score, setPlayer1Score] = useState<number | null>(null);
    const [player2Score, setPlayer2Score] = useState<number | null>(null);
    const [loading, setLoading] = useState(false);
    const activeTournamentId = 1;

    const loadMatches = async () => {
        try {
            const data = await getTournamentMatchesByRounds(activeTournamentId);
            setRounds(data);
        } catch (error) {
            toast.error(getErrorMessage(error));
        }
    };

    useEffect(() => {
        loadMatches();
    }, []);

    const showModal = (match: MatchResponse) => {
        setSelectedMatch(match);
        setPlayer1Score(null);
        setPlayer2Score(null);
        setIsModalOpen(true);
    };

    const handleOk = async () => {
        if (!selectedMatch || player1Score === null || player2Score === null) {
            toast.warning("Molimo unesite oba rezultata.");
            return;
        }

        setLoading(true);
        try {
            await insertTournamentMatchResult(activeTournamentId, {
                matchId: selectedMatch.id,
                player1Id: selectedMatch.player1Id!,
                player2Id: selectedMatch.player2Id!,
                player1Games: player1Score,
                player2Games: player2Score,
            });
            toast.success("Rezultat uspješno spremljen.");
            setIsModalOpen(false);
            loadMatches();
        } catch (error) {
            toast.error(getErrorMessage(error));
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = () => {
        setPlayer1Score(null);
        setPlayer2Score(null);
        setIsModalOpen(false);
    };

    return (
        <Box sx={{ width: '100%' }}>
            {rounds.map((round) => (
                <Box key={round.roundNumber} sx={{ mb: 4 }}>
                    <Typography variant="h6" sx={{ mb: 2, fontWeight: 700 }}>
                        {round.roundName || `${round.roundNumber}. Kolo`}
                    </Typography>

                    <Box
                        sx={{
                            display: 'grid',
                            // min(...) so the track can collapse below 320px instead of
                            // pushing the page sideways on a narrow phone
                            gridTemplateColumns: 'repeat(auto-fill, minmax(min(320px, 100%), 1fr))',
                            gap: 2,
                            width: '100%',
                            minWidth: 0,
                        }}
                    >
                        {round.matches.map((groupMatches) => (
                            <Box key={groupMatches.groupId ?? 'no-group'}>
                                <Typography variant="subtitle1" sx={{ mb: 1, fontWeight: 600 }}>
                                    {groupMatches.groupName ? `Grupa ${groupMatches.groupName}` : 'Eliminacijska faza'}
                                </Typography>

                                <TableContainer component={Paper} sx={{overflowX: 'auto'}}>
                                    <Table
                                        size="small"
                                        aria-label={`Matches for ${groupMatches.groupName || 'round'}`}
                                        sx={{
                                            '& td, & th': {px: {xs: 1, sm: 2}},
                                            // the score column hugs its content so the two
                                            // name columns share everything that is left
                                            '& td:last-of-type, & th:last-of-type': {
                                                width: '1%',
                                                whiteSpace: 'nowrap',
                                            },
                                        }}
                                    >
                                        <TableHead>
                                            <TableRow>
                                                <TableCell>Player 1</TableCell>
                                                <TableCell>Player 2</TableCell>
                                                <TableCell align="right">Score</TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {groupMatches.matches.map((match) => (
                                                <TableRow key={match.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                                    <TableCell component="th" scope="row">
                                                        {match.player1Name || 'TBD'}
                                                    </TableCell>
                                                    <TableCell>
                                                        {match.player2Name || 'TBD'}
                                                    </TableCell>
                                                    <TableCell align="right">
                                                        {match.player1Games !== null && match.player2Games !== null ? (
                                                            `${match.player1Games} : ${match.player2Games}`
                                                        ) : (
                                                            <Button
                                                                variant="contained"
                                                                size="small"
                                                                onClick={() => showModal(match)}
                                                            >
                                                                UNESI
                                                            </Button>
                                                        )}
                                                    </TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                </TableContainer>
                            </Box>
                        ))}
                    </Box>
                </Box>
            ))}

            <Modal
                title="Unos rezultata"
                open={isModalOpen}
                onOk={handleOk}
                onCancel={handleCancel}
                okText="Spremi"
                cancelText="Odustani"
                confirmLoading={loading}
            >
                {selectedMatch && (
                    <Box sx={{ mt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <Typography variant="body1" sx={{ fontWeight: 600, textAlign: 'center' }}>
                            {selectedMatch.player1Name} vs {selectedMatch.player2Name}
                        </Typography>
                        
                        {/* a long name and the input side by side squeeze each other on a
                            phone, so the label sits above the field until `sm` */}
                        <Box sx={{
                            display: 'flex',
                            flexDirection: { xs: 'column', sm: 'row' },
                            alignItems: { xs: 'flex-start', sm: 'center' },
                            justifyContent: 'space-between',
                            gap: 1,
                        }}>
                            <Typography>{selectedMatch.player1Name}</Typography>
                            <InputNumber
                                placeholder={'gemovi'}
                                inputMode="numeric"
                                min={1}
                                max={9}
                                value={player1Score}
                                onChange={(value) => setPlayer1Score(value)}
                            />
                        </Box>

                        <Box sx={{
                            display: 'flex',
                            flexDirection: { xs: 'column', sm: 'row' },
                            alignItems: { xs: 'flex-start', sm: 'center' },
                            justifyContent: 'space-between',
                            gap: 1,
                        }}>
                            <Typography>{selectedMatch.player2Name}</Typography>
                            <InputNumber
                                placeholder={'gemovi'}
                                inputMode="numeric"
                                min={1}
                                max={9}
                                value={player2Score}
                                onChange={(value) => setPlayer2Score(value)}
                            />
                        </Box>
                    </Box>
                )}
            </Modal>
        </Box>
    );
}

export default TournamentMatchInsert;