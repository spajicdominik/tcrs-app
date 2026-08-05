import { useEffect } from "react";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import RegisterForm from "../features/auth/components/RegisterForm.tsx";
import RootLayout from "../app/pages/Root.tsx";
import AuthenticationForm from "../features/auth/components/AuthenticationForm.tsx";
import Main from "../app/pages/Main.tsx";
import ProtectedRoute from "./ProtectedRoute.tsx";
import { useAppDispatch } from "../stores/hooks.ts";
import { clearUser, fetchCurrentUser } from "../features/auth/stores/auth.ts";

const RoutesComponent = () => {
    const dispatch = useAppDispatch();

    useEffect(() => {
        // resolve the session from the HttpOnly cookie once, on app start
        dispatch(fetchCurrentUser());

        // apiClient raises this when any request comes back 401 (e.g. the token expired
        // mid-session). Clearing the user makes ProtectedRoute redirect to the login page.
        const onUnauthorized = () => dispatch(clearUser());
        window.addEventListener('auth:unauthorized', onUnauthorized);
        return () => window.removeEventListener('auth:unauthorized', onUnauthorized);
    }, [dispatch]);

    return (
        <div>
            <BrowserRouter basename="/">
                <Routes>
                    <Route path="/" element={<RootLayout/>}>
                        {/* public */}
                        <Route path="/register" element={<RegisterForm/>}/>
                        <Route path="/authenticate" element={<AuthenticationForm/>}/>

                        {/* private: everything below requires a valid session */}
                        <Route element={<ProtectedRoute/>}>
                            <Route path="/" element={<Main/>}/>
                        </Route>
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
    )
}

export default RoutesComponent
