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
router.get('/:cityName', dataController.listDataForCity);

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
