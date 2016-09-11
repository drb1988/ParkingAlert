var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
	/**
    * Base route,
    * @name /
    */
  res.render('index', { title: 'Express' });
});

router.get('/users', function(req, res, next) {
	/**
    * Route to get all users,
    * @name /users
    */
  res.render('users', { title: 'Express' });
});

router.get('/users/:userId', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param :userId
    */
  res.render('user', { title: 'Express' });
});

module.exports = router;
