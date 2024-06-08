var jwt = require('jsonwebtoken');
const SECRET_KEY = process.env.JWT_SECRET_KEY;

function verifyToken(req, res, next) {
    const token = req.headers['authorization'];

    if (!token) {
        return res.status(401).json({ message: 'Access token is missing or invalid' });
    }

    jwt.verify(token, SECRET_KEY, function (err, decoded) {
        if (err) {
            return res.status(401).json({ message: 'Failed to authenticate token' });
        }

        req.userId = decoded.id;
        next();
    });
}

module.exports = verifyToken;
