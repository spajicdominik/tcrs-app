import React from "react";
import { IlamyCalendar } from '@ilamy/calendar';

const events = [
    {
        id: '1',
        title: 'Project Kickoff',
        start: '2026-05-01T10:00:00Z',
        end: '2026-05-01T11:30:00Z',
        color: 'blue'
    }
];

const Main : React.FC = () => (
    <div>
            <IlamyCalendar
                events={events}
                initialView="week"
                onEventClick={(event) => console.log('Clicked:', event)}
            />
    </div>
);

export default Main;