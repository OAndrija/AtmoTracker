import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import AreaBumpChart from './AreaBumpChart';

const Dashboard = () => {
    const { cityName } = useParams();
    const [currentAirQualityData, setAirQualityData] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get(`http://localhost:3001/data/${cityName}`);
                setAirQualityData(response.data);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };

        fetchData();
    }, [cityName]);

    return (
        <div>
            <h1>Data for {cityName}</h1>
            {/* Render your AreaBumpC hart here */}
            <AreaBumpChart data={currentAirQualityData} />
        </div>
    );
}

export default Dashboard;
