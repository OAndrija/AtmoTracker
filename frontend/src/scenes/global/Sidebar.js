import { Box, IconButton, Button, Typography } from "@mui/material";
import { useState, useContext } from 'react';
import { Sidebar, Menu, MenuItem, sidebarClasses } from 'react-pro-sidebar';
import { Link } from 'react-router-dom';
import { useTheme } from '@mui/material';
import StatsIcon from '@mui/icons-material/BarChart';
import MapIcon from '@mui/icons-material/Map';
import { ColorModeContext, tokens } from '../../theme';
import MenuIcon from '@mui/icons-material/Menu';
import LightModeOutlinedIcon from "@mui/icons-material/LightModeOutlined";
import DarkModeOutlinedIcon from "@mui/icons-material/DarkModeOutlined";

const CustomSidebar = () => {
    const theme = useTheme();
    const colors = tokens(theme.palette.mode);
    const colorMode = useContext(ColorModeContext);
    const [collapsed, setCollapsed] = useState(true);

    const handleToggle = () => {
        setCollapsed(!collapsed);
    };

    return (
        <Box sx={{zIndex: 1000}}>
            <Sidebar
                style={{ borderRight: "0px" }}
                collapsed={collapsed}
                width="200px"
                collapsedWidth="70px"
                rootStyles={{
                    [`.${sidebarClasses.container}`]: {
                        height: '100vh',
                        backgroundColor: colors.primary[400],
                        boxShadow: '0px 4px 10px rgba(0,0,0, 0.2) !important',
                        border: 'none !important'
                    },
                }}
            >
                <Box sx={{ display: 'flex', justifyContent: 'center', p: 1, mt: 10 }}>
                    <Button onClick={handleToggle}>
                        <MenuIcon />
                    </Button>
                </Box>
                <Menu
                    menuItemStyles={{
                        button: {
                            [`&.active`]: {
                                backgroundColor: '#13395e',
                                color: '#b6c8d9',
                            },
                            color: colors.grey[100],
                            '&:hover': {
                                backgroundColor: colors.primary[900],
                                color: theme.palette.text.primary,
                            },
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center'
                        },
                    }}
                >
                    <MenuItem style={{marginTop: '12px'}} icon={<StatsIcon />} component={<Link to="/stats" />}>
                        <Typography>Stats</Typography>
                    </MenuItem>
                    <MenuItem style={{marginTop: '18px'}} icon={<MapIcon />} component={<Link to="/map" />}>
                        <Typography>Map</Typography>
                    </MenuItem>
                    <MenuItem 
                        style={{marginTop: '12px'}} 
                        icon={
                            <IconButton onClick={colorMode.toggleColorMode}>
                                {theme.palette.mode === 'dark' ? (
                                    <DarkModeOutlinedIcon />
                                ) : (
                                    <LightModeOutlinedIcon />
                                )}
                            </IconButton>}
                        />
                </Menu>
            </Sidebar>
        </Box>
    );
};

export default CustomSidebar;
