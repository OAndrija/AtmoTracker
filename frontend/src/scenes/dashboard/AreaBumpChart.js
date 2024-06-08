import React from 'react';
import { ResponsiveAreaBump } from '@nivo/bump'

const AreaBumpChart = ({ data }) => {

    return (
        <ResponsiveAreaBump
            data={data}
            margin={{ top: 40, right: 100, bottom: 40, left: 100 }}
            spacing={11}
            xPadding={0.45}
            colors={{ scheme: 'nivo' }}
            blendMode="multiply"
            fillOpacity={0.9}
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
            borderColor={{
                from: 'color',
                modifiers: [
                    [
                        'darker',
                        '0.4'
                    ]
                ]
            }}
            startLabel={true}
            endLabel="id"
            axisTop={{
                tickSize: 5,
                tickPadding: 5,
                tickRotation: 0,
                legend: '',
                legendPosition: 'middle',
                legendOffset: -36,
                truncateTickAt: 0
            }}
            axisBottom={{
                tickSize: 5,
                tickPadding: 5,
                tickRotation: 0,
                legend: '',
                legendPosition: 'middle',
                legendOffset: 32,
                truncateTickAt: 0
            }}
        />
    );
}

export default AreaBumpChart;
