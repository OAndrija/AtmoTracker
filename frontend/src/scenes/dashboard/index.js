import React, { useContext, useEffect, useState } from "react";
import { ColorModeContext, tokens } from "../../theme";
import {
  Box,
  Typography,
  useTheme,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@mui/material";
import axios from "axios";
import LjubljanaPie from "./LjubljanaPieChart";
import MariborPie from "./MariborPieChart";
import CeljePie from "./CeljePieChart";
import KranjPie from "./KranjPieChart";
import WeatherBarChart from "./WeatherBarChart";
import MasksOutlinedIcon from "@mui/icons-material/MasksOutlined";
import AirOutlinedIcon from "@mui/icons-material/AirOutlined";
import HighQualityOutlinedIcon from "@mui/icons-material/HighQualityOutlined";
import { CSSTransition, TransitionGroup } from "react-transition-group";
import "./Dashboard.css"; // Import the CSS for animations
import Topbar from "../global/Topbar";

const Dashboard = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const colorMode = useContext(ColorModeContext);
  const [cityData, setCityData] = useState([]);
  const [cleanestCities, setCleanestCities] = useState([]);

  useEffect(() => {
    axios
      .get("http://localhost:3001/data/cleanestcities")
      .then((response) => {
        setCityData(response.data);
      })
      .catch((error) => {
        console.error("Error fetching city PM data:", error);
      });
  }, []);

  useEffect(() => {
    const sortedCities = cityData
      .sort((a, b) => a.data.pm25 - b.data.pm25)
      .slice(0, 6);
    setCleanestCities(sortedCities);
  }, [cityData]);

  return (
    <><Box> <Topbar/>  </Box>
    <Box display="flex" flexDirection="column" p={2}>
      <Box
        display="grid"
        gridTemplateColumns="repeat(12, 1fr)"
        gridAutoRows="200px"
        gap="20px"
        width="100%"
      >
        <Box
          gridColumn="span 3"
          backgroundColor={colors.primary[400]}
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          height="100%"
        >
          <Box display="flex" alignItems="center" marginTop="65px">
            <MasksOutlinedIcon
              sx={{
                color: colors.greenAccent[500],
                marginRight: "8px",
                fontSize: "30px",
              }} />
            <Typography variant="h4" sx={{ color: colors.grey[100] }}>
              Air Quality
            </Typography>
          </Box>
          <Typography variant="h5" sx={{ color: colors.greenAccent[400] }}>
            Ljubljana
          </Typography>
          <LjubljanaPie />
        </Box>
        <Box
          gridColumn="span 3"
          backgroundColor={colors.primary[400]}
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          height="100%"
        >
          <Box display="flex" alignItems="center" marginTop="65px">
            <MasksOutlinedIcon
              sx={{
                color: colors.greenAccent[500],
                marginRight: "8px",
                fontSize: "30px",
              }} />
            <Typography variant="h4" sx={{ color: colors.grey[100] }}>
              Air Quality
            </Typography>
          </Box>
          <Typography variant="h5" sx={{ color: colors.greenAccent[400] }}>
            Maribor
          </Typography>
          <MariborPie />
        </Box>
        <Box
          gridColumn="span 3"
          backgroundColor={colors.primary[400]}
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          height="100%"
        >
          <Box display="flex" alignItems="center" marginTop="65px">
            <MasksOutlinedIcon
              sx={{
                color: colors.greenAccent[500],
                marginRight: "8px",
                fontSize: "30px",
              }} />
            <Typography variant="h4" sx={{ color: colors.grey[100] }}>
              Air Quality
            </Typography>
          </Box>
          <Typography variant="h5" sx={{ color: colors.greenAccent[400] }}>
            Kranj
          </Typography>
          <KranjPie />
        </Box>
        <Box
          gridColumn="span 3"
          backgroundColor={colors.primary[400]}
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          height="100%"
        >
          <Box display="flex" alignItems="center" marginTop="65px">
            <MasksOutlinedIcon
              sx={{
                color: colors.greenAccent[500],
                marginRight: "8px",
                fontSize: "30px",
              }} />
            <Typography variant="h4" sx={{ color: colors.grey[100] }}>
              Air Quality
            </Typography>
          </Box>
          <Typography variant="h5" sx={{ color: colors.greenAccent[400] }}>
            Celje
          </Typography>
          <CeljePie />
        </Box>
        <Box
          gridColumn="span 8"
          gridRow="span 2"
          backgroundColor={colors.primary[400]}
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          height="100%"
        >
          <Box display="flex" alignItems="center" marginTop="65px">
            <AirOutlinedIcon
              sx={{
                color: colors.greenAccent[500],
                marginRight: "8px",
                fontSize: "30px",
              }} />
            <Typography variant="h4" sx={{ color: colors.grey[100] }}>
              Latest Weather Information
            </Typography>
          </Box>
          <Typography variant="h5" sx={{ color: colors.greenAccent[400] }}>
            Slovenia
          </Typography>
          <WeatherBarChart />
        </Box>
        <Box
          gridColumn="span 4"
          gridRow="span 2"
          backgroundColor={colors.primary[400]}
          p={2}
        >
          <Box display="flex" alignItems="center">
            <HighQualityOutlinedIcon
              sx={{
                marginRight: "10px",
                fontSize: "30px",
                marginTop: "-10px",
                color: colors.greenAccent[400],
              }} />
            <Typography
              variant="h4"
              sx={{ color: colors.grey[100], marginBottom: "10px" }}
            >
              Cities with Best Air Quality
            </Typography>
          </Box>
          <TableContainer
            component={Paper}
            sx={{ backgroundColor: colors.primary[500] }}
          >
            <Table aria-label="simple table">
              <TableHead>
                <TableRow>
                  <TableCell
                    sx={{ color: colors.greenAccent[300], fontSize: "16px" }}
                  >
                    City
                  </TableCell>
                  <TableCell
                    sx={{ color: colors.greenAccent[300], fontSize: "16px" }}
                  >
                    PM2.5
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                <TransitionGroup component={null}>
                  {cleanestCities.map((city) => (
                    <CSSTransition
                      key={city._id}
                      timeout={300}
                      classNames="fade"
                    >
                      <TableRow>
                        <TableCell
                          sx={{ color: colors.grey[100], fontSize: "13px" }}
                        >
                          {city.data_series_id.tags[1]}
                        </TableCell>
                        <TableCell
                          sx={{ color: colors.grey[100], fontSize: "13px" }}
                        >
                          {city.data.pm25}
                        </TableCell>
                      </TableRow>
                    </CSSTransition>
                  ))}
                </TransitionGroup>
              </TableBody>
            </Table>
          </TableContainer>
        </Box>
      </Box>
    </Box></>
  );
};

export default Dashboard;
