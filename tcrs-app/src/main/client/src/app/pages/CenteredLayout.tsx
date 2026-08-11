import { Outlet } from "react-router-dom";

/**
 * Layout for short pages - the login and registration forms - which should sit in the
 * middle of the screen.
 *
 * `m-auto` on the child rather than `items-center` on the parent: auto margins collapse
 * to zero once the content is taller than the container, so a long form still starts at
 * the top and stays fully scrollable instead of losing its head off-screen.
 */
export default function CenteredLayout() {
    return (
        <div className="flex min-h-screen w-full">
            {/* w-full so the form inside can size against a real width - a bare `m-auto`
                box shrink-wraps to its content, which made `w-full` children collapse */}
            <div className="m-auto w-full max-w-[600px] p-4">
                <Outlet />
            </div>
        </div>
    );
}
