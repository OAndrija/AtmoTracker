import React, { useContext } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { useTheme } from '@mui/material/styles';

const position = [46.056946,14.505751];

const MapComponent = () => {
  const theme = useTheme();
  const isDarkMode = theme.palette.mode === 'dark';

  const lightTileLayer = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
  const darkTileLayer = "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png";

  return (
    <div style={{ height: '100vh', width: '100vw', position: 'absolute', top: 0, left: 0 }}>
      <MapContainer center={position} zoom={9} style={{ height: '100%', width: '100%' }}>
        <TileLayer
          url={isDarkMode ? darkTileLayer : lightTileLayer}
          attribution='&copy;'
        />
        
      </MapContainer>
    </div>
  );
};

export default MapComponent;