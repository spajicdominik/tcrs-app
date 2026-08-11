import React, {useEffect, useState} from 'react';
import {
    Box,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
} from '@mui/material';
import type { GroupStandings } from '../types/GroupStandings.ts';
import {toast} from "react-toastify";
import {getErrorMessage} from "../../../lib/getErrorMessage.ts";
import {getStandings} from "../api/getStandings.ts";

/** Game difference reads better with an explicit sign: +3 / 0 / -2. */
const formatDifference = (difference: number) => (difference > 0 ? `+${difference}` : String(difference));


/** One standings table per group, laid out side by side. */
const TournamentTable: React.FC = () => {
    const [standings, setStandings] = useState<GroupStandings[]>([]);
    const activeTournamentId = 1;

    useEffect(() => {
        const loadStandings = async () => {
            try {
                const standings : GroupStandings[] = await getStandings(activeTournamentId);
                setStandings(standings);
            }
            catch (error) {
                toast.error(getErrorMessage(error));
            }
        };
        loadStandings();
    }, []);

    return (
        // Grid rather than flex-wrap: with flex, a group left alone on the last row
        // stretches to the full width. auto-fill keeps the empty tracks, so every
        // group keeps the same column width however many wrap.
        <Box
            sx={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))',
                gap: 2,
                width: '100%',
            }}
        >
            {standings.map((group) => (
                <Box key={group.groupId}>
                    <Typography variant="subtitle1" sx={{mb: 1, fontWeight: 600}}>
                        Grupa {group.groupName}
                    </Typography>

                    <TableContainer component={Paper}>
                        <Table size="small" aria-label={`Tablica grupe ${group.groupName}`}>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Igrač</TableCell>
                                    <TableCell align="right">Odigrano</TableCell>
                                    <TableCell align="right">Bodovi</TableCell>
                                    <TableCell align="right">Razlika</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {group.rows.map((row) => {
                                    const qualifying = row.position <= group.qualifiersPerGroup;
                                    return (
                                        <TableRow
                                            key={row.playerId}
                                            sx={{
                                                '&:last-child td, &:last-child th': {border: 0},
                                                // the places that go through to the elimination phase
                                                ...(qualifying && {'& td, & th': {fontWeight: 600}}),
                                            }}
                                        >
                                            <TableCell component="th" scope="row">
                                                {row.position}. {row.playerName}
                                            </TableCell>
                                            <TableCell align="right">
                                                {row.played}/{row.scheduled}
                                            </TableCell>
                                            <TableCell align="right">{row.points}</TableCell>
                                            <TableCell align="right">
                                                {formatDifference(row.gameDifference)}
                                            </TableCell>
                                        </TableRow>
                                    );
                                })}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </Box>
            ))}
        </Box>
    );
}

export default TournamentTable;
