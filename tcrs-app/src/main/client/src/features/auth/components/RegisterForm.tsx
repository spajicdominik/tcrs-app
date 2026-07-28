import React from 'react';
import type { FormProps } from 'antd';
import { Button, Form, Input } from 'antd';
import type {RegisterRequest} from "../types/RegisterRequest.ts";

const onFinish: FormProps<RegisterRequest>['onFinish'] = (values) => {
    console.log('Success:', values);
};

const onFinishFailed: FormProps<RegisterRequest>['onFinishFailed'] = (errorInfo) => {
    console.log('Failed:', errorInfo);
};

const RegisterForm: React.FC = () => (
    <Form
        name="basic"
        labelCol={{ span: 8 }}
        wrapperCol={{ span: 16 }}
        style={{ maxWidth: 600 }}
        initialValues={{ remember: true,  phoneNumber: null }}
        onFinish={onFinish}
        onFinishFailed={onFinishFailed}
        autoComplete="off"
        className="flex flex-col gap-4"
    >
        <Form.Item<RegisterRequest>
            label="E-mail"
            name="email"
            rules={[{ required: true, message: 'Molimo vas unesite e-mail!' }]}
        >
            <Input />
        </Form.Item>

        <Form.Item<RegisterRequest>
            label="Password"
            name="password"
            rules={[{ required: true, message: 'Molimo vas unesite lozinku!' }]}
        >
            <Input.Password />
        </Form.Item>

        <Form.Item<RegisterRequest>
            label="Ime"
            name="firstName"
            rules={[{ required: true, message: 'Molimo vas unesite ime!' }]}
        >
            <Input />
        </Form.Item>

        <Form.Item<RegisterRequest>
            label="Prezime"
            name="lastName"
            rules={[{ required: true, message: 'Molimo vas unesite prezime!' }]}
        >
            <Input />
        </Form.Item>

        <Form.Item<RegisterRequest>
            label="Broj telefona"
            name="phoneNumber"
            normalize={(value) => (value === '' || value === undefined ? null : value)}
        >
            <Input />
        </Form.Item>

        <Form.Item label={null}>
            <Button type="primary" htmlType="submit">
                Submit
            </Button>
        </Form.Item>
    </Form>
);

export default RegisterForm;