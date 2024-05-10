var DataModel = require('../models/dataModel.js');
var DataseriesModel = require('../models/dataSeriesModel.js');
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
    // Find dataSeries by name
        DataSeries.findOne({ name: req.body.name }, function (err, series) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when fetching data series',
                    error: err
                });
            }

            if (!series) {
                return res.status(404).json({
                    message: 'No such data series exists'
                });
            }

            // Proceed with data creation using the found series _id
            var data = new DataModel({
                data_series_id: series._id,
                timestamp: req.body.timestamp,
                data: req.body.data
            });

            data.save(function (err, data) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when creating data',
                        error: err
                    });
                }

                return res.status(201).json(data);
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
