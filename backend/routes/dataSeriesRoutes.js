var express = require('express');
var router = express.Router();
var dataSeriesController = require('../controllers/dataSeriesController.js');

/*
 * GET
 */
router.get('/location', dataSeriesController.list);
router.get('/weather', dataSeriesController.sendAllWeatherSeries);
router.get('/airquality', dataSeriesController.sendAllAirQualitySeries);
/*
 * GET
 */
router.get('/:id', dataSeriesController.show);

/*
 * POST
 */
router.post('/', dataSeriesController.create);

/*
 * PUT
 */
router.put('/:id', dataSeriesController.update);

/*
 * DELETE
 */
router.delete('/:id', dataSeriesController.remove);

module.exports = router;
