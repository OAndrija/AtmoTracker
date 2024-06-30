import React, { useEffect, useState } from "react";
import {
  MapContainer,
  TileLayer,
  Marker,
  Popup,
  ZoomControl,
  useMap,
} from "react-leaflet";
import "leaflet/dist/leaflet.css";
import { useTheme } from "@mui/material/styles";
import axios from "axios";
import L from "leaflet";
import PlaceCardComponent from "./PlaceCardComponent";
import {
  FaSnowflake,
  FaTemperatureLow,
  FaCloudSun,
  FaSun,
  FaTemperatureHigh,
  FaIndustry,
  FaSmog,
  FaCar,
  FaShieldAlt,
} from "react-icons/fa";
import { renderToString } from "react-dom/server";
import { tokens } from "../../theme";

const DEFAULT_CENTER = [46.13723571213672, 14.732964599882179];
const DEFAULT_ZOOM = 9;


const defaultIcon = new L.Icon({
  iconUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const greenPm10Icon = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=material&color=green&size=small&icon=factory&iconSize=small&textSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const orangePm10Icon = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=material&color=orange&size=small&icon=factory&iconSize=small&textSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const redPm10Icon = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=material&color=red&size=small&icon=factory&iconSize=small&textSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const greenpm25Icon = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=green&size=small&icon=smog&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const orangepm25Icon = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=orange&size=small&icon=smog&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const redpm25Icon = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=red&size=small&icon=smog&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const greenOzon = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=green&size=small&icon=shield-alt&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const orangeOzon = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=orange&size=small&icon=shield-alt&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const redOzon = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=red&size=small&icon=shield-alt&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const greenNo2 = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=green&size=small&icon=car&iconSize=small&textSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const orangeNo2 = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=orange&size=small&icon=car&iconSize=small&textSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const redNo2 = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=red&size=small&icon=car&iconSize=small&textSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const snowFlake = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=blue&size=small&icon=snowflake&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const temperatureLow = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=%23add8e6&size=small&icon=temperature-low&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const cloudSun = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=material&color=orange&size=small&icon=cloud-sun&iconType=awesome&iconSize=small&textSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const sonce = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=yellow&size=small&icon=sun&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const temperatureHigh = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=red&size=small&icon=temperature-high&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const padovinaSunIcon = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=%23add8e6&size=small&icon=sun&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const lightRain = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=blue&size=small&icon=cloud-sun&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const moderateRain = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=%2300008b&size=small&icon=cloud-showers-heavy&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const haevyRain = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=purple&size=small&icon=cloud-showers-heavy&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const calmWind = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=lightgreen&size=small&icon=wind&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const lightWind = new L.Icon({
  iconUrl:  'https://api.geoapify.com/v1/icon/?type=awesome&color=green&size=small&icon=wind&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const moderateWind = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=%23f4ce00&size=small&icon=wind&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});
const heavyWind = new L.Icon({
  iconUrl: 'https://api.geoapify.com/v1/icon/?type=awesome&color=%23ec9d00&size=small&icon=wind&iconSize=small&scaleFactor=2&apiKey=2b257b2140204b5abc2a90177e0a63d3',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const SetViewOnChange = ({ center, zoom }) => {
  const map = useMap();
  useEffect(() => {
    map.setView(center, zoom);
  }, [center, zoom, map]);
  return null;
};

const MapComponent = ({
  filter,
  center,
  setCenter,
  zoom,
  setZoom,
  isOpenCard,
  setIsOpenCard,
  selectedItem,
  setSelectedItem
}) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const isDarkMode = theme.palette.mode === "dark";
  const [data, setData] = useState([]);
  const bounds = [
    [44.89810538701363, 11.721979471289325],
    [47.88727867077649, 18.317427164768347],
  ];
  const lightTileLayer = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
  const darkTileLayer =
    "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png";

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(
          "http://localhost:3001/data/transformedData"
        );
        setData(response.data);
      } catch (error) {
        console.error("Failed to fetch data:", error);
      }
    };

    fetchData();
  }, []);

  const closeCard = () => {
    setIsOpenCard(false);
    setSelectedItem({});
    setCenter(DEFAULT_CENTER);
    setZoom(DEFAULT_ZOOM);
  };

  const filterData = (item) => {
    switch (filter) {
      case "temperature":
        return item.temperature !== undefined && item.temperature !== "";
      case "wind":
        return item.windSpeed !== undefined && item.windSpeed !== "";
      case "rain":
        return item.precipitation !== undefined && item.precipitation !== "";
      case "pm10":
        return item.pm10 !== undefined && item.pm10 !== "";
      case "pm25":
        return item.pm25 !== undefined && item.pm25 !== "";
      case "ozon":
        return item.ozon !== undefined && item.ozon !== "" && item.ozon !== "-";
      case "no2":
        return item.no2 !== undefined && item.no2 !== "";
      default:
        return false;
    }
  };

  const onMarkerClick = (item) => {
    setIsOpenCard(true);
    setSelectedItem(item);
    setCenter([item.location.latitude, item.location.longitude]);
    setZoom(11); // Zoom in to level 13 on marker click
  };

  const renderFilteredData = (item) => {
    switch (filter) {
      case "temperature":
        return `Temperature: ${item.temperature} °C`;
      case "wind":
        return `Wind Speed: ${item.windSpeed} m/s`;
      case "rain":
        return `Precipitation: ${item.precipitation} mm`;
      case "pm10":
        return `PM10: ${item.pm10} µg/m³`;
      case "pm25":
        return `PM2.5: ${item.pm25} µg/m³`;
      case "ozon":
        return `Ozone: ${item.ozon} µg/m³`;
      case "no2":
        return `NO2: ${item.no2} µg/m³`;
      default:
        return null;
    }
  };

  const getIcon = (item) => {

    if  (filter === 'temperature') {
      if (item.temperature < 0) {
        return snowFlake;
      } else if (item.temperature >= 0 && item.temperature < 10) {
        return temperatureLow;
      } else if (item.temperature >= 10 && item.temperature < 20) {
        return cloudSun;
      } else if (item.temperature >= 20 && item.temperature < 30) {
        return sonce;
      } else if (item.temperature >= 30) {
        return temperatureHigh;
      }
    }
    else if (filter === 'rain') {
      if (item.precipitation == 0) {
        return padovinaSunIcon;
      } else if (item.precipitation > 0 && item.precipitation <= 2.5) {
        return lightRain;
      } else if (item.precipitation > 2.5 && item.precipitation <= 7.5) {
        return moderateRain;
      } else if (item.precipitation > 7.5 && item.precipitation <= 50) {
        return haevyRain;
      }
    }
    else if (filter === 'wind') {
      if (item.windSpeed <= 2) {
        return calmWind;
      } else if (item.windSpeed <= 5.5) {
        return lightWind;
      } else if (item.windSpeed <= 10) {
        return moderateWind;
      } else if (item.windSpeed > 10) {
        return heavyWind;
      }
    }
    else if  (filter === 'pm10') {
      if (item.pm10 <= 54) {
        return greenPm10Icon;
      } else if (item.pm10 <= 254) {
        return orangePm10Icon;
      } else if (item.pm10 <= 354) {
        return redPm10Icon;
      }
    } else if (filter === 'pm25') {
      if (item.pm25 <= 12) {
        return greenpm25Icon;
      } else if (item.pm25 <= 55.4) {
        return orangepm25Icon;
      } else if (item.pm25 <= 150.4) {
        return redpm25Icon;
      }
    } else if (filter === 'ozon') {
      if (item.ozon <= 54) {
        return greenOzon;
      } else if (item.ozon <= 85) {
        return orangeOzon;
      } else if (item.ozon <= 200) {
        return redOzon;
      } else {
        return redOzon;
      }
    } else if (filter === 'no2') {
      if (item.no2 <= 53) {
        return greenNo2;
      }  else if (item.no2 <= 360) {
        return orangeNo2;
      } else if (item.no2 <= 649) {
        return redNo2;
      }
      else {
        return redNo2;
      }
    }
    return defaultIcon;
  };

  return (
    <div
      style={{
        height: "100vh",
        width: "100vw",
        position: "absolute",
        top: 0,
        left: 0,
      }}
    >
      {isOpenCard && (
        <PlaceCardComponent onClose={closeCard} item={selectedItem} />
      )}
      <MapContainer
        center={center}
        zoom={zoom}
        style={{ height: "100%", width: "100%" }}
        maxBounds={bounds}
        maxBoundsViscosity={1.0}
        minZoom={3}
        maxZoom={12}
        zoomControl={false}
        attributionControl={false}
      >
        <SetViewOnChange center={center} zoom={zoom} />
        <TileLayer
          url={lightTileLayer}
          attribution="&copy;"
          className={isDarkMode ? "dark-mode" : ""}
        />
        {data.filter(filterData).map((item, index) => (
          <Marker
            key={index}
            position={[item.location.latitude, item.location.longitude]}
            icon={getIcon(item)}
            eventHandlers={{
              click: () => onMarkerClick(item),
            }}
          ></Marker>
        ))}
        <ZoomControl position="bottomright" />
      </MapContainer>
      <style jsx global>{`
        .leaflet-tile,
        .leaflet-control-zoom-in,
        .leaflet-control-zoom-out,
        .leaflet-control-attribution {
          ${isDarkMode
            ? "filter: invert(100%) hue-rotate(180deg) brightness(95%) contrast(90%);"
            : ""}
        }
        .leaflet-container {
          background: ${colors.primary[400]};
        }
      `}</style>
    </div>
  );
};

export default MapComponent;
