import ReservationCalendar from "../../features/reservations/components/ReservationCalendar.tsx";
import PostReservation from "../../features/reservations/components/PostReservation.tsx";
import UserAvatar from "../../features/auth/components/UserAvatar.tsx";
import {Button} from "antd";
import {useNavigate} from "react-router-dom";
import TournamentTable from "../../features/tournaments/components/TournamentTable.tsx";

export default function Main() {
    const navigate = useNavigate();

    return (
        <div className="flex flex-col w-full">
            <div className="flex items-center justify-between p-4">
                <UserAvatar/>
                <Button type="primary" size="large" onClick={() => navigate("/post-tournament")}>
                    NOVO NATJECANJE
                </Button>
                <PostReservation/>
            </div>
            <ReservationCalendar/>
            <TournamentTable/>
        </div>
    );
}