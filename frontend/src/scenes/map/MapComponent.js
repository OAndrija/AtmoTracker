import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup, ZoomControl } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { useTheme } from '@mui/material/styles';
import axios from 'axios';

const position = [46.056946, 14.505751];
const bounds = [
  [45.4215, 13.3755],
  [46.8766, 16.5966]
];

const MapComponent = () => {
  const theme = useTheme();
  const isDarkMode = theme.palette.mode === 'dark';
  const [temperatureData, setTemperatureData] = useState([]);

  const lightTileLayer = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
  const darkTileLayer = "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png";

  useEffect(() => {
    const fetchTemperatureData = async () => {
      try {
        const response = await axios.get('http://localhost:3001/data/temperature');
        setTemperatureData(response.data);
      } catch (error) {
        console.error('Failed to fetch temperature:', error);
      }
    };

    fetchTemperatureData();
  }, []);

  return (
    <div style={{ height: '100vh', width: '100vw', position: 'absolute', top: 0, left: 0 }}>
      <MapContainer 
        center={position} 
        zoom={9} 
        style={{ height: '100%', width: '100%' }}
        maxBounds={bounds}
        maxBoundsViscosity={1.0}
        minZoom={7}
        maxZoom={9}
        zoomControl={false}
        attributionControl={false}
      >
        <TileLayer
          url={lightTileLayer}
          attribution='&copy;'
          className={isDarkMode ? 'dark-mode' : ''}
        />
        {temperatureData.map((data, index) => (
          <Marker key={index} position={[data.location.latitude, data.location.longitude]}>
            <Popup>
              <div>
                <strong>Name:</strong> {data.name}<br/>
                <strong>Temperature:</strong> {data.temperature} C<br/>
                <strong>Timestamp:</strong> {new Date(data.timestamp).toLocaleString()}
              </div>
            </Popup>
          </Marker>
        ))}
        <ZoomControl position="bottomright" /> 
      </MapContainer>
      <style jsx global>{`
        .leaflet-tile,
        .leaflet-control-zoom-in,
        .leaflet-control-zoom-out,
        .leaflet-control-attribution {
          ${isDarkMode ? 'filter: invert(100%) hue-rotate(180deg) brightness(95%) contrast(90%);' : ''}
        }
      `}</style>
    </div>
  );
};

export default MapComponent;
