import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Box, Typography, useTheme } from "@mui/material";
import axios from 'axios';
import AreaBumpChart from './AreaBumpChart';
import BarChart from './WeatherBarChart';

const Dashboard = () => {
    return (
        <Box m="20px" height={"80%"}>
            <Typography> Proba </Typography>
            <BarChart />
        </Box>
    );
}

export default Dashboard;
