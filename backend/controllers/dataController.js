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


    listAllWeatherData: function (req, res) {
        DataModel.find({
            $or: [
                { 'data.windGusts': { $exists: true } },
                { 'data.temperature': { $exists: true } },
                { 'data.windSpeed': { $exists: true } },
                { 'data.precipitation': { $exists: true } }
            ] // Check if at least one of the fields exists under the data object
        })
            .sort({ timestamp: 1 }) // Sort by timestamp in descending order
            .populate('data_series_id')
            .exec(function (err, weatherData) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when getting weather data.',
                        error: err
                    });
                }
    
                if (!weatherData || weatherData.length === 0) {
                    return res.status(404).json({
                        message: 'No weather data found.'
                    });
                }
    
                // Transform data into a unified structure
                const transformedData = weatherData.map(item => ({
                    windGusts: item.data?.get('windGusts') || null,
                    temperature: item.data?.get('temperature') || null,
                    windSpeed: item.data?.get('windSpeed') || null,
                    precipitation: item.data?.get('precipitation') || null,
                    location: item.data_series_id?.location || null,
                    name: item.data_series_id?.name || null,
                    timestamp: item.timestamp
                }));
    
                return res.json(transformedData);
            });
    },

    listAllAirQualityData: function (req, res) {
        DataModel.find({
            $or: [
                { 'data.pm10': { $exists: true } },
                { 'data.pm25': { $exists: true } },
                { 'data.so2': { $exists: true } },
                { 'data.co': { $exists: true } },
                { 'data.ozon': { $exists: true } },
                { 'data.no2': { $exists: true } },
                { 'data.benzen': { $exists: true } }
            ] // Check if at least one of the fields exists under the data object
        })
            .sort({ timestamp: 1 }) // Sort by timestamp in descending order
            .populate('data_series_id')
            .exec(function (err, weatherData) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when getting weather data.',
                        error: err
                    });
                }
    
                if (!weatherData || weatherData.length === 0) {
                    return res.status(404).json({
                        message: 'No weather data found.'
                    });
                }
    
                // Transform data into a unified structure
                const transformedData = weatherData.map(item => ({
                    pm10: item.data?.get('pm10') || null,
                    pm25: item.data?.get('pm25') || null,
                    so2: item.data?.get('so2') || null,
                    co: item.data?.get('co') || null,
                    ozon: item.data?.get('ozon') || null,
                    no2: item.data?.get('no2')|| null,
                    benzen: item.data?.get('benzen') || null,
                    location: item.data_series_id?.location || null,
                    name: item.data_series_id?.name || null,
                    timestamp: item.timestamp
                }));
    
                return res.json(transformedData);
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


    listCurrentWindSpeedData: function (req, res) {
        DataModel.find({
            'data.windSpeed': { $exists: true } // Check if windSpeed exists under the data object
        })
            .sort({ timestamp: -1 }) // Sort by the most recent data
            .populate('data_series_id')
            .exec(function (err, windSpeedData) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when getting wind speed data.',
                        error: err
                    });
                }
    
                const transformedData = windSpeedData.map(item => ({
                    windSpeed: item.data?.get('windSpeed'),
                    location: item.data_series_id.location,
                    name: item.data_series_id.name,
                    timestamp: item.timestamp
                }));
    
                return res.json(transformedData);
            });
    },
    listCurrentWindGustsData(req, res) {
        DataModel.find({
            'data.windGusts': { $exists: true } // Check if windGusts exists under the data object
        })
            .sort({ timestamp: -1 }) // Sort by timestamp in descending order
            .populate('data_series_id')
            .exec(function (err, windGustsData) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when getting wind gusts data.',
                        error: err
                    });
                }
    
                if (!windGustsData) {
                    return res.status(404).json({
                        message: 'No wind gusts data found.'
                    });
                }
    
                const transformedData = windGustsData.map(item => ({
                    windGusts: item.data?.get('windGusts'),
                    location: item.data_series_id.location,
                    name: item.data_series_id.name,
                    timestamp: item.timestamp
                }));
    
                return res.json(transformedData);
            });
    },

    listCurrentTemperature(req, res) {
        DataModel.find({
            'data.temperature': { $exists: true } // Check if temperature exists under the data object
        })
            .sort({ timestamp: -1 }) // Sort by timestamp in descending order
            .populate('data_series_id')
            .exec(function (err, temperatureData) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when getting temperature data.',
                        error: err
                    });
                }
    
                if (!temperatureData) {
                    return res.status(404).json({
                        message: 'No temperature data found.'
                    });
                }
    
                const transformedData = temperatureData.map(item => ({
                    temperature: item.data?.get('temperature'),
                    location: item.data_series_id.location,
                    name: item.data_series_id.name,
                    timestamp: item.timestamp
                }));
    
                return res.json(transformedData);
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
                // Instead of returning an error, simply return and skip further processing
                return res.status(200).json({
                    message: `Data series not found for ${req.body.name}, skipping file creation.`
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
