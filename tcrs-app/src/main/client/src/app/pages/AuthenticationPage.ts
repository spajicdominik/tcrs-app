import { Link, useSearchParams } from "react-router-dom";
import { redirect } from "react-router-dom";

export default function AuthenticationPage() {
    const [searchParams] = useSearchParams();
    const isLogin = searchParams.get('mode') == 'login';
    const isQueryValid = (searchParams.get('mode') == 'login') || (searchParams.get('mode') == 'register');

    
}