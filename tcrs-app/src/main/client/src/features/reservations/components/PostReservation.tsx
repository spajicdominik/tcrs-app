import { useState } from 'react';
import { Alert, Button, Form, Modal, Select, TimePicker, DatePicker } from 'antd';
import dayjs, { type Dayjs } from 'dayjs';
import { MatchType } from '../../match/types/MatchType';
import type { CreateReservationRequest } from '../types/CreateReservationRequest';
import type { DatePickerProps } from 'antd';
import {createReservation} from "../api/createReservation.ts";

const TIME_FORMAT = 'HH:mm';
const DURATION_MINUTES = 90;
const OPENING_HOUR = 7;
const LATEST_START_HOUR = 19;

const matchTypeOptions = [
    { value: MatchType.Friendly, label: 'Prijateljska' },
    { value: MatchType.Tournament, label: 'Liga' },
];

// TODO: replace with players fetched from the API (e.g. GET /api/v1/users)
const partnerOptions = [
    { value: 2, label: 'Ivan Horvat' },
    { value: 3, label: 'Marko Marić' },
    { value: 4, label: 'Ana Kovač' },
];

type ReservationFormValues = {
    startDate?: Dayjs;
    startTime?: Dayjs;
    matchType?: MatchType;
    partnerId?: number | null;
};

const disabledTime = () => ({
    disabledHours: () =>
        Array.from({ length: 24 }, (_, h) => h).filter(
            (h) => h < OPENING_HOUR || h > LATEST_START_HOUR,
        ),
    disabledMinutes: (selectedHour: number) =>
        selectedHour === LATEST_START_HOUR ? [30] : [],
});

// only today through 6 days from now can be booked (a one-week window)
const disabledDate: DatePickerProps['disabledDate'] = (current) => {
    const today = dayjs().startOf('day');
    const lastBookableDay = dayjs().add(6, 'day').endOf('day');
    return current.isBefore(today) || current.isAfter(lastBookableDay);
};

export default function PostReservation() {
    const [open, setOpen] = useState(false);
    const [form] = Form.useForm<ReservationFormValues>();

    // live-watch the picked time so we can show the full reservation window
    const startTime = Form.useWatch('startTime', form);
    const reservationWindow = startTime
        ? `${startTime.format(TIME_FORMAT)} – ${startTime
              .add(DURATION_MINUTES, 'minute')
              .format(TIME_FORMAT)}`
        : null;

    const handleClose = () => {
        setOpen(false);
        form.resetFields();
    };

    const onFinish = (values: ReservationFormValues) => {
        const date = values.startDate!.format('YYYY-MM-DD');
        const time = values.startTime!.format('HH:mm:00');

        const payload: CreateReservationRequest = {
            // naked wall-clock string (no "Z") to match the LocalDateTime backend
            timeStart: `${date}T${time}`,
            matchType: values.matchType!,
            partnerId: values.partnerId ?? null,
        };

        // TODO: hook up createReservation(payload) here
        createReservation(payload);
        handleClose();
        window.location.reload();
    };

    const onChange: DatePickerProps['onChange'] = (date, dateString) => {
        console.log(date, dateString);
    };

    return (
        <div className="flex justify-end p-4">
            <Button type="primary" size="large" onClick={() => setOpen(true)}>
                NOVI TERMIN
            </Button>

            <Modal
                title="Novi termin"
                open={open}
                onCancel={handleClose}
                footer={null}
                destroyOnHidden
            >
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={onFinish}
                    requiredMark="optional"
                    className="mt-4 flex flex-col gap-2"
                >
                    <Form.Item
                        label="Datum termina"
                        name="startDate"
                        rules={[{ required: true, message: 'Molimo vas odaberite datum termina.' }]}
                    >
                        <DatePicker
                            className="w-full"
                            format="DD.MM.YYYY."
                            placeholder="Odaberi datum"
                            disabledDate={disabledDate}
                            onChange={onChange}
                        />
                    </Form.Item>

                    <Form.Item
                        label="Pocetak termina"
                        name="startTime"
                        rules={[{ required: true, message: 'Molimo vas odaberite pocetak termina.' }]}
                    >
                        <TimePicker
                            className="w-full"
                            format={TIME_FORMAT}
                            minuteStep={30}
                            needConfirm={false}
                            disabledTime={disabledTime}
                            hideDisabledOptions
                            showNow={false}
                            placeholder="Odaberi vrijeme"
                        />
                    </Form.Item>

                    {reservationWindow && (
                        <Alert
                            className="mb-4"
                            type="info"
                            showIcon
                            message={`Reservation: ${reservationWindow}`}
                        />
                    )}

                    <Form.Item
                        label="Vrsta termina"
                        name="matchType"
                        rules={[{ required: true, message: 'Molimo vas odaberite vrstu termina.' }]}
                    >
                        <Select placeholder="Odaberi vrstu termina" options={matchTypeOptions} />
                    </Form.Item>

                    <Form.Item label="Partner" name="partnerId">
                        <Select
                            placeholder="Dodaj partnera"
                            options={partnerOptions}
                            allowClear
                            showSearch
                            optionFilterProp="label"
                        />
                    </Form.Item>

                    <Form.Item className="mb-0">
                        <Button type="primary" htmlType="submit" block>
                            Upiši
                        </Button>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
}
