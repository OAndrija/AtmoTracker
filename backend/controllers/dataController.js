var DataModel = require('../models/dataModel.js');
var DataSeriesModel = require('../models/dataSeriesModel.js');
/**
 * dataController.js
 *
 * @description :: Server-side logic for managing datas.
 */
module.exports = {

    /**
     * dataController.list()
     */
    list: function (req, res) {
        DataModel.find(function (err, datas) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting data.',
                    error: err
                });
            }

            return res.json(datas);
        });
    },

    listGlobalBarChartData: async function (req, res) {
        try {
            // Names of the data series we are interested in
            const seriesNames = ["Weather Ljubljana", "Weather Maribor", "Weather Kranj"];
    
            console.log("Series Names:", seriesNames);
    
            // Find the data series with the specified names
            const dataSeries = await DataSeriesModel.find({ name: { $in: seriesNames } });
    
            console.log("Data Series found:", dataSeries);
    
            // Map series names to their IDs for easy lookup
            const seriesIdMap = dataSeries.reduce((acc, series) => {
                acc[series.name] = series._id;
                return acc;
            }, {});
    
            console.log("Series ID Map:", seriesIdMap);
    
            // Ensure we found all the necessary series
            if (Object.keys(seriesIdMap).length !== seriesNames.length) {
                console.log("One or more data series not found.");
                return res.status(404).json({
                    message: 'One or more data series not found.'
                });
            }
    
            // Find the most recent data for each series
            const latestDataPromises = seriesNames.map(name => 
                DataModel.findOne({ data_series_id: seriesIdMap[name] })
                    .sort({ timestamp: -1 })
                    .exec()
            );
    
            // Wait for all promises to resolve
            const latestDataResults = await Promise.all(latestDataPromises);
    
            console.log("Latest Data Results:", latestDataResults);
    
            // Define colors for each city
            const colors = {
                "Ljubljana": {
                    temperatureColor: "hsl(185, 70%, 50%)",
                    precipitationColor: "hsl(153, 70%, 50%)",
                    windspeedColor: "hsl(238, 70%, 50%)",
                    windgustColor: "hsl(127, 70%, 50%)"
                },
                "Maribor": {
                    temperatureColor: "hsl(178, 70%, 50%)",
                    precipitationColor: "hsl(233, 70%, 50%)",
                    windspeedColor: "hsl(250, 70%, 50%)",
                    windgustColor: "hsl(215, 70%, 50%)"
                },
                "Kranj": {
                    temperatureColor: "hsl(348, 70%, 50%)",
                    precipitationColor: "hsl(323, 70%, 50%)",
                    windspeedColor: "hsl(19, 70%, 50%)",
                    windgustColor: "hsl(303, 70%, 50%)"
                }
            };
    
            // Construct the response
            const responseData = seriesNames.map((name, index) => {
                const city = name.split(" ")[1];
                const data = latestDataResults[index].data;
                return {
                    city: city,
                    temperature: data.get('temperature'),
                    temperatureColor: colors[city].temperatureColor,
                    precipitation: data.get('precipitation'),
                    precipitationColor: colors[city].precipitationColor,
                    windspeed: data.get('windSpeed'),
                    windspeedColor: colors[city].windspeedColor,
                    windgust: data.get('windGusts'),
                    windgustColor: colors[city].windgustColor
                };
            });
    
            console.log("Response Data:", responseData);
    
            // Send the response
            return res.json(responseData);
        } catch (err) {
            console.error("Error when getting global bar chart data:", err);
            return res.status(500).json({
                message: 'Error when getting global bar chart data.',
                error: err
            });
        }
    },

    listNearbyData: function (req, res) {
        const { longitude, latitude } = req.query;//ce hocemo maxDistance dodamo tu

        if (!longitude || !latitude) {
            return res.status(400).json({
                message: 'Please provide both longitude and latitude'
            });
        }

        const radius = 5 / 6378.1; // 5 km radius in radians

        DataSeriesModel.find({
            location: {
                $geoWithin: {
                    $centerSphere: [
                        [parseFloat(latitude), parseFloat(longitude)],
                        radius
                        //parseFloat(maxDistance)
                    ]
                }
            }
        }).exec(function (err, dataSeries) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting nearby data series.',
                    error: err
                });
            }

            const dataSeriesIds = dataSeries.map(ds => ds._id);

            DataModel.find({
                data_series_id: { $in: dataSeriesIds }
            }).populate('data_series_id').exec(function (err, datas) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when getting data.',
                        error: err
                    });
                }

                return res.json(datas);
            });
        });
    },
    
    //lists all data and their corresponding data series object in the last hour
    listCurrentData: function (req, res) {
        const now = new Date();
        const startOfCurrentHour = new Date(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours());
    
        DataModel.find({
            timestamp: { $gte: startOfCurrentHour }
        }).populate('data_series_id').exec(function (err, datas) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting data.',
                    error: err
                });
            }
    
            return res.json(datas);
        });
    },

    listAirQualityDataForCity: async function (req, res) {
        try {
            const { cityName } = req.params;
            const now = new Date();
            const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate());
            const startOfYesterday = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1);
            const endOfYesterday = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0); // End of yesterday is the start of today
    
            // Find the data series by city name and air_quality in tags
            const dataSeries = await DataSeriesModel.findOne({ tags: { $all: [cityName, "air_quality"] } });
            if (!dataSeries) {
                return res.status(404).json({ message: 'City not found' });
            }
    
            // Find the data from the start of yesterday until now
            const data = await DataModel.find({
                data_series_id: dataSeries._id,
                timestamp: { $gte: startOfYesterday }
            }).exec();
    
            // List of pollutants
            const pollutants = ["pm10", "pm25", "so2", "co", "ozon", "no2", "benzen"];
    
            // Transform data into the required format
            const result = pollutants.map(pollutant => ({
                id: pollutant.toUpperCase(),
                data: [
                    { x: startOfYesterday.toISOString().split('T')[0], y: getValueForDate(data, pollutant, startOfYesterday, endOfYesterday) },
                    { x: startOfToday.toISOString().split('T')[0], y: getValueForDate(data, pollutant, startOfToday, now) }
                ]
            }));
    
            res.json(result);
        } catch (error) {
            res.status(500).json({
                message: 'Error when getting data.',
                error: error.message
            });
        }
    },

    listCurrentWindSpeedData: function (req, res) {
        const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);

        DataModel.find({
            timestamp: {$gte: oneHourAgo},
            'data.windSpeed': {$exists: true} // Check if windSpeed exists under the data object
        }).populate('data_series_id').select('data.windSpeed').exec(function (err, windSpeedData) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting wind speed data.',
                    error: err
                });
            }

            return res.json(windSpeedData);
        });
    },

    listCurrentWindGustsData(req, res) {
        const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);

        DataModel.find({
            timestamp: {$gte: oneHourAgo},
            'data.windGusts': {$exists: true} // Check if windSpeed exists under the data object
        }).populate('data_series_id').select('data.windGusts').exec(function (err, windGustsData) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting wind speed data.',
                    error: err
                });
            }

            return res.json(windGustsData);
        });
    },

    listCurrentPrecipitationData: function (req, res) {
        const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);

        DataModel.find({
            timestamp: {$gte: oneHourAgo},
            'data.precipitation': {$exists: true} // Check if windSpeed exists under the data object
        }).select('data.precipitation').exec(function (err, precipitationData) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting wind speed data.',
                    error: err
                });
            }

            return res.json(precipitationData);
        });
    },

    listCurrentPm10Data: function (req, res) {
        const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);

        DataModel.find({
            timestamp: {$gte: oneHourAgo},
            'data.pm10': {$exists: true} // Check if windSpeed exists under the data object
        }).populate('data_series_id').select('data.pm10').exec(function (err, pm10Data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting wind speed data.',
                    error: err
                });
            }

            return res.json(pm10Data);
        });
    },

    listCurrentPm25Data: function (req, res) {
        const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);

        DataModel.find({
            timestamp: {$gte: oneHourAgo},
            'data.pm25': {$exists: true} // Check if windSpeed exists under the data object
        }).populate('data_series_id').select('data.pm25').exec(function (err, pm25Data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting wind speed data.',
                    error: err
                });
            }

            return res.json(pm25Data);
        });
    },

    listCurrentOzonData: function (req, res) {
        const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);

        DataModel.find({
            timestamp: {$gte: oneHourAgo},
            'data.ozon': {$exists: true} // Check if windSpeed exists under the data object
        }).populate('data_series_id').select('data.ozon').exec(function (err, ozonData) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting wind speed data.',
                    error: err
                });
            }

            return res.json(ozonData);
        });
    },

    listCurrentNo2Data: function (req, res) {
        const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);

        DataModel.find({
            timestamp: {$gte: oneHourAgo},
            'data.no2': {$exists: true} // Check if windSpeed exists under the data object
        }).populate('data_series_id').select('data.no2').exec(function (err, no2Data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting wind speed data.',
                    error: err
                });
            }

            return res.json(no2Data);
        });
    },

    /**
     * dataController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        DataModel.findOne({_id: id}, function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting data.',
                    error: err
                });
            }

            if (!data) {
                return res.status(404).json({
                    message: 'No such data'
                });
            }

            return res.json(data);
        });
    },

    /**
     * dataController.create()
     */
    create: function (req, res) {
        // Log incoming request data
        console.log("Received request body:", req.body);

        // Validate incoming data
        if (!req.body.name || !req.body.timestamp || !req.body.data) {
            return res.status(400).json({
                message: 'Missing required fields'
            });
        }

        // Log the name being searched
        console.log("Searching for data series with name:", req.body.name);

        // Find dataSeries by name
        DataSeriesModel.findOne({name: req.body.name}, function (err, series) {
            if (err) {
                console.error('Error when fetching data series:', err);
                return res.status(500).json({
                    message: 'Error when fetching data series',
                    error: err
                });
            }

            if (!series) {
                console.log("No data series found with name:", req.body.name);
                return res.status(404).json({
                    message: 'No such data series exists'
                });
            }

            // Proceed with data creation using the found series _id
            var data = new DataModel({
                data_series_id: series._id,
                timestamp: new Date(req.body.timestamp), // Ensure timestamp is a Date object
                data: req.body.data
            });

            // Log the data to be saved
            console.log("Creating new data entry with:", data);

            data.save(function (err, savedData) {
                if (err) {
                    console.error('Error when creating data:', err);
                    return res.status(500).json({
                        message: 'Error when creating data',
                        error: err
                    });
                }

                console.log("Data created successfully:", savedData);
                return res.status(201).json(savedData);
            });
        });
    },

    /**
     * dataController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        DataModel.findOne({_id: id}, function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting data',
                    error: err
                });
            }

            if (!data) {
                return res.status(404).json({
                    message: 'No such data'
                });
            }

            data.data_series_id = req.body.data_series_id ? req.body.data_series_id : data.data_series_id;
            data.timestamp = req.body.timestamp ? req.body.timestamp : data.timestamp;
            data.data = req.body.data ? req.body.data : data.data;

            data.save(function (err, data) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating data.',
                        error: err
                    });
                }

                return res.json(data);
            });
        });
    },

    /**
     * dataController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        DataModel.findByIdAndRemove(id, function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the data.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    },
};

function getValueForDate(data, pollutant, startDate, endDate) {
    console.log(`Filtering data for pollutant: ${pollutant} from ${startDate.toISOString()} to ${endDate.toISOString()}`);
    const filteredData = data.filter(d => {
        const dataDate = new Date(d.timestamp);
        return dataDate >= startDate && dataDate < endDate;
    });

    console.log(`Filtered data for ${startDate.toISOString().split('T')[0]}:`, filteredData);

    if (filteredData.length > 0) {
        const latestDataPoint = filteredData[filteredData.length - 1]; // Get the latest data point for the date range
        if (latestDataPoint.data && typeof latestDataPoint.data.get === 'function') {
            const pollutantValue = latestDataPoint.data.get(pollutant);
            return pollutantValue !== '' ? pollutantValue : null;
        }
    }

    return null;
}
