var express = require('express');
var router = express.Router();
var assert = require('assert');

/* GET home page. */
router.get('/', function(req, res, next) {
  /**
    * Base route,
    * @name /
    */
//     describe('Array', function() {
//     describe('#indexOf()', function() {
//     it('should return -1 when the value is not present', function() {
//       assert.equal(-1, [1,2,3].indexOf(4));
//     });
//   });
// });
  res.render('index', { title: 'Express' });
});
router.get('/login', function(req, res, next) {
  /**
    * Base route,
    * @name /
    */
//     describe('Array', function() {
//     describe('#indexOf()', function() {
//     it('should return -1 when the value is not present', function() {
//       assert.equal(-1, [1,2,3].indexOf(4));
//     });
//   });
// });
  res.render('login', { title: 'Express' });
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
    * @param {String} :userId
    */
  res.render('user', { title: 'Express' });
});

module.exports = router;
