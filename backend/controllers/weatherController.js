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
        const weatherDataList = req.body.weatherTableRows;
    
        let operationPromises = [];
    
        weatherDataList.forEach(weatherData => {
            // First, find the city by name
            operationPromises.push(
                CityModel.findOne({ name: weatherData.city }).exec()
                .then(city => {
                    if (!city) {
                        // If no city is found, throw an error or handle it appropriately
                        throw new Error(`City not found: ${weatherData.city}`);
                    }
    
                    // Create a new weather model
                    var weather = new WeatherModel({
                        city_id : city._id,  // Use the found city's ID
                        temperature : weatherData.temperature,
                        wind_speed : weatherData.windSpeed,
                        wind_gust : weatherData.windGusts,
                        precipitation : weatherData.precipitation
                    });
    
                    // Save the weather data, update the city and return the promise
                    return weather.save()
                    .then(savedWeather => {
                        // Update the city's weather_series array
                        city.weather_series.push(savedWeather._id);
                        // Save the updated city
                        return city.save().then(() => savedWeather);
                    });

                })
            );
        });
    
        // Use Promise.all to handle all operations
        Promise.all(operationPromises)
            .then(result => {
                // All operations have completed successfully
                return res.status(201).json(result);
            })
            .catch(error => {
                // If any operation failed
                return res.status(500).json({
                    message: 'Error when creating weather',
                    error: error.message || error
                });
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
