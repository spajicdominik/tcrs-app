import { Navigate, Outlet } from "react-router-dom";
import { Spin } from "antd";
import { useAppSelector } from "../stores/hooks.ts";

/**
 * Gate for private routes.
 *
 * The `idle`/`loading` branch matters: /me is async, so on the first render there is
 * no user yet. Redirecting during that window would bounce a logged-in user to the
 * login page on every refresh.
 */
export default function ProtectedRoute() {
    const status = useAppSelector((state) => state.auth.status);

    if (status === 'idle' || status === 'loading') {
        return (
            <div className="flex h-screen w-full items-center justify-center">
                <Spin size="large" />
            </div>
        );
    }

    return status === 'authenticated' ? <Outlet /> : <Navigate to="/authenticate" replace />;
}
