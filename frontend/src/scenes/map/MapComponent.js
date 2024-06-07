import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { useTheme } from '@mui/material/styles';
import axios from 'axios';
import L from 'leaflet';
import { BsThermometerSun } from "react-icons/bs";
import { renderToString } from 'react-dom/server';
import { FaThermometerThreeQuarters, FaThermometerHalf } from "react-icons/fa";

const position = [46.056946, 14.505751];

const highTempIcon = new L.DivIcon({
  html: renderToString(<BsThermometerSun style={{ color: 'red', fontSize: '24px' }} />),
  className: '',
  iconSize: [24, 24],
  iconAnchor: [12, 24],
  popupAnchor: [0, -24]
});

const midTempIcon = new L.DivIcon({
  html: renderToString(<FaThermometerThreeQuarters style={{ color: 'orange', fontSize: '24px' }} />),
  className: '',
  iconSize: [24, 24],
  iconAnchor: [12, 24],
  popupAnchor: [0, -24]
});

const lowMidTempIcon = new L.DivIcon({
  html: renderToString(<FaThermometerHalf style={{ color: 'yellow', fontSize: '24px' }} />),
  className: '',
  iconSize: [24, 24],
  iconAnchor: [12, 24],
  popupAnchor: [0, -24]
});

const defaultIcon = new L.Icon({
  iconUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const MapComponent = ({ filter }) => {
  const theme = useTheme();
  const isDarkMode = theme.palette.mode === 'dark';
  const [data, setData] = useState([]);

  const lightTileLayer = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
  const darkTileLayer = "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png";

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get('http://localhost:3001/data/transformedData');
        setData(response.data);
      } catch (error) {
        console.error('Failed to fetch temperature:', error);
      }
    };

    fetchData();
  }, [position, filter]);

  const filterData = (item) => {
    switch (filter) {
      case 'temperature':
        return item.temperature !== undefined && item.temperature !== '';
      case 'wind':
        return item.windSpeed !== undefined && item.windSpeed !== '';
      case 'rain':
        return item.precipitation !== undefined && item.precipitation !== '';
      case 'pm10':
        return item.pm10 !== undefined && item.pm10 !== '';
      case 'pm25':
        return item.pm25 !== undefined && item.pm25 !== '';
      case 'ozon':
        return item.ozon !== undefined && item.ozon !== '';
      case 'no2':
        return item.no2 !== undefined && item.no2 !== '';
      default:
        return false;
    }
  };

  const renderFilteredData = (item) => {
    switch (filter) {
      case 'temperature':
        return `Temperature: ${item.temperature} °C`;
      case 'wind':
        return `Wind Speed: ${item.windSpeed} m/s`;
      case 'rain':
        return `Precipitation: ${item.precipitation} mm`;
      case 'pm10':
        return `PM10: ${item.pm10} µg/m³`;
      case 'pm25':
        return `PM2.5: ${item.pm25} µg/m³`;
      case 'ozon':
        return `Ozone: ${item.ozon} µg/m³`;
      case 'no2':
        return `NO2: ${item.no2} µg/m³`;
      default:
        return null;
    }
  };

  const getIcon = (item) => {
    if (filter === 'temperature') {
      if (item.temperature > 25) {
        return highTempIcon;
      } else if (item.temperature >= 20 && item.temperature <= 25) {
        return midTempIcon;
      }
      else if (item.temperature >= 15 && item.temperature <= 19){
        return lowMidTempIcon;
      }
    }
    return defaultIcon;
  };

  return (
    <div style={{ height: '100vh', width: '100vw', position: 'absolute', top: 0, left: 0 }}>
      <MapContainer center={position} zoom={9} style={{ height: '100%', width: '100%' }}>
        <TileLayer
          url={isDarkMode ? darkTileLayer : lightTileLayer}
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>'
        />
        {data.filter(filterData).map((item, index) => (
          <Marker key={index} 
          position={[item.location.latitude, item.location.longitude]}
          icon={getIcon(item)}
          >
            <Popup>
              <div>
                <strong>Name:</strong> {item.name}<br />
                {renderFilteredData(item)}<br />
                <strong>Timestamp:</strong> {new Date(item.timestamp).toLocaleString()}
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
};

export default MapComponent;
