import {ColorModeContext, useMode} from './theme';
import {CssBaseline, ThemeProvider, Box} from '@mui/material';
import {Routes, Route} from "react-router-dom";
import Topbar from './scenes/global/Topbar';
import Dashboard from './scenes/dashboard';
import CustomSidebar from './scenes/global/Sidebar';
import {UserContext} from "./userContext";
// import WebSocket from './components/WebSocket';
// import Map from './scenes/map';
import MapComponent from './scenes/map/MapComponent';
import Login from "./components/Login";
import Register from "./components/Register";
import Profile from "./components/Profile";
import Logout from "./components/Logout";
import {useEffect, useState} from "react";

function App() {
    const [theme, colorMode] = useMode();
    const [user, setUser] = useState(localStorage.user ? JSON.parse(localStorage.user) : null);
    const [profile, setProfile] = useState({});
    const updateUserData = (userInfo) => {
        localStorage.setItem("user", JSON.stringify(userInfo));
        setUser(userInfo);
    }

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
        <UserContext.Provider value={{
            user: user,
            setUserContext: updateUserData
        }}>
            <ColorModeContext.Provider value={colorMode}>
                <ThemeProvider theme={theme}>
                    <CssBaseline/>
                    <Box sx={{display: 'flex', height: '100vh'}}>
                        <CustomSidebar/>
                        <Box sx={{display: 'flex', flexDirection: 'column', flex: 1}}>
                            <Topbar avatarUrl={avatarUrl}/>
                            <Box sx={{flex: 1}}>
                                <Routes>
                                    <Route path="/dashboard" element={<Dashboard/>}/>
                                    <Route path="/map" element={<MapComponent/>}/>
                                    <Route path="/login" element={<Login/>}/>
                                    <Route path="/register" element={<Register/>}/>
                                    <Route path="/profile" element={<Profile/>}/>
                                    <Route path="/logout" element={<Logout/>}/>
                                </Routes>
                            </Box>
                        </Box>
                    </Box>
                </ThemeProvider>
            </ColorModeContext.Provider>
        </UserContext.Provider>
    );
}

export default App;