import { useTheme } from "@mui/material";
import { ResponsiveBar } from "@nivo/bar";
import { tokens } from "../../theme";
import { useState, useEffect } from "react";
import axios from "axios";

const BarChart = () => {
    const theme = useTheme();
    const colors = tokens(theme.palette.mode);
    const [barChartData, setData] = useState([]);

    useEffect(() => {
        // Fetch data from the API
        axios.get('http://localhost:3001/data/globalbarchart')
            .then(response => {
                console.log("Fetched data:", response.data);
                setData(response.data);
            })
            .catch(error => {
                console.error("Error fetching data:", error);
            });
    }, []);

    return (
        <ResponsiveBar
            data={barChartData}
            keys={[
                'temperature',
                'precipitation',
                'windspeed',
                'windgust'
            ]}
            indexBy="city"
            margin={{ top: 50, right: 130, bottom: 50, left: 60 }}
            padding={0.3}
            valueScale={{ type: 'linear' }}
            indexScale={{ type: 'band', round: true }}
            colors={({ id, data }) => data[`${id}Color`]}
            defs={[
                {
                    id: 'dots',
                    type: 'patternDots',
                    background: 'inherit',
                    color: '#38bcb2',
                    size: 4,
                    padding: 1,
                    stagger: true
                },
                {
                    id: 'lines',
                    type: 'patternLines',
                    background: 'inherit',
                    color: '#eed312',
                    rotation: -45,
                    lineWidth: 6,
                    spacing: 10
                }
            ]}
            fill={[
                {
                    match: {
                        id: 'precipitation'
                    },
                    id: 'dots'
                },
                {
                    match: {
                        id: 'windspeed'
                    },
                    id: 'lines'
                }
            ]}
            borderColor={{
                from: 'color',
                modifiers: [
                    [
                        'darker',
                        1.6
                    ]
                ]
            }}
            axisTop={null}
            axisRight={null}
            axisBottom={{
                tickSize: 5,
                tickPadding: 5,
                tickRotation: 0,
                legend: 'city',
                legendPosition: 'middle',
                legendOffset: 32,
                truncateTickAt: 0
            }}
            axisLeft={{
                tickSize: 5,
                tickPadding: 5,
                tickRotation: 0,
                legend: 'µg/m³',
                legendPosition: 'middle',
                legendOffset: -40,
                truncateTickAt: 0
            }}
            labelSkipWidth={12}
            labelSkipHeight={12}
            labelTextColor={{
                from: 'color',
                modifiers: [
                    [
                        'darker',
                        1.6
                    ]
                ]
            }}
            legends={[
                {
                    dataFrom: 'keys',
                    anchor: 'bottom-right',
                    direction: 'column',
                    justify: false,
                    translateX: 120,
                    translateY: 0,
                    itemsSpacing: 2,
                    itemWidth: 100,
                    itemHeight: 20,
                    itemDirection: 'left-to-right',
                    itemOpacity: 0.85,
                    symbolSize: 20,
                    effects: [
                        {
                            on: 'hover',
                            style: {
                                itemOpacity: 1
                            }
                        }
                    ]
                }
            ]}
            role="application"
            ariaLabel="Nivo bar chart demo"
            barAriaLabel={e=>e.id+": "+e.formattedValue+" in city: "+e.indexValue}
        />
    );
};

export default BarChart;
