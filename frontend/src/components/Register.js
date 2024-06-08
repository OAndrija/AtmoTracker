import React, { useState } from 'react';
import './LoginRegister.css';

function Register() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    const [file, setFile] = useState(null);
    const [error, setError] = useState("");

    async function Register(e) {
        e.preventDefault();

        const formData = new FormData();
        formData.append('email', email);
        formData.append('username', username);
        formData.append('password', password);
        formData.append('image', file);

        const res = await fetch("http://localhost:3001/users", {
            method: 'POST',
            credentials: 'include',
            body: formData
        });

        const data = await res.json();

        if (data._id !== undefined) {
            window.location.href = "/login";
        } else {
            setUsername("");
            setPassword("");
            setEmail("");
            setError("Registration failed");
        }
    }

    return (
        <div className="container">
            <form onSubmit={Register} className="form-group">
                <input
                    type="text"
                    name="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="form-control"
                />
                <input
                    type="text"
                    name="username"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    className="form-control"
                />
                <input
                    type="password"
                    name="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="form-control"
                />
                <label className="choose-photo-label" htmlFor="file">Choose an avatar</label>
                <input
                    type="file"
                    className="form-control"
                    id="file"
                    onChange={(e) => setFile(e.target.files[0])}
                />
                <input
                    type="submit"
                    name="submit"
                    value="Register"
                    className="btn"
                />
                {error && <div className="text-danger">{error}</div>}
            </form>
        </div>
    );
}

export default Register;
