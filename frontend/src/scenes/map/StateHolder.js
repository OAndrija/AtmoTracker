import React, { useState } from 'react';
import Topbar from '../global/Topbar';
import MapComponent from './MapComponent';

const StateHolder = () => {
  const [filter, setFilter] = useState('temperature');  
  const [mapCenter, setMapCenter] = useState([46.056946, 14.505751]);

  const handleSuggestionClick = (suggestion) => {
    const lat = parseFloat(suggestion.location.latitude);
    const lon = parseFloat(suggestion.location.longitude);

    if (isNaN(lat) || isNaN(lon)) {
      console.error('Invalid latitude or longitude:', lat, lon);
      return;
    }

    console.log('Setting map center to:', lat, lon);
    setMapCenter([lat, lon]);
  };

  return (
    <div>
      <Topbar setFilter={setFilter}  onSuggestionClick={handleSuggestionClick}/>
      <MapComponent filter={filter} center={mapCenter} />
    </div>
  );
};
 
export default StateHolder;