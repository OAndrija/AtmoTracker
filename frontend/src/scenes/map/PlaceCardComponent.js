import React, {useContext, useEffect} from 'react';
import {ColorModeContext, tokens} from '../../theme';
import {useTheme} from "@mui/material/styles";
import {Card, CardContent, CardHeader, IconButton, Typography, Grid} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';

function PlaceCardComponent({item, onClose}) {
    const theme = useTheme();
    const colors = tokens(theme.palette.mode);

    useEffect(() => {
        console.log(item);
    }, [item]);

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
                zIndex: 990
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
                        {item.name}
                    </Typography>
                }
                action={
                    <IconButton onClick={onClose}>
                        <CloseIcon/>
                    </IconButton>
                }
                sx={{
                    borderBottom: '1px solid #ddd',
                    marginBottom: 2,
                }}
            />
            <CardContent>
                {item.timestamp && (
                    <Typography variant="body1" fontSize={18}>
                        <strong>Timestamp</strong>: {new Date(item.timestamp).toLocaleString()}
                    </Typography>
                )}
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
