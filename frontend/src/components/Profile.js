import { useContext, useEffect, useState } from 'react';
import { UserContext } from '../userContext';
import { Navigate } from 'react-router-dom';
import './Profile.css'; // Import your CSS file

function Profile() {
    const userContext = useContext(UserContext);
    const [profile, setProfile] = useState({});

    useEffect(() => {
        const getProfile = async () => {
            const res = await fetch("http://localhost:3002/users/profile", { credentials: "include" });
            const data = await res.json();
            setProfile(data);
        }
        getProfile();
    }, []);

    const avatarUrl = profile.path ? `http://localhost:3002${profile.path}` : '';

    return (
        <>
            {!userContext.user ? <Navigate replace to="/profile" /> : ""}
            <div className="container">
                <div className="card">
                    <h1>User Profile</h1>
                    {avatarUrl && <img src={avatarUrl} alt="User Avatar" className="rounded-avatar" />}
                    <p>Username: {profile.username}</p>
                    <p>Email: {profile.email}</p>
                </div>
            </div>
        </>
    );
}

export default Profile;
