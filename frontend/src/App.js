import { ColorModeContext, useMode } from './theme';
import { CssBaseline, ThemeProvider, Box } from '@mui/material';
import { Routes, Route } from "react-router-dom";
import Topbar from './scenes/global/Topbar';
import Dashboard from './scenes/dashboard';
import CustomSidebar from './scenes/global/Sidebar';
// import WebSocket from './components/WebSocket';
// import Map from './scenes/map';

import MapComponent from './scenes/map/MapComponent';


function App() {
  const [theme, colorMode] = useMode();

  return (
    <ColorModeContext.Provider value={colorMode}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Box sx={{ display: 'flex', height: '100vh' }}>
          <CustomSidebar />
          <Box sx={{ display: 'flex', flexDirection: 'column', flex: 1 }}>
            <Topbar />
            <Box sx={{ flex: 1}}>
              <Routes>
                <Route path="/search/:cityName" element={<Dashboard />} />
                <Route path="/map" element={ <MapComponent />} />
              </Routes>
            </Box>
          </Box>
        </Box>
      </ThemeProvider>
    </ColorModeContext.Provider>
  );
}

export default App;
