import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { useTheme } from '@mui/material/styles';
import axios from 'axios';
import L from 'leaflet';
import { FaSnowflake, FaTemperatureLow, FaCloudSun, FaSun, FaTemperatureHigh, FaIndustry, FaSmog, FaCar, FaShieldAlt } from 'react-icons/fa';
import { renderToString } from 'react-dom/server';

const position = [46.056946, 14.505751];

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

/*/
const createIcon = (icon, color) => {
  return L.divIcon({
    html: renderToString(
      <div style={{
        position: 'relative',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        width: '35px',
        height: '35px',
      }}>
        <div style={{
          position: 'absolute',
          width: '30px',
          height: '30px',
          backgroundColor: '#fff',
          borderRadius: '50%',
          border: `2px solid ${color}`,
          boxShadow: '0 0 5px rgba(0,0,0,0.5)'
        }}>
          <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            width: '100%',
            height: '100%',
            color: color
          }}>
            {icon}
          </div>
        </div>
        <div style={{
          position: 'absolute',
          bottom: '-10px',
          width: '10px',
          height: '10px',
          backgroundColor: color,
          transform: 'rotate(45deg)'
        }}></div>
      </div>
    ),
    iconSize: [35, 45],
    className: 'custom-icon'
  });
}; /*/

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
        console.error('Failed to fetch data:', error);
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
    /*/if (filter === 'temperature') {
      if (item.temperature < 0) {
        return createIcon(<FaSnowflake />, 'blue');
      } else if (item.temperature >= 0 && item.temperature < 10) {
        return createIcon(<FaTemperatureLow />, 'lightblue');
      } else if (item.temperature >= 10 && item.temperature < 20) {
        return createIcon(<FaCloudSun />, 'orange');
      } else if (item.temperature >= 20 && item.temperature < 30) {
        return createIcon(<FaSun />, 'yellow');
      } else if (item.temperature >= 30) {
        return createIcon(<FaTemperatureHigh />, 'red');
      }/*/
      if (filter === 'pm10') {
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
    <div style={{ height: '100vh', width: '100vw', position: 'absolute', top: 0, left: 0 }}>
      <MapContainer center={position} zoom={9} style={{ height: '100%', width: '100%' }}>
        <TileLayer
          url={isDarkMode ? darkTileLayer : lightTileLayer}
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>'
        />
        {data.filter(filterData).map((item, index) => (
          <Marker 
            key={index} 
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
