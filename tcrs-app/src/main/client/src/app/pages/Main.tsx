import ReservationCalendar from "../../features/reservations/components/ReservationCalendar.tsx";
import PostReservation from "../../features/reservations/components/PostReservation.tsx";

export default function Main() {
    return (
        <div className="flex flex-col w-full">
            <PostReservation/>
            <ReservationCalendar/>
        </div>
    );
}