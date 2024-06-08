var express = require('express');
var router = express.Router();
var dataController = require('../controllers/dataController.js');

/*
 * GET
 */
router.get('/', dataController.list);
router.get('/all', dataController.listCurrentData);
router.get('/windSpeed', dataController.listCurrentWindSpeedData);
router.get('/windGusts', dataController.listCurrentWindGustsData);
router.get('/precipitation', dataController.listCurrentPrecipitationData);
router.get('/pm10', dataController.listCurrentPm10Data);
router.get('/pm25', dataController.listCurrentPm25Data);
router.get('/ozon', dataController.listCurrentOzonData);
router.get('/no2', dataController.listCurrentNo2Data);
router.get('/nearby',dataController.listNearbyData);
router.get('/ljubljanapiechart', dataController.listLjubljanaPieChartData);
router.get('/mariborpiechart', dataController.listMariborPieChartData);
router.get('/kranjpiechart', dataController.listKranjPieChartData);
router.get('/celjepiechart', dataController.listCeljePieChartData);
router.get('/cleanestcities', dataController.listLatestPM25Data);
// router.get('/:cityName', dataController.listAirQualityDataForCity);
router.get('/globalbarchart', dataController.listGlobalBarChartData);
router.get('/temperature',dataController.listCurrentTemperature);

/*
 * GET
 */
router.get('/:id', dataController.show);

/*
 * POST
 */
router.post('/', dataController.create);

/*
 * PUT
 */
router.put('/:id', dataController.update);

/*
 * DELETE
 */
router.delete('/:id', dataController.remove);

module.exports = router;
