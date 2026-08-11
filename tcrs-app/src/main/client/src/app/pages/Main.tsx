import ReservationCalendar from "../../features/reservations/components/ReservationCalendar.tsx";
import PostReservation from "../../features/reservations/components/PostReservation.tsx";
import UserAvatar from "../../features/auth/components/UserAvatar.tsx";
import {Button} from "antd";
import {useNavigate} from "react-router-dom";
import TournamentTable from "../../features/tournaments/components/TournamentTable.tsx";
import TournamentMatchInsert from "../../features/tournaments/components/TournamentMatchInsert.tsx";

export default function Main() {
    const navigate = useNavigate();

    return (
        // min-w-0 lets the flex children shrink below their content width, which is
        // what allows the calendar and the tables to scroll internally instead of
        // stretching the page
        <div className="flex w-full min-w-0 flex-col gap-4 p-3 sm:p-4">
            {/*
              * Phone: avatar on its own row, then the two actions stacked full width.
              * From `sm` up it collapses back to the original single row.
              */}
            <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <UserAvatar/>

                <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:gap-3">
                    <Button
                        type="primary"
                        size="large"
                        block
                        className="sm:!w-auto"
                        onClick={() => navigate("/post-tournament")}
                    >
                        NOVO NATJECANJE
                    </Button>
                    <PostReservation/>
                </div>
            </div>

            <ReservationCalendar/>
            <TournamentTable/>
            <TournamentMatchInsert/>
        </div>
    );
}
