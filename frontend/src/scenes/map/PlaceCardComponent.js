import React, { useContext, useEffect, useState } from "react";
import { ColorModeContext, tokens } from "../../theme";
import { useTheme } from "@mui/material/styles";
import {
  Card,
  CardContent,
  CardHeader,
  IconButton,
  Typography,
  Grid,
  Box,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { keyframes } from "@mui/system";
import axios from "axios";

const slideIn = keyframes`
  from {
    transform: translateX(-100%);
  }
  to {
    transform: translateX(0);
  }
`;

const slideOut = keyframes`
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-100%);
  }
`;

function PlaceCardComponent({ item, onClose }) {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const [visible, setVisible] = useState(true);
  const [imageUrl, setImageUrl] = useState("");

  useEffect(() => {
    console.log("Item:", item);
    fetchImage(item.name);
  }, [item]);

  const handleClose = () => {
    setVisible(false);
    setTimeout(() => {
      onClose();
    }, 320);
  };

  const fetchImage = async (cityName) => {
    try {
      if (!process.env.REACT_APP_UNSPLASH_ACCESS_KEY) {
        console.error("Unsplash access key is not set");
        return;
      }

      const fetchFromUnsplash = async (query) => {
        const response = await axios.get(
          `https://api.unsplash.com/search/photos`,
          {
            params: { query, per_page: 1 },
            headers: {
              Authorization: `Client-ID ${process.env.REACT_APP_UNSPLASH_ACCESS_KEY}`,
            },
          }
        );
        return response.data.results;
      };

      const nameWithoutFirstWord = cityName.split(" ").slice(1).join(" ");

      console.log("Fetching image for:", nameWithoutFirstWord);
      let results = await fetchFromUnsplash(nameWithoutFirstWord);

      if (results.length > 0) {
        console.log("Image found:", results[0].urls.small);
        setImageUrl(results[0].urls.small);
      } else {
        console.log("No image found for:", cityName);
      }
    } catch (error) {
      console.error("Error fetching image:", error);
    }
  };

  const nameWithoutFirstWord = item.name.split(" ").slice(1).join(" ");

  return (
    <Card
      sx={{
        position: "fixed",
        top: 0,
        left: 70,
        width: 440,
        paddingTop: 8,
        paddingBottom: 2,
        paddingLeft: 1,
        backgroundColor: colors.primary[400],
        borderTopRightRadius: 40,
        borderBottomRightRadius: 40,
        zIndex: 990,
        animation: `${visible ? slideIn : slideOut} 0.5s forwards`,
      }}
    >
      <CardHeader
        title={
          <Typography
            variant="body1"
            sx={{
              fontSize: "24px",
              fontWeight: "bold",
            }}
          >
            {nameWithoutFirstWord}
          </Typography>
        }
        action={
          <IconButton onClick={handleClose}>
            <CloseIcon  sx={{ fontSize: 34, marginRight: 1 }}/>
          </IconButton>
        }
        sx={{
          borderBottom: "1px solid #ddd",
          marginBottom: 2,
        }}
      />
      {imageUrl && (
        <Box
          component="img"
          sx={{
            height: 200,
            width: "100%",
            objectFit: "cover",
            borderBottom: "1px solid #ddd",
          }}
          src={imageUrl}
          alt={nameWithoutFirstWord}
        />
      )}
      <CardContent>
        {item.temperature && (
          <Typography variant="body1" fontSize={18}>
            <strong>Temperature</strong>: <span style={{ color: colors.greenAccent[300] }}>{item.temperature} °C</span>
          </Typography>
        )}
        {item.windGusts && (
          <Typography variant="body1" fontSize={18}>
            <strong>Wind Gusts</strong>: <span style={{ color: colors.greenAccent[300] }}>{item.windGusts} m/s</span>
          </Typography>
        )}
        {item.windSpeed && (
          <Typography variant="body1" fontSize={18}>
            <strong>Wind Speed</strong>: <span style={{ color: colors.greenAccent[300] }}>{item.windSpeed} m/s</span>
          </Typography>
        )}
        {item.precipitation && (
          <Typography variant="body1" fontSize={18}>
            <strong>Precipitation</strong>: <span style={{ color: colors.greenAccent[300] }}>{item.precipitation} mm</span>
          </Typography>
        )}
        {item.pm10 && (
          <Typography variant="body1" fontSize={18}>
            <strong>PM10</strong>: <span style={{ color: colors.greenAccent[300] }}>{item.pm10} µg/m³</span>
          </Typography>
        )}
        {item.pm25 && (
          <Typography variant="body1" fontSize={18}>
            <strong>PM2.5</strong>: <span style={{ color: colors.greenAccent[300] }}>{item.pm25} µg/m³</span>
          </Typography>
        )}
        {item.ozon && (
          <Typography variant="body1" fontSize={18}>
            <strong>Ozone</strong>: <span style={{ color: colors.greenAccent[300] }}>{item.ozon} µg/m³</span>
          </Typography>
        )}
        {item.no2 && (
          <Typography variant="body1" fontSize={18}>
            <strong>NO2</strong>: <span style={{ color: colors.greenAccent[300] }}>{item.no2} µg/m³</span>
          </Typography>
        )}
      </CardContent>
    </Card>
  );
}

export default PlaceCardComponent;
