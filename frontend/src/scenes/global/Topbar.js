import React, { useState, useContext, useEffect } from "react";
import {
  Box,
  IconButton,
  useTheme,
  InputBase,
  Typography,
  Menu,
  MenuItem,
  Divider,
  Avatar,
} from "@mui/material";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { ColorModeContext, tokens } from "../../theme";
import FilterButton from "./FilterButton";
import LightModeOutlinedIcon from "@mui/icons-material/LightModeOutlined";
import DarkModeOutlinedIcon from "@mui/icons-material/DarkModeOutlined";
import LogoutIcon from "@mui/icons-material/Logout";
import AirOutlinedIcon from "@mui/icons-material/AirOutlined";
import DeviceThermostatOutlinedIcon from "@mui/icons-material/DeviceThermostatOutlined";
import WaterDropOutlinedIcon from "@mui/icons-material/WaterDropOutlined";
import SearchIcon from "@mui/icons-material/Search";
import MasksOutlinedIcon from "@mui/icons-material/MasksOutlined";
import { UserContext } from "../../userContext";
import "./App.css"; // Make sure to import your CSS file
import MapSearch from "../map/MapSearch";


const Topbar = ({ avatarUrl,setFilter,onSuggestionClick}) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const colorMode = useContext(ColorModeContext);
  const location = useLocation();
  const userContext = useContext(UserContext);

  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);
  const [isMapPage, setIsMapPage] = useState(location.pathname === "http://localhost:3000/map");
  const [animationClass, setAnimationClass] = useState("topbar-initial");

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  useEffect(() => {
    const currentIsMapPage = location.pathname === "/map";
    if (currentIsMapPage !== isMapPage) {
      setAnimationClass(  
        currentIsMapPage ? "topbar-retract" : "topbar-slide-in"
      );
      setIsMapPage(currentIsMapPage);
    } else if (
      location.pathname !== "/map" &&
      animationClass === "topbar-initial"
    ) {
      setAnimationClass("topbar-slide-in");
    }
  }, [location, isMapPage, animationClass]);

  const initialAnimation = `
    .topbar-initial {
      background-color: transparent;
      box-shadow: 0;
    }
  `;

  const slideInAnimation = `
    @keyframes slideInFromLeft {
      0% {
        background-position: 100% 0;
        box-shadow: 0;
      }
      100% {
        background-position: 0 0;
        box-shadow: 10px 4px 10px rgba(0,0,0, 0.2);
      }
    }

    .topbar-slide-in {
      animation: slideInFromLeft 0.8s forwards;
      background: linear-gradient(to right, ${colors.primary[400]} 50%, transparent 50%);
      background-size: 200% 100%;
    }
  `;

  const retractAnimation = `
    @keyframes retractToLeft {
      0% {
        background-position: 0 0;
        box-shadow: 10px 4px 10px rgba(0,0,0, 0.2);
      }
      100% {
        background-position: 100% 0;
        box-shadow: 0;
      }
    }

    .topbar-retract {
      animation: retractToLeft 0.8s forwards;
      background: linear-gradient(to right, ${colors.primary[400]} 50%, transparent 50%);
      background-size: 200% 100%;
    }
  `;

  return (
    <>
      <style>
        {initialAnimation}
        {slideInAnimation}
        {retractAnimation}
      </style>
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        p={2}
        className={`topbar-transition ${animationClass}`} // Apply the transition class here
        sx={{
           backgroundColor: 'transparent',
          position: "relative",
          top: 0,
          left: 0,
          right: 0,
          zIndex: 1000,
          boxShadow: isMapPage ? "none" : "none",
        }}
      >
        {/* SEARCH BAR AND FILTERS */}
        <Box display="flex" alignItems="center">
        <MapSearch onSuggestionClick={onSuggestionClick} colors={colors} />
          {isMapPage && (
            <Box display="flex" alignItems="center" ml={2}>
              <FilterButton icon={<DeviceThermostatOutlinedIcon />} text="Temperature" onClick={() => setFilter('temperature')} />
              <FilterButton icon={<AirOutlinedIcon />} text="Wind" onClick={() => setFilter('wind')} />
              <FilterButton icon={<WaterDropOutlinedIcon />} text="Rain" onClick={() => setFilter('rain')} />
              <FilterButton icon={<MasksOutlinedIcon />} text="PM10" onClick={() => setFilter('pm10')} />
              <FilterButton icon={<MasksOutlinedIcon />} text="PM2,5" onClick={() => setFilter('pm25')} />
              <FilterButton icon={<MasksOutlinedIcon />} text="Ozon" onClick={() => setFilter('ozon')} />
              <FilterButton icon={<MasksOutlinedIcon />} text="NO2" onClick={() => setFilter('no2')} />
            </Box>
          )}
        </Box>

        {/* ICONS */}
        <Box display="flex" alignItems="center">
          {userContext.user ? (
            <>
              <IconButton
                onClick={handleClick}
                sx={{ width: 44, height: 44, marginRight: "10px" }}
              >
                <Avatar src={avatarUrl} sx={{ width: 44, height: 44 }} />
              </IconButton>
              <Menu
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                PaperProps={{
                  elevation: 3,
                  sx: {
                    overflow: "visible",
                    filter: "drop-shadow(0px 2px 8px rgba(0,0,0,0.32))",
                    mt: 1.5,
                    borderRadius: 7,
                    minWidth: 350,
                    width: "fit-content",
                    padding: 2,
                    color: "inherit",
                    fontFamily: "inherit",
                    fontSize: "inherit",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    "& .MuiMenuItem-root": {
                      display: "flex",
                      flexDirection: "column",
                      alignItems: "center",
                      gap: 1,
                      "&:hover": {
                        backgroundColor: "transparent",
                      },
                      "&:active": {
                        backgroundColor: "transparent",
                      },
                    },
                    "& .MuiAvatar-root": {
                      width: 64,
                      height: 64,
                    },
                    "& .MuiTypography-root": {
                      textAlign: "center",
                    },
                    "& .menuLink": {
                      width: "100%",
                      color: "inherit",
                      textDecoration: "none",
                      justifyContent: "center",
                      textAlign: "center",
                      alignItems: "center",
                      display: "flex",
                      padding: "8px 16px",
                    },
                  },
                }}
                transformOrigin={{ horizontal: "right", vertical: "top" }}
                anchorOrigin={{ horizontal: "right", vertical: "bottom" }}
              >
                <MenuItem>
                  <Typography variant="body" fontSize="14px">
                    {userContext.user.email}
                  </Typography>
                </MenuItem>
                <MenuItem>
                  <Avatar src={avatarUrl} alt={userContext.user.username} />
                </MenuItem>
                <MenuItem>
                  <Typography variant="body" fontSize="22px">
                    Hi, {userContext.user.username}!
                  </Typography>
                </MenuItem>
                <Divider sx={{ width: "100%" }} />
                <MenuItem
                  sx={{
                    borderRadius: "25px",
                    width: "250px",
                    padding: "4px 8px",
                    alignSelf: "stretch",
                    boxShadow: "0px 0px 10px rgba(0,0,0, 0.3)",
                    marginTop: "15px",
                    display: "flex",
                    flexDirection: "row",
                    backgroundColor: colors.primary[400],
                    flex: "1",
                    "&:hover": {
                      backgroundColor: colors.primary[500],
                    },
                  }}
                >
                  <Box
                    sx={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "center",
                      flex: "1",
                    }}
                  >
                    <Box
                      onClick={colorMode.toggleColorMode}
                      sx={{
                        flex: "1",
                        justifyContent: "flex-start",
                        display: "flex",
                        alignItems: "center",
                        cursor: "pointer",
                        padding: "8px 30px",
                        paddingLeft: "55px",
                      }}
                    >
                      {theme.palette.mode === "dark" ? (
                        <DarkModeOutlinedIcon />
                      ) : (
                        <LightModeOutlinedIcon />
                      )}
                    </Box>
                    <Divider
                      orientation="vertical"
                      flexItem
                      sx={{ ml: 2, bgcolor: colors.primary[600] }}
                    />
                    <Box
                      component={Link}
                      to="/logout"
                      className="menuLink"
                      sx={{
                        flex: "1",
                        justifyContent: "flex-end",
                        display: "flex",
                        cursor: "pointer",
                        padding: "8px 16px",
                      }}
                    >
                      <LogoutIcon sx={{ mr: 0.5 }} />
                      <Typography variant="body" fontSize="16px">
                        Sign out
                      </Typography>
                    </Box>
                  </Box>
                </MenuItem>
              </Menu>
            </>
          ) : (
            <>
              <IconButton component={Link} to="/login">
                <Typography variant="body">Login</Typography>
              </IconButton>
              <IconButton component={Link} to="/register">
                <Typography variant="body">Register</Typography>
              </IconButton>
            </>
          )}
        </Box>
      </Box>
    </>
  );
};

export default Topbar;
