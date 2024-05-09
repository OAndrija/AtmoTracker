var QualityModel = require('../models/qualityModel.js');

/**
 * qualityController.js
 *
 * @description :: Server-side logic for managing qualitys.
 */
module.exports = {
    /**
     * qualityController.list()
     */
    list: function (req, res) {
        QualityModel.find(function (err, qualitys) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting quality.',
                    error: err
                });
            }

            return res.json(qualitys);
        });
    },
    /**
     * qualityController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        QualityModel.findOne({_id: id}, function (err, quality) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting quality.',
                    error: err
                });
            }

            if (!quality) {
                return res.status(404).json({
                    message: 'No such quality'
                });
            }

            return res.json(quality);
        });
    },
    /**
     * qualityController.create()
     */
    create: function (req, res) {
        const qualityDataList = req.body.qualityTableRows;
        let operationPromises = [];
    
        qualityDataList.forEach(qualityData => {
            operationPromises.push(
                CityModel.findOne({ name: qualityData.city }).exec()
                .then(city => {
                    if (!city) {
                        throw new Error(`City not found: ${qualityData.city}`);
                    }
    
                    // Create a new weather model
                    var quality = new QualityModel({
                        city_id: city._id,
                        timestamp: qualityData.timestamp,
                        pm10: qualityData.pm10,
                        pm25: qualityData.pm25,
                        so2: qualityData.so2,
                        co: qualityData.co,
                        ozon: qualityData.ozon,
                        no2: qualityData.no2,
                        benzen: qualityData.benzen
                    });
    
                    // Save the weather data
                    return quality.save()
                        .then(savedQuality => {
                            // Update the city's weather_series array
                            city.quality_series.push(savedQuality._id);
                            // Save the updated city
                            return city.save().then(() => savedQuality);
                        });
                })
            );
        });
    
        // Once all operations are complete, respond to the client
        Promise.all(operationPromises)
            .then(results => {
                res.status(200).json({
                    message: "Weather data successfully saved and city updated",
                    data: results
                });
            })
            .catch(error => {
                res.status(500).json({
                    message: "Error saving weather data",
                    error: error.message
                });
            });
    },
    /**
     * qualityController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        QualityModel.findOne({_id: id}, function (err, quality) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting quality',
                    error: err
                });
            }

            if (!quality) {
                return res.status(404).json({
                    message: 'No such quality'
                });
            }

            quality.city_id = req.body.city_id ? req.body.city_id : quality.city_id;
			quality.timestamp = req.body.timestamp ? req.body.timestamp : quality.timestamp;
			quality.pm10 = req.body.pm10 ? req.body.pm10 : quality.pm10;
			quality.pm25 = req.body.pm25 ? req.body.pm25 : quality.pm25;
			quality.so2 = req.body.so2 ? req.body.so2 : quality.so2;
			quality.co = req.body.co ? req.body.co : quality.co;
			quality.ozon = req.body.ozon ? req.body.ozon : quality.ozon;
			quality.no2 = req.body.no2 ? req.body.no2 : quality.no2;
			quality.benzen = req.body.benzen ? req.body.benzen : quality.benzen;
			
            quality.save(function (err, quality) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating quality.',
                        error: err
                    });
                }

                return res.json(quality);
            });
        });
    },

    /**
     * qualityController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        QualityModel.findByIdAndRemove(id, function (err, quality) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the quality.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    }
};
