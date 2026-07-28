
import { Outlet } from "react-router-dom";


export default function RootLayout() {
    return (
        <div className="flex items-center justify-center h-screen">
            <Outlet></Outlet>
        </div>
    )
}