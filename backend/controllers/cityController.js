var CityModel = require('../models/cityModel.js');

/**
 * cityController.js
 *
 * @description :: Server-side logic for managing citys.
 */
module.exports = {

    /**
     * cityController.list()
     */
    list: function (req, res) {
        CityModel.find(function (err, citys) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting city.',
                    error: err
                });
            }

            return res.json(citys);
        });
    },

    /**
     * cityController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        CityModel.findOne({_id: id}, function (err, city) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting city.',
                    error: err
                });
            }

            if (!city) {
                return res.status(404).json({
                    message: 'No such city'
                });
            }

            return res.json(city);
        });
    },

    /**
     * cityController.create()
     */
    create: function (req, res) {
        var city = new CityModel({
			name : req.body.name,
			location : req.body.location,
			weather_series : req.body.weather_series,
			quality_series : req.body.quality_series
        });

        city.save(function (err, city) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating city',
                    error: err
                });
            }

            return res.status(201).json(city);
        });
    },

    /**
     * cityController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        CityModel.findOne({_id: id}, function (err, city) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting city',
                    error: err
                });
            }

            if (!city) {
                return res.status(404).json({
                    message: 'No such city'
                });
            }

            city.name = req.body.name ? req.body.name : city.name;
			city.location = req.body.location ? req.body.location : city.location;
			city.weather_series = req.body.weather_series ? req.body.weather_series : city.weather_series;
			city.quality_series = req.body.quality_series ? req.body.quality_series : city.quality_series;
			
            city.save(function (err, city) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating city.',
                        error: err
                    });
                }

                return res.json(city);
            });
        });
    },

    /**
     * cityController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        CityModel.findByIdAndRemove(id, function (err, city) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the city.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    }
};
