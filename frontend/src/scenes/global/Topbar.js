import { Box, IconButton, useTheme, InputBase, Typography } from "@mui/material";
import { useContext } from "react";
import { ColorModeContext, tokens } from "../../theme";
import FilterButton from "./FilterButton";
import LightModeOutlinedIcon from "@mui/icons-material/LightModeOutlined";
import DarkModeOutlinedIcon from "@mui/icons-material/DarkModeOutlined";
import NotificationsOutlinedIcon from "@mui/icons-material/NotificationsOutlined";
import SettingsOutlinedIcon from "@mui/icons-material/SettingsOutlined";
import PersonOutlinedIcon from "@mui/icons-material/PersonOutlined";
import AirOutlinedIcon from '@mui/icons-material/AirOutlined';
import DeviceThermostatOutlinedIcon from '@mui/icons-material/DeviceThermostatOutlined';
import WaterDropOutlinedIcon from '@mui/icons-material/WaterDropOutlined';
import SearchIcon from "@mui/icons-material/Search";

const Topbar = () => {
    const theme = useTheme();
    const colors = tokens(theme.palette.mode);
    const colorMode = useContext(ColorModeContext);

    return (
        <Box display="flex" justifyContent="space-between" alignItems="center" p={2} sx={{ backgroundColor: 'transparent' }}>
            {/* SEARCH BAR AND FILTERS */}
            <Box display="flex" alignItems="center">
                <Box display="flex" backgroundColor={colors.primary[400]} borderRadius="35px" sx={{ ml: 5, width: '300px', height: '50px', boxShadow: '0px 4px 10px rgba(0,0,0, 0.2)' }}>
                    <InputBase sx={{ ml: 3, flex: 1, fontSize: 17 }} placeholder="Search" />
                    <IconButton type="button" sx={{ p: 1, mr: 2 }}>
                        <SearchIcon />
                    </IconButton>
                </Box>
                <Box display="flex" alignItems="center" ml={2}>
                    <FilterButton icon={<DeviceThermostatOutlinedIcon />} text="Temperature" />
                    <FilterButton icon={<AirOutlinedIcon />} text="Wind" />
                    <FilterButton icon={<WaterDropOutlinedIcon />} text="Rain" />
                    <FilterButton icon={<WaterDropOutlinedIcon />} text="PM10" />
                    <FilterButton icon={<WaterDropOutlinedIcon />} text="PM2,5" />
                    <FilterButton icon={<WaterDropOutlinedIcon />} text="Ozon" />
                    <FilterButton icon={<WaterDropOutlinedIcon />} text="NO2" />
                </Box>
            </Box>

            {/* ICONS */}
            <Box display="flex" alignItems="center">
                <IconButton onClick={colorMode.toggleColorMode}>
                    {theme.palette.mode === 'dark' ? (
                        <DarkModeOutlinedIcon />
                    ) : (
                        <LightModeOutlinedIcon />
                    )}
                </IconButton>
                <IconButton>
                    <PersonOutlinedIcon />
                </IconButton>
            </Box>
        </Box>
    );
};

export default Topbar;
