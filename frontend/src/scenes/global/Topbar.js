import { Box, IconButton, useTheme, InputBase, Typography, Menu, MenuItem } from "@mui/material";
import { useContext, useState } from "react";
import { ColorModeContext, tokens } from "../../theme";
import FilterButton from "./FilterButton";
import LightModeOutlinedIcon from "@mui/icons-material/LightModeOutlined";
import DarkModeOutlinedIcon from "@mui/icons-material/DarkModeOutlined";
import PersonOutlinedIcon from "@mui/icons-material/PersonOutlined";
import AirOutlinedIcon from '@mui/icons-material/AirOutlined';
import DeviceThermostatOutlinedIcon from '@mui/icons-material/DeviceThermostatOutlined';
import WaterDropOutlinedIcon from '@mui/icons-material/WaterDropOutlined';
import SearchIcon from "@mui/icons-material/Search";
import MasksOutlinedIcon from '@mui/icons-material/MasksOutlined';
import { useLocation, Link } from "react-router-dom";
import { UserContext } from "../../userContext";

const Topbar = () => {
    const theme = useTheme();
    const colors = tokens(theme.palette.mode);
    const colorMode = useContext(ColorModeContext);
    const location = useLocation();
    const userContext = useContext(UserContext);

    const [anchorEl, setAnchorEl] = useState(null);
    const open = Boolean(anchorEl);

    const handleClick = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const isMapPage = location.pathname === '/map';
    const isRegisterPage = location.pathname === '/register';
    const isLoginPage = location.pathname === '/login';
    const isProfilePage = location.pathname === '/profile';

    return (
        <Box display="flex" justifyContent="space-between" alignItems="center" p={2} sx={{
            backgroundColor: isMapPage || isRegisterPage || isLoginPage || isProfilePage ? 'transparent' : colors.primary[400],
            position: 'flex',
            top: 0,
            left: 0,
            right: 0,
            zIndex: 1000,
            boxShadow: isMapPage ? 'none' : '10px 4px 10px rgba(0,0,0, 0.2)',
        }}>
            {/* SEARCH BAR AND FILTERS */}
            <Box display="flex" alignItems="center">
                <Box display="flex" backgroundColor={colors.primary[400]} borderRadius="35px"
                     sx={{ ml: 1, width: '400px', height: '42px', boxShadow: '0px 4px 10px rgba(0,0,0, 0.2)' }}>
                    <InputBase sx={{ ml: 3, flex: 1, fontSize: 15 }} placeholder="Search" />
                    <IconButton type="button" sx={{ p: 1, mr: 2 }}>
                        <SearchIcon />
                    </IconButton>
                </Box>
                <Box display="flex" alignItems="center" ml={2}>
                    <FilterButton icon={<DeviceThermostatOutlinedIcon />} text="Temperature" />
                    <FilterButton icon={<AirOutlinedIcon />} text="Wind" />
                    <FilterButton icon={<WaterDropOutlinedIcon />} text="Rain" />
                    <FilterButton icon={<MasksOutlinedIcon />} text="PM10" />
                    <FilterButton icon={<MasksOutlinedIcon />} text="PM2,5" />
                    <FilterButton icon={<MasksOutlinedIcon />} text="Ozon" />
                    <FilterButton icon={<MasksOutlinedIcon />} text="NO2" />
                </Box>
            </Box>

            {/* ICONS */}
            <Box display="flex" alignItems="center">
                {userContext.user ? (
                    <>
                        <IconButton onClick={handleClick}>
                            <PersonOutlinedIcon />
                        </IconButton>
                        <Menu
                            anchorEl={anchorEl}
                            open={open}
                            onClose={handleClose}
                            PaperProps={{
                                elevation: 0,
                                sx: {
                                    overflow: 'visible',
                                    filter: 'drop-shadow(0px 2px 8px rgba(0,0,0,0.32))',
                                    mt: 1.5,
                                    '& .MuiAvatar-root': {
                                        width: 32,
                                        height: 32,
                                        ml: -0.5,
                                        mr: 1,
                                    },
                                    '& .MuiTypography-root': {
                                        fontSize: 14,
                                    },
                                },
                            }}
                            transformOrigin={{ horizontal: 'right', vertical: 'top' }}
                            anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
                        >
                            <MenuItem>
                                <Typography variant="body1">{userContext.user.name}</Typography>
                            </MenuItem>
                            <MenuItem>
                                <Typography variant="body1">{userContext.user.email}</Typography>
                            </MenuItem>
                            <MenuItem>
                                <Link to="/profile">
                                    <Typography variant="body1">Profile</Typography>
                                </Link>
                            </MenuItem>
                            <MenuItem>
                                <Typography variant="body1">Settings</Typography>
                            </MenuItem>
                            <MenuItem>
                                <Typography variant="body1">Logout</Typography>
                            </MenuItem>
                        </Menu>
                    </>
                ) : (
                    <>
                        <IconButton component={Link} to="/login">
                            <PersonOutlinedIcon />
                        </IconButton>
                        <IconButton component={Link} to="/register">
                            <Typography variant="body1">Register</Typography>
                        </IconButton>
                    </>
                )}
                <IconButton onClick={colorMode.toggleColorMode}>
                    {theme.palette.mode === 'dark' ? (
                        <DarkModeOutlinedIcon />
                    ) : (
                        <LightModeOutlinedIcon />
                    )}
                </IconButton>
            </Box>
        </Box>
    );
};

export default Topbar;
