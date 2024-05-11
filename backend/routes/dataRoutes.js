var express = require('express');
var router = express.Router();
var dataController = require('../controllers/dataController.js');

/*
 * GET
 */
router.get('/', dataController.list);
router.get('/all', dataController.listCurrentData);
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
