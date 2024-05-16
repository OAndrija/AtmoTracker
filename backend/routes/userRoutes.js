var express = require('express');
var router = express.Router();
var userController = require('../controllers/userController.js');
var verifyToken = require('../middlewares/verifyToken.js');

router.get('/', verifyToken, userController.list);
router.get('/register', userController.showRegister);
router.get('/login', userController.showLogin);
router.get('/profile', verifyToken, userController.profile);
router.get('/logout', verifyToken, userController.logout);
router.get('/:id', verifyToken, userController.show);

router.post('/', userController.create);
router.post('/login', userController.login);

router.put('/:id', verifyToken, userController.update);

router.delete('/:id', verifyToken, userController.remove);

module.exports = router;
