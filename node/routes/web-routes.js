var express = require('express');
var router = express.Router();
var dbConfig = require('../db');
var db = require('mongoskin').db(dbConfig.url);
var assert = require('assert');
var MongoClient = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectId; 


router.get('/getNotifications', function(req, res, next) {
		/**
    	* Route to get all notification points,
    	* @name /getNotifications
    	* @param {String} :userId
    	*/

    	var findNotifications = function(db, callback) {   
	 	var o_id = new ObjectId(req.params.userID);
	 		var result = [];
		    var cursor =db.collection('notifications').find();
		    cursor.each(function(err, doc) {
		      assert.equal(err, null);
		      if (doc != null) {
		         console.log(doc.location.coordinates[0]);
		         console.log(doc.create_date);
		         result.push({"latitude": doc.location.coordinates[0],
		         		"longitude": doc.location.coordinates[1],
		         		"create_date": doc.create_date});
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
