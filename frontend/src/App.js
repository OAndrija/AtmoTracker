import { ColorModeContext, useMode } from './theme';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { Routes, Route } from "react-router-dom"
import Topbar from './scenes/global/Topbar';
import Dashboard from './scenes/dashboard';
import Sidebar from './scenes/global/Sidebar';
// import WebSocket from './components/WebSocket';
// import Sidebar from './scenes/global/Sidebar';
// import Map from './scenes/map';

import MapComponent from './scenes/map/MapComponent';


function App() {
  const [theme, colorMode] = useMode();

  return (
    <ColorModeContext.Provider value={colorMode}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <div className="App">
      
          <main className="content">
            <Topbar />
            <MapComponent />
            <Routes>
              {/* <Route path="/" element={<Dashboard />} /> */}
              {/* <Route path="/map" element={<Map />} /> */}
            </Routes>
          </main>
        </div>
      </ThemeProvider>
    </ColorModeContext.Provider>
  );
}

export default App;
