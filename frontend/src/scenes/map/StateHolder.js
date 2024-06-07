import React, { useState } from 'react';
import Topbar from '../global/Topbar';
import MapComponent from './MapComponent';

const StateHolder = () => {
  const [filter, setFilter] = useState('temperature');
  console.log('Dashboard rendering, setFilter:', setFilter);
  return (
    <div>
      <Topbar setFilter={setFilter} />
      <MapComponent filter={filter} />
    </div>
  );
};
 
export default StateHolder;