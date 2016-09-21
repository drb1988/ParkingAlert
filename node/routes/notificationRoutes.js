var express = require('express');
var router = express.Router();
var dbConfig = require('../db');
var db = require('mongoskin').db(dbConfig.url);
var assert = require('assert');
var MongoClient = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectId; 

/* GET home page. */
router.post('/notification', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
  var notificationID ="";
  var insertDocument = function(db, callback) {
   db.collection('notifications').insertOne( {
      "status": req.body.status,
      "is_active": req.body.is_active,
      "sender_read": true,
      "sender_deleted": false,
      "receiver_read": false,
      "receiver_deleted": false,
      "location": req.body.location,
      "reverse_geocode": req.body.reverse_geocode,
      "create_date": new Date(),
      "vehicle": req.body.vehicle,
      "sender_id": req.body.sender_id,
      "sender_nickname": req.body.sender_nickname,
      "is_ontime": true
   }, function(err, result) {
    assert.equal(err, null);
    notificationID = result.insertedId;
    console.log("Inserted a user in the users collection. "+result.insertedId);
    callback();
  });
};
MongoClient.connect(dbConfig.url, function(err, db) {
  assert.equal(null, err);
  insertDocument(db, function() {
      db.close();
      res.status(200).send(notificationID)
  });
});
});




module.exports = router;