var express = require('express');
var router = express.Router();
var qualityController = require('../controllers/qualityController.js');

/*
 * GET
 */
router.get('/', qualityController.list);

/*
 * GET
 */
router.get('/:id', qualityController.show);

/*
 * POST
 */
router.post('/', qualityController.create);

/*
 * PUT
 */
router.put('/:id', qualityController.update);

/*
 * DELETE
 */
router.delete('/:id', qualityController.remove);

module.exports = router;
