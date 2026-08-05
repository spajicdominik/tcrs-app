import React from 'react';
import type { FormProps } from 'antd';
import { Button, Form, Input } from 'antd';
import axios from "axios";
import {AUTH_URL} from "../../../constants";
import type {AuthenticationRequest} from "../types/AuthenticationRequest.ts";
import {useNavigate} from "react-router-dom";
import {toast} from "react-toastify";
import {useAppDispatch} from "../../../stores/hooks.ts";
import {fetchCurrentUser} from "../stores/auth.ts";
import {getErrorMessage} from "../../../lib/getErrorMessage.ts";

const AuthenticationForm: React.FC = () => {
    const navigate = useNavigate();
    const dispatch = useAppDispatch();

    const onFinish: FormProps<AuthenticationRequest>['onFinish'] = async (values) => {
        const payload : AuthenticationRequest = values;
        try {
            // withCredentials lets the browser store the HttpOnly JWT cookie the backend sets.
            // We deliberately do NOT read or store the token — it lives only in the cookie.
            await axios.post(AUTH_URL + "/authenticate", payload, { withCredentials: true });

            // Populate the store BEFORE navigating, otherwise ProtectedRoute still sees
            // 'anonymous' and bounces straight back to this page.
            await dispatch(fetchCurrentUser()).unwrap();
            navigate("/");
        } catch (error) {
            toast.error(getErrorMessage(error));
        }
    };

    const onFinishFailed: FormProps<AuthenticationRequest>['onFinishFailed'] = (errorInfo) => {
        console.log('Failed:', errorInfo);
    };

    return (
        <Form
            name="basic"
            labelCol={{span: 8}}
            wrapperCol={{span: 16}}
            style={{maxWidth: 600}}
            initialValues={{remember: true, phoneNumber: null}}
            onFinish={onFinish}
            onFinishFailed={onFinishFailed}
            autoComplete="off"
            className="flex flex-col gap-4"
        >
            <Form.Item<AuthenticationRequest>
                label="E-mail"
                name="email"
                rules={[{required: true, message: 'Molimo vas unesite e-mail!'}]}
            >
                <Input/>
            </Form.Item>

            <Form.Item<AuthenticationRequest>
                label="Password"
                name="password"
                rules={[{required: true, message: 'Molimo vas unesite lozinku!'}]}
            >
                <Input.Password/>
            </Form.Item>

            <Form.Item label={null}>
                <Button type="primary" htmlType="submit">
                    Submit
                </Button>
            </Form.Item>
        </Form>
    );
}

export default AuthenticationForm;