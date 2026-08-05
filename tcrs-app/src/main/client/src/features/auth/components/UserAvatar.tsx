import { useState } from 'react';
import { Avatar, Button, Descriptions, Modal } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../../../stores/hooks.ts';
import { logoutUser } from '../stores/auth.ts';

function initialsOf(name: string) {
    return name
        .split(' ')
        .filter(Boolean)
        .slice(0, 2)
        .map((part) => part[0])
        .join('')
        .toUpperCase();
}

export default function UserAvatar() {
    const [open, setOpen] = useState(false);
    const [loggingOut, setLoggingOut] = useState(false);
    const navigate = useNavigate();
    const dispatch = useAppDispatch();
    const user = useAppSelector((state) => state.auth.user);

    // rendered inside a protected route, so this is only a safety net
    if (!user) return null;

    const handleLogout = async () => {
        setLoggingOut(true);
        // clears the HttpOnly cookies server-side AND the user in the store;
        // the thunk clears local state even if the request fails
        await dispatch(logoutUser());
        setLoggingOut(false);
        navigate('/authenticate');
    };

    return (
        <>
            <Avatar
                size="large"
                onClick={() => setOpen(true)}
                className="cursor-pointer select-none bg-blue-500"
            >
                {initialsOf(user.name)}
            </Avatar>

            <Modal
                title="Moj profil"
                open={open}
                onCancel={() => setOpen(false)}
                footer={null}
                destroyOnHidden
            >
                <Descriptions column={1} className="mt-4" bordered size="small">
                    <Descriptions.Item label="Ime i prezime">{user.name}</Descriptions.Item>
                    <Descriptions.Item label="Email">{user.email}</Descriptions.Item>
                    <Descriptions.Item label="Broj telefona">
                        {user.phoneNumber ?? '—'}
                    </Descriptions.Item>
                    <Descriptions.Item label="Uloga">{user.role}</Descriptions.Item>
                </Descriptions>

                <Button
                    danger
                    block
                    className="mt-6"
                    loading={loggingOut}
                    onClick={handleLogout}
                >
                    Odjava
                </Button>
            </Modal>
        </>
    );
}
