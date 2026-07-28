import {BrowserRouter, Route, Routes} from "react-router-dom";
import RegisterForm from "../features/auth/components/RegisterForm.tsx";

const RoutesComponent = () => {

    return (
        <div>
            <BrowserRouter>
                <Routes>
                    <Route path="/register" element={<RegisterForm/>}/>
                </Routes>
            </BrowserRouter>
        </div>
    )
}

export default RoutesComponent