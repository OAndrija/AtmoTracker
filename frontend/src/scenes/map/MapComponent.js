import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { useTheme } from '@mui/material/styles';
import axios from 'axios';
//import WbSunnyIcon from '@mui/icons-material/WbSunny';
const position = [46.056946, 14.505751]; // Default center position for the map
const MapComponent = () => {

 

  const theme = useTheme();
  const isDarkMode = theme.palette.mode === 'dark';
  const [temperatureData, setTemperatureData] = useState([]); // State to store wind gust data

  const lightTileLayer = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
  const darkTileLayer = "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png";

  // Fetch wind gust data from the server when the component mounts
  useEffect(() => {
    const fetchTemperatureData = async () => {
      try {
        const response = await axios.get('http://localhost:3001/data/temperature'); // Modify this URL to match your API endpoint
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
                <strong>Temperature:</strong> {data.temperature} C<br/>
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