import { useState } from "react";
import { ProSidebar, Menu, MenuItem } from "react-pro-sidebar";
import { Box, IconButton, Typography, useTheme } from '@mui/material';
import { Link } from "react-router-dom";
import { tokens } from "../../theme";
import HomeOutlinedIcon from "@mui/icons-material/HomeOutlined";

const Sidebar = () => {
    const theme = useTheme();
    const colors = tokens(theme.palette.mode);
    const [isCollapsed, setisCollapsed] = useState(false);
    const [selected, setSelected] = useState('Dashboard');

    return (
        <Box sx={{ "& .pro-sidebar-inner":{
        
        } }}>

        </Box>
    );
}

export default Sidebar;