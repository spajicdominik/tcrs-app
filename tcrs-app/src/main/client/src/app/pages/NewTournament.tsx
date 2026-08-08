import React, { useEffect, useState } from 'react';
import { Button, Flex, Input, Select, Space } from 'antd';
import type { TransferProps } from 'antd';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import type { UserResponse } from '../../features/users/types/UserResponse.ts';
import { getActiveUsers } from '../../features/users/api/getActiveUsers.ts';
import { getErrorMessage } from '../../lib/getErrorMessage.ts';
import type { TournamentOptionsResponse } from "../../features/tournaments/types/TournamentOptionsResponse.ts";
import { generateTournamentOptions } from "../../features/tournaments/api/generateTournamentOptions.ts";
import type { TournamentOptionsRequest } from "../../features/tournaments/types/TournamentOptionsRequest.ts";
import type { CreateTournamentRequest } from "../../features/tournaments/types/CreateTournamentRequest.ts";
import { createTournament } from "../../features/tournaments/api/createTournament.ts";
import type { PlayerRow } from '../../features/tournaments/types/PlayerRow.ts';
import PlayerTableTransfer from '../../features/tournaments/components/PlayerTableTransfer.tsx';

const NewTournament: React.FC = () => {
    const navigate = useNavigate();
    const [players, setPlayers] = useState<PlayerRow[]>([]);
    const [targetKeys, setTargetKeys] = useState<TransferProps['targetKeys']>([]);
    const [options, setOptions] = useState<TournamentOptionsResponse>();
    const [name, setName] = useState('');
    const [numberOfGroups, setNumberOfGroups] = useState<number>();
    const [qualifiersPerGroup, setQualifiersPerGroup] = useState<number>();
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        const loadPlayers = async () => {
            try {
                const users: UserResponse[] = await getActiveUsers();
                setPlayers(users.map((user) => ({ ...user, key: String(user.id) })));
            } catch (error) {
                toast.error(getErrorMessage(error));
            }
        };
        loadPlayers();
    }, []);

    // the ids to send as `playerIds` when creating the tournament
    const selectedPlayerIds = (targetKeys ?? []).map(Number);

    const onChange: TransferProps['onChange'] = (nextTargetKeys) => {
        setTargetKeys(nextTargetKeys);
        // the options were computed for the previous player list, so they no longer apply
        setOptions(undefined);
        setNumberOfGroups(undefined);
        setQualifiersPerGroup(undefined);
    };

    const generateOptions = async (playerIds: number[]) => {
        const payload: TournamentOptionsRequest = { playerIds };

        try {
            const response: TournamentOptionsResponse = await generateTournamentOptions(payload);
            setOptions(response);
            setNumberOfGroups(undefined);
            setQualifiersPerGroup(undefined);
            if (response.options.length === 0) {
                toast.error(`Za ${response.numberOfPlayers} igrača nema valjane podjele u grupe.`);
            }
        } catch (error) {
            toast.error(getErrorMessage(error));
        }
    };

    // the option the admin picked in the first Select; drives the second one
    const selectedOption = options?.options.find((o) => o.numberOfGroups === numberOfGroups);

    const groupSelectOptions = (options?.options ?? []).map((option) => ({
        value: option.numberOfGroups,
        label: `${option.numberOfGroups} ${option.numberOfGroups === 1 ? 'grupa' : 'grupe'}`
            + ` (${option.groupSizes.join(' + ')} igrača)`,
    }));

    const qualifierSelectOptions = (selectedOption?.qualifiersPerGroupOptions ?? []).map((q) => ({
        value: q,
        label: `${q} po grupi → ${q * selectedOption!.numberOfGroups} u eliminaciji`,
    }));

    const handleGroupsChange = (value: number) => {
        setNumberOfGroups(value);
        // a qualifier count valid for the old layout may be invalid for this one
        setQualifiersPerGroup(undefined);
    };

    const canSubmit =
        name.trim().length > 0 && selectedOption !== undefined && qualifiersPerGroup !== undefined;

    const submit = async () => {
        if (!selectedOption || qualifiersPerGroup === undefined) {
            return;
        }

        const payload: CreateTournamentRequest = {
            name: name.trim(),
            numberOfGroups: selectedOption.numberOfGroups,
            // the server derives the real split; this only satisfies its @NotNull
            playersPerGroup: selectedOption.groupSizes[0],
            qualifiersPerGroup,
            playerIds: selectedPlayerIds,
        };

        setSaving(true);
        try {
            const tournament = await createTournament(payload);
            toast.success(`Natjecanje "${tournament.name}" je stvoreno.`);
            navigate('/');
        } catch (error) {
            toast.error(getErrorMessage(error));
        } finally {
            setSaving(false);
        }
    };

    return (
        <Flex align="start" gap="middle" vertical className="w-full p-4">
            <PlayerTableTransfer
                players={players}
                targetKeys={targetKeys}
                onChange={onChange}
            />

            <span>Odabrano igrača: {selectedPlayerIds.length}</span>

            <Button
                type="primary"
                disabled={selectedPlayerIds.length === 0}
                onClick={() => generateOptions(selectedPlayerIds)}
            >
                Generiraj opcije
            </Button>

            {options && options.options.length > 0 && (
                <Space direction="vertical" size="middle" className="w-full max-w-md">
                    <Input
                        placeholder="Naziv natjecanja"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />

                    <Select
                        className="w-full"
                        placeholder="Broj grupa"
                        options={groupSelectOptions}
                        value={numberOfGroups}
                        onChange={handleGroupsChange}
                    />

                    <Select
                        className="w-full"
                        placeholder="Broj kvalificiranih po grupi"
                        options={qualifierSelectOptions}
                        value={qualifiersPerGroup}
                        onChange={setQualifiersPerGroup}
                        // nothing to choose from until a layout is picked
                        disabled={!selectedOption}
                    />

                    <Button type="primary" block disabled={!canSubmit} loading={saving} onClick={submit}>
                        Stvori natjecanje
                    </Button>
                </Space>
            )}
        </Flex>
    );
};

export default NewTournament;
