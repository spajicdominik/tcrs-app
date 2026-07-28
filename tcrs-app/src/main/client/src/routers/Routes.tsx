import {BrowserRouter, Route, Routes} from "react-router-dom";
import RegisterForm from "../features/auth/components/RegisterForm.tsx";
import RootLayout from "../app/pages/Root.tsx";

const RoutesComponent = () => {

    return (
        <div>
            <BrowserRouter basename="/">
                <Routes>
                    <Route path="/" element={<RootLayout/>}>
                        <Route path="/register" element={<RegisterForm/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
    )
}

export default RoutesComponent