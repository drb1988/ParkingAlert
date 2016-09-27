var express = require('express');
var router = express.Router();
var dbConfig = require('../db');
var db = require('mongoskin').db(dbConfig.url);
var assert = require('assert');
var MongoClient = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectId; 

function distance(lat1, lon1, lat2, lon2, unit) {
  var radlat1 = Math.PI * lat1/180
  var radlat2 = Math.PI * lat2/180
  var theta = lon1-lon2
  var radtheta = Math.PI * theta/180
  var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
  dist = Math.acos(dist)
  dist = dist * 180/Math.PI
  dist = dist * 60 * 1.1515
  if (unit=="K") { dist = dist * 1.609344 }
  if (unit=="N") { dist = dist * 0.8684 }
  return dist
}
var response;

var findUsersByNotification = function(db, callback, notificationID) {
    /**
    * Function to get users by notificationID,
    * @name findUsersByNotification
    * @param {String} :notificationID
    */   
    var o_id = new ObjectId(notificationID);
      db.collection('notifications').findOne({"_id": o_id},
        function(err, result) {
            assert.equal(err, null);
            response = {
              "sender_id": result.sender_id,
              "receiver_id": result.receiver_id
            }
            callback(response);
        });           
    }

var findUserToken = function(db, callback, userID) {
    /**
    * Function to get tokens by userID,
    * @name findUserToken
    * @param {String} :userId
    */ 
    console.log("aici "+userID);  
    var o_id = new ObjectId(userID);
      db.collection('parking').findOne({"_id": o_id},
        function(err, result) {
              assert.equal(err, null);
              console.log(result)
              callback(result.security[0].device_token);
        });            
    }

/* GET home page. */
router.post('/notification', function(req, res, next) {
    /**
    * Route to insert notifications,
    * @name /notification
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
      "location": {
          "type": "Point",
          "coordinates": [
            req.body.latitude,
            req.body.longitude
          ]
      },
      "reverse_geocode": req.body.reverse_geocode,
      "create_date": new Date(),
      "vehicle": req.body.vehicle,
      "sender_id": new ObjectId(req.body.sender_id),
      "estimations": [
        {"sender": {
          "type": "Point",
          "coordinates": [
            req.body.latitude,
            req.body.longitude
          ]}
      ],
      "sender_nickname": req.body.sender_nickname,
      "receiver_id": new ObjectId(req.body.receiver_id),
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
      findUserToken(db, function(){}, req.body.receiver_id);
      db.close();
      res.status(200).send(notificationID)
  });
});
});

router.post('/receiverRead/:notificationID', function(req, res, next) {
    /**
    * Route to mark notifications as read,
    * @name /receiverRead/:notificationID
    * @param {String} :notificationID
    */
  var deleteCar = function(db, callback) {   
  var o_id = new ObjectId(req.params.notificationID);
    db.collection('notifications').update({"_id": o_id}, 
             {$set: { 
                      "receiver_read": true

                    },
              $push:{
                    "estimations": {
                      "receiver": req.body
                    }   
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
          var senderID;
          findUsersByNotification(db, function(notificationSenderID){
            senderID = notificationSenderID.sender_id;
            findUserToken(db, function(notificationToken){console.log(notificationToken)}, senderID)
          }, req.params.notificationID);
          db.close();
          res.status(200).send(req.params.notificationID)
      });
    });
})

router.post('/receiverDeleted/:notificationID', function(req, res, next) {
    /**
    * Route to mark notifications a deleted by the receiver,
    * @name /receiverDeleted/:notificationID
    * @param {String} :notificationID
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
    * Route to mark notifications as deleted by the server,
    * @name /senderDeleted/:notificationID
    * @param {String} :notificationID
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

router.get('/getNotification/:notificationID', function(req, res, next) {
    /**
    * Route to get a notification,
    * @name /getNotification/:notificationID
    * @param {String} :notificationID
    */
    var findNotification = function(db, callback) {   
    var o_id = new ObjectId(req.params.userID);
      db.collection('notifications').findOne({"_id": o_id},
        function(err, result) {
              assert.equal(err, null);
              console.log("Found notification "+req.params.notificationID);
              res.status(200).send(result)
              callback();
        });            
    }
    MongoClient.connect(dbConfig.url, function(err, db) {
        assert.equal(null, err);
        findNotification(db, function() {
            db.close();
        });
      });
});

module.exports = router;