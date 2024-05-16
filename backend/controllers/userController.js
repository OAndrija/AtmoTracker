const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const UserModel = require('../models/userModel.js');
const {jwtSecretKey} = require('../config.js');

/**
 * userController.js
 *
 * @description :: Server-side logic for managing users.
 */
module.exports = {

    /**
     * userController.list()
     */
    list: function (req, res) {
        UserModel.find(function (err, users) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user.',
                    error: err
                });
            }

            return res.json(users);
        });
    },

    /**
     * userController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        UserModel.findOne({_id: id}, function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user.',
                    error: err
                });
            }

            if (!user) {
                return res.status(404).json({
                    message: 'No such user'
                });
            }

            return res.json(user);
        });
    },

    /**
     * userController.create()
     */
    create: function (req, res) {
        var user = new UserModel({
            username: req.body.username,
            password: req.body.password,
            email: req.body.email
        });

        user.save(function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating user',
                    error: err
                });
            }

            return res.status(201).json(user);
            //return res.redirect('/users/login');
        });
    },

    /**
     * userController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        UserModel.findOne({_id: id}, function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user',
                    error: err
                });
            }

            if (!user) {
                return res.status(404).json({
                    message: 'No such user'
                });
            }

            user.username = req.body.username ? req.body.username : user.username;
            user.password = req.body.password ? req.body.password : user.password;
            user.email = req.body.email ? req.body.email : user.email;

            user.save(function (err, user) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating user.',
                        error: err
                    });
                }

                return res.json(user);
            });
        });
    },

    /**
     * userController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        UserModel.findByIdAndRemove(id, function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the user.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    },

    showRegister: function (req, res) {
        res.render('user/register');
    },

    showLogin: function (req, res) {
        res.render('user/login');
    },

    login: function (req, res, next) {
        UserModel.authenticate(req.body.username, req.body.password, function (err, user) {
            if (err || !user) {
                var err = new Error('Wrong username or password');
                err.status = 401;
                return next(err);
            }
            req.session.userId = user._id;
            // Generate JWT token
            const token = jwt.sign({
                userId: user._id,
                name: user.username,
            }, jwtSecretKey, {expiresIn: '1d'});

            // Return token and user information in the response
            return res.json({ token: token, user: user });
        });
    },

    profile: function(req, res, next) {
        if (!req.userId) {
            return res.status(401).json({ message: 'Unauthorized: No token provided' });
        }

        UserModel.findById(req.userId)
            .exec(function(error, user) {
                if (error) {
                    return next(error);
                } else {
                    if (!user) {
                        var err = new Error('User not found');
                        err.status = 404;
                        return next(err);
                    } else {
                        // Pass the token to the template
                        res.render('profile', { user: user, token: req.headers['authorization'] });
                    }
                }
            });
    },

    logout: function (req, res, next) {
        if (req.session) {
            req.session.destroy(function (err) {
                if (err) {
                    return next(err);
                } else {
                    //return res.redirect('/');
                    return res.status(201).json({});
                }
            });
        }
    }
};
