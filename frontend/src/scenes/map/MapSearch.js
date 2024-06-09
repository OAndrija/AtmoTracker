import React, { useState, useEffect } from 'react';
import { Box, InputBase, IconButton, List, ListItem, ListItemText } from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import axios from 'axios';

const MapSearch = ({ onSuggestionClick, colors }) => {
  const [query, setQuery] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [locations, setLocations] = useState([]);

  useEffect(() => {
    // Fetch available locations from your backend
    const fetchLocations = async () => {
      try {
        const response = await axios.get('http://localhost:3001/dataseries/location'); // Adjust the endpoint as necessary
        setLocations(response.data);
      } catch (error) {
        console.error('Failed to fetch locations:', error);
      }
    };

    fetchLocations();
  }, []);

  const handleInputChange = (e) => {
    const value = e.target.value;
    setQuery(value);

    if (value.length > 2) {
      const filtered = locations.filter(location =>
        location.tags[1][0].toLowerCase().includes(value.toLowerCase())
      );
      setSuggestions(filtered);
    } else {
      setSuggestions([]);
    }
  };

  const handleSuggestionClick = (suggestion) => {
    console.log('Suggestion clicked:', suggestion);
    setQuery('');
    setSuggestions([]);
    onSuggestionClick(suggestion);
  };

  return (
    <Box display="flex" flexDirection="column" position="relative">
      <Box display="flex" backgroundColor={colors.primary[400]} borderRadius="35px" sx={{ ml: 1, width: '400px', height: '42px', boxShadow: '0px 4px 10px rgba(0,0,0, 0.2)' }}>
        <InputBase
          sx={{ ml: 3, flex: 1, fontSize: 15 }}
          placeholder="Search"
          value={query}
          onChange={handleInputChange}
        />
        <IconButton type="button" sx={{ p: 1, mr: 2 }}>
          <SearchIcon />
        </IconButton>
      </Box>
      {suggestions.length > 0 && (
        <List
          sx={{
            position: 'absolute',
            top: '50px',
            left: 0,
            right: 0,
            backgroundColor: '#fff',
            boxShadow: '0px 4px 10px rgba(0,0,0, 0.1)',
            borderRadius: '5px',
            zIndex: 1000,
            maxHeight: '200px',
            overflow: 'auto',
          }}
        >
          {suggestions.map((suggestion, index) => (
            <ListItem button key={index} onClick={() => handleSuggestionClick(suggestion)}>
              <ListItemText
                primary={suggestion.tags[1][0]}
                secondary={`Lat: ${suggestion.location.latitude}, Lon: ${suggestion.location.longitude}`}
              />
            </ListItem>
          ))}
        </List>
      )}
    </Box>
  );
};

export default MapSearch;
