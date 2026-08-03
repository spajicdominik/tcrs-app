import {useState, useEffect} from "react";
import {StandaloneWeekView} from "@mui/x-scheduler";
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
            <StandaloneWeekView
                events={events}
                onEventsChange={setEvents}
                defaultVisibleDate={new Date(2026, 7, 4)}
                readOnly={true}
                // whole hours only; 22 shows the 21:00–22:00 row so 21:30 events are visible
                viewConfig={{ week: { startTime: 7, endTime: 21 } }}
            />
        </div>
    );
}