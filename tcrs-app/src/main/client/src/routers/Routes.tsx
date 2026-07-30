import {BrowserRouter, Route, Routes} from "react-router-dom";
import RegisterForm from "../features/auth/components/RegisterForm.tsx";
import RootLayout from "../app/pages/Root.tsx";
import AuthenticationForm from "../features/auth/components/AuthenticationForm.tsx";
import Main from "../app/pages/Main.tsx";

const RoutesComponent = () => {

    return (
        <div>
            <BrowserRouter basename="/">
                <Routes>
                    <Route path="/" element={<RootLayout/>}>
                        <Route path="/" element={<Main/>}/>
                        <Route path="/register" element={<RegisterForm/>}/>
                        <Route path="/authenticate" element={<AuthenticationForm/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
    )
}

export default RoutesComponent