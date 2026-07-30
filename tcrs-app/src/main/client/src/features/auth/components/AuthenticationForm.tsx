import React from 'react';
import type { FormProps } from 'antd';
import { Button, Form, Input } from 'antd';
import axios, {type AxiosResponse} from "axios";
import {AUTH_URL} from "../../../constants";
import type {AuthenticationRequest} from "../types/AuthenticationRequest.ts";

const onFinish: FormProps<AuthenticationRequest>['onFinish'] = async (values) => {
    console.log('Success:', values);
    const payload : AuthenticationRequest = values;
    try {
        const response : AxiosResponse<AuthenticatorResponse> = await axios.post(AUTH_URL + "/authenticate", payload);
        return response.data;
    } catch (error) {
        console.error('Error:', error);
    }
};

const onFinishFailed: FormProps<AuthenticationRequest>['onFinishFailed'] = (errorInfo) => {
    console.log('Failed:', errorInfo);
};

const AuthenticationForm: React.FC = () => (
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
        <Form.Item<AuthenticationRequest>
            label="E-mail"
            name="email"
            rules={[{ required: true, message: 'Molimo vas unesite e-mail!' }]}
        >
            <Input />
        </Form.Item>

        <Form.Item<AuthenticationRequest>
            label="Password"
            name="password"
            rules={[{ required: true, message: 'Molimo vas unesite lozinku!' }]}
        >
            <Input.Password />
        </Form.Item>

        <Form.Item label={null}>
            <Button type="primary" htmlType="submit">
                Submit
            </Button>
        </Form.Item>
    </Form>
);

export default AuthenticationForm;