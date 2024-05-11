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

    //lists all data and their corresponding data series object in the last hour
    listCurrentData: function (req, res) {
        const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);

        DataModel.find({
            timestamp: {$gte: oneHourAgo}
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
        const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);

        DataModel.find({
            timestamp: {$gte: oneHourAgo},
            'data.windSpeed': {$exists: true} // Check if windSpeed exists under the data object
        }).select('data.windSpeed').exec(function (err, windSpeedData) {
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
        }).select('data.windGusts').exec(function (err, windGustsData) {
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
        }).select('data.pm10').exec(function (err, pm10Data) {
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
        }).select('data.pm25').exec(function (err, pm25Data) {
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
        }).select('data.ozon').exec(function (err, ozonData) {
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
        }).select('data.no2').exec(function (err, no2Data) {
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
    }
};
