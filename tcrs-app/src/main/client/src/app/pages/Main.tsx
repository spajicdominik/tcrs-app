import ReservationCalendar from "../../features/reservations/components/ReservationCalendar.tsx";
import PostReservation from "../../features/reservations/components/PostReservation.tsx";
import UserAvatar from "../../features/auth/components/UserAvatar.tsx";

export default function Main() {
    return (
        <div className="flex flex-col w-full">
            <div className="flex items-center justify-between p-4">
                <UserAvatar/>
                <PostReservation/>
            </div>
            <ReservationCalendar/>
        </div>
    );
}