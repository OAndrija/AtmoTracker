import { useContext, useState } from 'react';
import { UserContext } from '../userContext';
import { Navigate } from 'react-router-dom';
import './Login.css';

function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const userContext = useContext(UserContext);

    async function handleLogin(e) {
        e.preventDefault();
        const res = await fetch("http://localhost:3002/users/login", {
            method: "POST",
            credentials: "include",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });
        const data = await res.json();
        if (data._id !== undefined) {
            userContext.setUserContext(data);
            window.location.href = "/profile";
        } else {
            setUsername("");
            setPassword("");
            setError("Invalid username or password");
        }
    }

    return (
        <div className="container mt-4">
            <form onSubmit={handleLogin} className="login-form">
                {userContext.user ? <Navigate replace to="/" /> : ""}
                <div className="mb-3">
                    <input type="text" name="username" placeholder="Username"
                           value={username} onChange={(e) => (setUsername(e.target.value))}
                           className="form-control" />
                </div>
                <div className="mb-3">
                    <input type="password" name="password" placeholder="Password"
                           value={password} onChange={(e) => (setPassword(e.target.value))}
                           className="form-control" />
                </div>
                <div className="mb-3">
                    <input type="submit" name="submit" value="Log in" className="btn btn-primary" />
                </div>
                {error && <div className="text-danger">{error}</div>}
            </form>
        </div>
    );
}

export default Login;
