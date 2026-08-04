import { useState } from 'react';
import { Avatar, Button, Descriptions, Modal } from 'antd';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { apiClient } from '../../../lib/apiClient';
import { getErrorMessage } from '../../../lib/getErrorMessage';

// TODO: replace with the authenticated user, loaded from a `GET /me` endpoint
// (kept in a Redux auth slice). There is no user-data source on the frontend yet.
const user = {
    firstName: 'Admin',
    lastName: 'Admin',
    email: 'admin@tcrs.com',
    role: 'ADMIN',
};

export default function UserAvatar() {
    const [open, setOpen] = useState(false);
    const [loggingOut, setLoggingOut] = useState(false);
    const navigate = useNavigate();

    const initials = `${user.firstName[0] ?? ''}${user.lastName[0] ?? ''}`.toUpperCase();

    const handleLogout = async () => {
        setLoggingOut(true);
        try {
            // clears the HttpOnly jwt + refresh cookies on the server
            await apiClient.post('/auth/logout', {});
            navigate('/authenticate');
        } catch (error) {
            toast.error(getErrorMessage(error));
        } finally {
            setLoggingOut(false);
        }
    };

    return (
        <>
            <Avatar
                size="large"
                onClick={() => setOpen(true)}
                className="cursor-pointer select-none bg-blue-500"
            >
                {initials}
            </Avatar>

            <Modal
                title="Moj profil"
                open={open}
                onCancel={() => setOpen(false)}
                footer={null}
                destroyOnHidden
            >
                <Descriptions column={1} className="mt-4" bordered size="small">
                    <Descriptions.Item label="Ime i prezime">
                        {user.firstName} {user.lastName}
                    </Descriptions.Item>
                    <Descriptions.Item label="Email">{user.email}</Descriptions.Item>
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
