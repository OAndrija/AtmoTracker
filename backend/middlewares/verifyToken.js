const jwt = require('jsonwebtoken');
const { jwtSecretKey } = require('../config.js');

function verifyToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    if (!authHeader) {
        return res.status(401).json({ message: 'Unauthorized: No token provided' });
    }

    const token = authHeader.split(' ')[1]; // Extract token from Authorization header
    jwt.verify(token, jwtSecretKey, function(err, decoded) {
        if (err) {
            return res.status(401).json({ message: 'Unauthorized: Invalid token' });
        }

        req.userId = decoded.userId;
        next();
    });
}

module.exports = verifyToken;
