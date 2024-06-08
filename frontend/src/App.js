import { ColorModeContext, useMode } from './theme';
import { CssBaseline, ThemeProvider, Box } from '@mui/material';
import { Routes, Route } from "react-router-dom";
import Topbar from './scenes/global/Topbar';
import Dashboard from './scenes/dashboard';
import CustomSidebar from './scenes/global/Sidebar';
// import WebSocket from './components/WebSocket';
// import Map from './scenes/map';

import MapComponent from './scenes/map/MapComponent';
import AreaBumpChart from './scenes/dashboard/AreaBumpChart';


function App() {
  const [theme, colorMode] = useMode();

  return (
    <ColorModeContext.Provider value={colorMode}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Box sx={{ display: 'flex', height: '100vh' }}>
          <CustomSidebar />
          <Box sx={{ display: 'flex', flexDirection: 'column', flex: 1, height: '100vh' }}>
            <Topbar />
            <Box sx={{ flex: 1, overflow: 'auto'}}>
              <Routes>
                <Route path="/map" element={ <MapComponent />} />
                <Route path="/dashboard" element={<Dashboard />} />
              </Routes>
            </Box>
          </Box>
        </Box>
      </ThemeProvider>
    </ColorModeContext.Provider>
  );
}

export default App;
