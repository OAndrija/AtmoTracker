import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Box, Typography, useTheme } from "@mui/material";
import axios from "axios";
import AreaBumpChart from "./AreaBumpChart";
import BarChart from "./WeatherBarChart";
import LjubljanaPie from "./LjubljanaPieChart";
import MariborPie from "./MariborPieChart";
import CeljePie from "./CeljePieChart";
import KranjPie from "./KranjPieChart";

const Dashboard = () => {
  return (
    <Box height={"1000px"}>
      <Box m="20px" height={"50%"}>
        <Typography> Proba </Typography>
        <BarChart />
      </Box>
      <Box m="20px" height={"20%"}>
        <LjubljanaPie />
      </Box>
      <Box m="20px" height={"20%"}>
        <MariborPie />
      </Box>
      <Box m="20px" height={"20%"}>
        <KranjPie />
      </Box>
      <Box m="20px" height={"20%"}>
        <CeljePie />
      </Box>
    </Box>
  );
};

export default Dashboard;
