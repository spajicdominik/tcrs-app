import {useState, useEffect} from "react";
import { EventCalendar } from '@mui/x-scheduler/event-calendar';
import type {SchedulerEvent} from '@mui/x-scheduler/models';
import type {ReservationResponse} from "../../features/reservations/types/ReservationResponse.ts";
import {getActiveReservations} from "../../features/reservations/api/getActiveReservations.ts";
import {reservationToEvent} from "../../features/reservations/utils/ReservationToEvent.ts";

export default function RenderEventCalendar() {
    const [events, setEvents] = useState<SchedulerEvent[]>([]);

    useEffect(() => {
        const loadReservations = async () => {
            try {
                const reservations : ReservationResponse[] = await getActiveReservations();
                const reservationEvents : SchedulerEvent[] = reservationToEvent(reservations);
                setEvents(reservationEvents);
            }
            catch (error) {
                console.error('Error loading reservations:', error);
            }
        };
        loadReservations();
    }, []);

    return (
        <div style={{ height: 600, width: '100%' }}>
            <EventCalendar
                events={events}
                onEventsChange={setEvents}
                defaultVisibleDate={new Date(2026, 6, 30)}
            />
        </div>
    );
}