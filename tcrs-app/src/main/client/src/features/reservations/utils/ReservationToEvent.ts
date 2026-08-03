import type {ReservationResponse} from "../types/ReservationResponse.ts";
import type {SchedulerEvent} from '@mui/x-scheduler/models';

export function reservationToEvent(reservations : ReservationResponse[]) {
    const events : SchedulerEvent[] = [];

    for (const reservation of reservations) {
        const event : SchedulerEvent = {
            id : reservation.id,
            title : reservation.mainPlayerName + ", " + reservation.partnerName,
            start : reservation.timeStart,
            end : reservation.timeEnd,
        }
        events.push(event);
    }
    return events;
}