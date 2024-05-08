var WeatherModel = require('../models/weatherModel.js');

/**
 * weatherController.js
 *
 * @description :: Server-side logic for managing weathers.
 */
module.exports = {

    /**
     * weatherController.list()
     */
    list: function (req, res) {
        WeatherModel.find(function (err, weathers) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting weather.',
                    error: err
                });
            }

            return res.json(weathers);
        });
    },

    /**
     * weatherController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        WeatherModel.findOne({_id: id}, function (err, weather) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting weather.',
                    error: err
                });
            }

            if (!weather) {
                return res.status(404).json({
                    message: 'No such weather'
                });
            }

            return res.json(weather);
        });
    },

    /**
     * weatherController.create()
     */
    create: function (req, res) {
        var weather = new WeatherModel({
			city_id : req.body.city_id,
			timestamp : req.body.timestamp,
			temperature : req.body.temperature,
			wind_speed : req.body.wind_speed,
			wind_gust : req.body.wind_gust,
			precipitation : req.body.precipitation
        });

        weather.save(function (err, weather) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating weather',
                    error: err
                });
            }

            return res.status(201).json(weather);
        });
    },

    /**
     * weatherController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        WeatherModel.findOne({_id: id}, function (err, weather) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting weather',
                    error: err
                });
            }

            if (!weather) {
                return res.status(404).json({
                    message: 'No such weather'
                });
            }

            weather.city_id = req.body.city_id ? req.body.city_id : weather.city_id;
			weather.timestamp = req.body.timestamp ? req.body.timestamp : weather.timestamp;
			weather.temperature = req.body.temperature ? req.body.temperature : weather.temperature;
			weather.wind_speed = req.body.wind_speed ? req.body.wind_speed : weather.wind_speed;
			weather.wind_gust = req.body.wind_gust ? req.body.wind_gust : weather.wind_gust;
			weather.precipitation = req.body.precipitation ? req.body.precipitation : weather.precipitation;
			
            weather.save(function (err, weather) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating weather.',
                        error: err
                    });
                }

                return res.json(weather);
            });
        });
    },

    /**
     * weatherController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        WeatherModel.findByIdAndRemove(id, function (err, weather) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the weather.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    }
};
