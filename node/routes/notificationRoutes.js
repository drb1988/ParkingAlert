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
      "receiver_id": req.body.receiver_id,
      "receiver_nickname": req.body.receiver_nickname,
      "is_ontime": true
   }, function(err, result) {
    assert.equal(err, null);
    notificationID = result.insertedId;
    console.log("Sent a notification "+result.insertedId);
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

router.post('/receiverRead/:notificationID', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
    var deleteCar = function(db, callback) {   
  var o_id = new ObjectId(req.params.notificationID);
    db.collection('notifications').update({"_id": o_id}, 
             {$set: { 
                         "receiver_read": true 
                      }
             },function(err, result) {
            assert.equal(err, null);
            console.log("Receiver has read "+req.params.notificationID);
            callback();
      });            
  }
  MongoClient.connect(dbConfig.url, function(err, db) {
      assert.equal(null, err);
      deleteCar(db, function() {
          db.close();
          res.status(200).send(req.params.notificationID)
      });
    });
})

router.post('/receiverDeleted/:notificationID', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
    var deleteCar = function(db, callback) {   
  var o_id = new ObjectId(req.params.notificationID);
    db.collection('notifications').update({"_id": o_id}, 
             {$set: { 
                         "receiver_deleted": true 
                      }
             },function(err, result) {
            assert.equal(err, null);
            console.log("Receiver has deleted "+req.params.notificationID);
            callback();
      });            
  }
  MongoClient.connect(dbConfig.url, function(err, db) {
      assert.equal(null, err);
      deleteCar(db, function() {
          db.close();
          res.status(200).send(req.params.notificationID)
      });
    });
})

router.post('/senderDeleted/:notificationID', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
    var deleteCar = function(db, callback) {   
  var o_id = new ObjectId(req.params.notificationID);
    db.collection('notifications').update({"_id": o_id}, 
             {$set: { 
                         "sender_deleted": true 
                      }
             },function(err, result) {
            assert.equal(err, null);
            console.log("Sender has deleted "+req.params.notificationID);
            callback();
      });            
  }
  MongoClient.connect(dbConfig.url, function(err, db) {
      assert.equal(null, err);
      deleteCar(db, function() {
          db.close();
          res.status(200).send(req.params.notificationID)
      });
    });
})



module.exports = router;