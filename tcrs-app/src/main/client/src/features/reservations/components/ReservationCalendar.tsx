import {useState, useEffect} from "react";
import {StandaloneWeekView} from "@mui/x-scheduler";
import type {SchedulerEvent} from '@mui/x-scheduler/models';
import type {ReservationResponse} from "../types/ReservationResponse.ts";
import {getActiveReservations} from "../api/getActiveReservations.ts";
import {reservationToEvent} from "../utils/ReservationToEvent.ts";

export default function ReservationCalendar() {
    const [events, setEvents] = useState<SchedulerEvent[]>([]);

    // start the (week-aligned) view on today so it shows today + the next 6 days
    const today = new Date();
    const weekStartsOn = today.getDay() as 0 | 1 | 2 | 3 | 4 | 5 | 6;

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
                defaultVisibleDate={today}
                readOnly={true}
                // whole hours only; 22 shows the 21:00–22:00 row so 21:30 events are visible
                viewConfig={{ week: { startTime: 7, endTime: 21 } }}
                // week aligns to today's weekday -> window is today .. today + 6 days
                defaultPreferences={{ weekStartsOn }}
            />
        </div>
    );
}