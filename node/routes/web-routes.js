var express = require('express');
var router = express.Router();
var dbConfig = require('../db');
var db = require('mongoskin').db(dbConfig.url);
var assert = require('assert');
var MongoClient = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectId; 


router.get('/getNotifications', function(req, res, next) {
		/**
    	* Route to get all notifications for a user ID,
    	* @name /getNotifications/:userID
    	* @param {String} :userId
    	*/

    	var findNotifications = function(db, callback) {   
	 	var o_id = new ObjectId(req.params.userID);
	 		var result = [];
		    var cursor =db.collection('notifications').find();
		    cursor.each(function(err, doc) {
		      assert.equal(err, null);
		      if (doc != null) {
		         console.log(doc.location.coordinates);
		         result.push(doc.location.coordinates);
		      } else {
		         callback();
		         res.status(200).send(result)
		      }
		   });
		};	

		MongoClient.connect(dbConfig.url, function(err, db) {
			  assert.equal(null, err);
			  findNotifications(db, function() {
			      db.close();
			  });
			});
});

module.exports = router;
