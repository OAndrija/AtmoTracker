import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { useTheme } from '@mui/material/styles';
import axios from 'axios';
//import WbSunnyIcon from '@mui/icons-material/WbSunny';
const position = [46.056946, 14.505751]; 
const MapComponent = () => {

 

  const theme = useTheme();
  const isDarkMode = theme.palette.mode === 'dark';
  const [temperatureData, setTemperatureData] = useState([]); 

  const lightTileLayer = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
  const darkTileLayer = "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png";

  useEffect(() => {
    const fetchTemperatureData = async () => {
      try {
        const response = await axios.get('http://localhost:3001/data/allAirQuality'); 
        setTemperatureData(response.data);
      } catch (error) {
        console.error('Failed to fetch temperature:', error);
      }
    };

    fetchTemperatureData();
  }, []);

  return (
    <div style={{ height: '100vh', width: '100vw', position: 'absolute', top: 0, left: 0 }}>
      <MapContainer center={position} zoom={9} style={{ height: '100%', width: '100%' }}>
        <TileLayer
          url={isDarkMode ? darkTileLayer : lightTileLayer}
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>'
        />
        {temperatureData.map((data, index) => (
          <Marker key={index} position={[data.location.latitude, data.location.longitude]} >
            <Popup>
              <div>
                <strong>Name:</strong> {data.name}<br/>
                <strong>PM10:</strong> {data.pm10} µg/m<sup>3</sup><br/>
                <strong>PM2.5:</strong> {data.pm25} µg/m<sup>3</sup><br/>
                <strong>SO<sub>2</sub>:</strong> {data.so2} µg/m<sup>3</sup><br/>
                <strong>CO:</strong> {data.co} µg/m<sup>3</sup><br/>
                <strong>Ozone:</strong> {data.ozon} µg/m<sup>3</sup><br/>
                <strong>NO<sub>2</sub>:</strong> {data.no2} µg/m<sup>3</sup><br/>
                <strong>Benzen:</strong> {data.benzen} µg/m<sup>3</sup><br/>
                <strong>Timestamp:</strong> {new Date(data.timestamp).toLocaleString()}
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
};

export default MapComponent;
