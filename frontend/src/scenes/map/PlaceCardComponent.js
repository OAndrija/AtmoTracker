import React, { useContext, useEffect, useState } from 'react';
import { ColorModeContext, tokens } from '../../theme';
import { useTheme } from "@mui/material/styles";
import { Card, CardContent, CardHeader, IconButton, Typography, Grid, Box } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { keyframes } from '@mui/system';
import axios from 'axios';

const slideIn = keyframes`
  from {
    transform: translateX(-100%);
  }
  to {
    transform: translateX(0);
  }
`;

const slideOut = keyframes`
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-100%);
  }
`;

function PlaceCardComponent({ item, onClose }) {
    const theme = useTheme();
    const colors = tokens(theme.palette.mode);
    const [visible, setVisible] = useState(true);
    const [imageUrl, setImageUrl] = useState('');

    useEffect(() => {
        console.log('Item:', item);
        fetchImage(item.name);
    }, [item]);

    const handleClose = () => {
        setVisible(false);
        setTimeout(() => {
            onClose();
        }, 500); // Duration of the slide-out animation
    };

    const fetchImage = async (cityName) => {
        try {
            if (!process.env.REACT_APP_UNSPLASH_ACCESS_KEY) {
                console.error('Unsplash access key is not set');
                return;
            }

            const fetchFromUnsplash = async (query) => {
                const response = await axios.get(`https://api.unsplash.com/search/photos`, {
                    params: { query, per_page: 1 },
                    headers: {
                        Authorization: `Client-ID ${process.env.REACT_APP_UNSPLASH_ACCESS_KEY}`
                    }
                });
                return response.data.results;
            };

            console.log('Fetching image for:', cityName);
            let results = await fetchFromUnsplash(cityName);
            if (results.length === 0) {
                const nameWithoutFirstWord = cityName.split(' ').slice(1).join(' ');
                console.log('Fetching image for:', nameWithoutFirstWord);
                results = await fetchFromUnsplash(nameWithoutFirstWord);
            }

            if (results.length > 0) {
                console.log('Image found:', results[0].urls.small);
                setImageUrl(results[0].urls.small);
            } else {
                console.log('No image found for:', cityName);
            }
        } catch (error) {
            console.error('Error fetching image:', error);
        }
    };

    const nameWithoutFirstWord = item.name.split(' ').slice(1).join(' ');

    return (
        <Card
            sx={{
                position: 'fixed',
                top: 0,
                left: 70,
                width: 440,
                paddingTop: 8,
                paddingBottom: 2,
                paddingLeft: 1,
                backgroundColor: colors.primary[400],
                borderRadius: 0,
                zIndex: 990,
                animation: `${visible ? slideIn : slideOut} 0.5s forwards`
            }}
        >
            <CardHeader
                title={
                    <Typography
                        variant="body1"
                        sx={{
                            fontSize: '24px',
                            fontWeight: 'bold',
                        }}
                    >
                        {nameWithoutFirstWord}
                    </Typography>
                }
                action={
                    <IconButton onClick={handleClose}>
                        <CloseIcon />
                    </IconButton>
                }
                sx={{
                    borderBottom: '1px solid #ddd',
                    marginBottom: 2,
                }}
            />
            {imageUrl && (
                <Box
                    component="img"
                    sx={{
                        height: 200,
                        width: '100%',
                        objectFit: 'cover',
                        borderBottom: '1px solid #ddd',
                    }}
                    src={imageUrl}
                    alt={nameWithoutFirstWord}
                />
            )}
            <CardContent>
                {item.temperature && (
                    <Typography variant="body1" fontSize={18}>
                        <strong>Temperature</strong>: {item.temperature} °C
                    </Typography>
                )}
                {item.windGusts && (
                    <Typography variant="body1" fontSize={18}>
                        <strong>Wind Gusts</strong>: {item.windGusts} m/s
                    </Typography>
                )}
                {item.windSpeed && (
                    <Typography variant="body1" fontSize={18}>
                        <strong>Wind Speed</strong>: {item.windSpeed} m/s
                    </Typography>
                )}
                {item.precipitation && (
                    <Typography variant="body1" fontSize={18}>
                        <strong>Precipitation</strong>: {item.precipitation} mm
                    </Typography>
                )}
                {item.pm10 && (
                    <Typography variant="body1" fontSize={18}>
                        <strong>PM10</strong>: {item.pm10} µg/m³
                    </Typography>
                )}
                {item.pm25 && (
                    <Typography variant="body1" fontSize={18}>
                        <strong>PM2.5</strong>: {item.pm25} µg/m³
                    </Typography>
                )}
                {item.ozon && (
                    <Typography variant="body1" fontSize={18}>
                        <strong>Ozone</strong>: {item.ozon} µg/m³
                    </Typography>
                )}
                {item.no2 && (
                    <Typography variant="body1" fontSize={18}>
                        <strong>NO2</strong>: {item.no2} µg/m³
                    </Typography>
                )}
            </CardContent>
        </Card>
    );
}

export default PlaceCardComponent;
