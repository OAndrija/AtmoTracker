import { useContext, useEffect, useState } from 'react';
import { UserContext } from '../userContext';
import { Navigate, useNavigate } from 'react-router-dom';
import './Profile.css';

function Profile() {
    const userContext = useContext(UserContext);
    const [profile, setProfile] = useState({});
    const navigate = useNavigate();

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
            {userContext.user ? <Navigate replace to="/profile" /> : ""}
            <div className="container">
                <div className="card">
                    <h1>User Profile</h1>
                    {avatarUrl && <img src={avatarUrl} alt="User Avatar" className="rounded-avatar" />}
                    <p>Username: {profile.username}</p>
                    <p>Email: {profile.email}</p>
                    <div className="mb-3">
                        <input type="submit" name="submit" value="Log out" className="btn-logout btn-primary"
                               onClick={() => navigate("/logout", { replace: true })}></input>
                    </div>
                </div>
            </div>
        </>
    );
}

export default Profile;
