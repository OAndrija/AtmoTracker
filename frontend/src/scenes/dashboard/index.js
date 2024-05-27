import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

const Dashboard = () => {
    const { cityName } = useParams();
    const [data, setData] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get(`http://localhost:3001/data/${cityName}`);
                setData(response.data);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };

        fetchData();
    }, [cityName]);

    return (
        <div>
            <h1>Data for {cityName}</h1>
            {/* Render your data here */}
            {data.map((series, index) => (
                <div key={index}>
                    <h2>{series.id}</h2>
                    {series.data.map((point, idx) => (
                        <div key={idx}>
                            <span>{point.x}</span>: <span>{point.y}</span>
                        </div>
                    ))}
                </div>
            ))}
        </div>
    );
}

export default Dashboard;