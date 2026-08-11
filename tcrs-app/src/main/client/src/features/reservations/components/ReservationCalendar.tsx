import {useState, useEffect} from "react";
import {StandaloneWeekView} from "@mui/x-scheduler";
import type {SchedulerEvent} from '@mui/x-scheduler/models';
import type {ReservationResponse} from "../types/ReservationResponse.ts";
import {getActiveReservations} from "../api/getActiveReservations.ts";
import {reservationToEvent} from "../utils/ReservationToEvent.ts";
import {toast} from "react-toastify";
import {getErrorMessage} from "../../../lib/getErrorMessage.ts";
import {useIsMobile} from "../../../hooks/useIsMobile.ts";

/** Below this the seven day columns become too narrow to read a name in. */
const MIN_WEEK_WIDTH = 700;

export default function ReservationCalendar() {
    const [events, setEvents] = useState<SchedulerEvent[]>([]);
    const isMobile = useIsMobile();

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
                toast.error(getErrorMessage(error));
            }
        };
        loadReservations();
    }, []);

    return (
        /*
         * A full week cannot be squeezed into a phone screen without the columns
         * becoming unusable, so the grid keeps a readable minimum width and the user
         * swipes sideways within this box. min-w-0 is what actually lets the box be
         * narrower than its content - without it a flex child refuses to shrink and
         * the whole page would scroll instead.
         */
        <div className="w-full min-w-0 overflow-x-auto">
            <div
                style={{
                    // shorter on a phone: 600px of calendar would fill the screen and
                    // bury the standings below it
                    height: isMobile ? 420 : 600,
                    minWidth: MIN_WEEK_WIDTH,
                }}
            >
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
        </div>
    );
}
