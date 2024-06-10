import React, { useState } from 'react';
import Topbar from '../global/Topbar';
import MapComponent from './MapComponent';

const StateHolder = () => {
  const [filter, setFilter] = useState('temperature');  
  const [mapCenter, setMapCenter] = useState([46.056946, 14.505751]);
  const [isOpenCard, setIsOpenCard] = useState(false);
  const [selectedItem, setSelectedItem] = useState({});
  const [mapZoom, setMapZoom] = useState(9);

  const handleSuggestionClick = (suggestion) => {
    const lat = parseFloat(suggestion.location.latitude);
    const lon = parseFloat(suggestion.location.longitude);

    if (isNaN(lat) || isNaN(lon)) {
      console.error('Invalid latitude or longitude:', lat, lon);
      return;
    }

    console.log('Setting map center to:', lat, lon);
    setMapCenter([lat, lon]);
    setMapZoom(11); // Zoom in on search result click
    setSelectedItem(suggestion); // Set the selected item to the clicked suggestion
    setIsOpenCard(true); // Open the place card
  };

  const closeCard = () => {
    setIsOpenCard(false);
    setSelectedItem({});
    setMapCenter([46.056946, 14.505751]); // Reset to default center
    setMapZoom(9); // Reset to default zoom
  };

  return (
    <div>
      <Topbar setFilter={setFilter} onSuggestionClick={handleSuggestionClick} />
      <MapComponent
        filter={filter}
        center={mapCenter}
        setCenter={setMapCenter}
        zoom={mapZoom}
        setZoom={setMapZoom}
        isOpenCard={isOpenCard}
        closeCard={closeCard}
        selectedItem={selectedItem}
      />
    </div>
  );
};

export default StateHolder;
