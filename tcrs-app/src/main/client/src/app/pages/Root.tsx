import { Outlet } from "react-router-dom";

/**
 * The app shell. Deliberately does NOT centre its child vertically: pages like Main
 * are taller than the viewport, and a centred flex item that overflows spills equally
 * off the top and the bottom - the top half then cannot be scrolled to at all.
 *
 * Pages that are short enough to centre (the auth forms) use CenteredLayout instead.
 */
export default function RootLayout() {
    return (
        <div className="flex min-h-screen w-full">
            <Outlet />
        </div>
    );
}
