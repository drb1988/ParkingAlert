var express = require('express');
var router = express.Router();
var dbConfig = require('../db');
var db = require('mongoskin').db(dbConfig.url);
var assert = require('assert');
var MongoClient = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectId; 
var FCM = require('fcm-push');


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

var toLocalTime = function(time) {
  //console.log(time)
  var d = new Date(time);
  var offset = (new Date().getTimezoneOffset() / 60) * -1;
  var n = new Date(d.getTime() + offset);
  console.log(n)
  return n;
};

var sendNotification = function(token, notification, car, type, time, lat, longit){
        var serverKey = 'AIzaSyA0PfeFcDYeQOhx6HPo1q4r2mD7xY4BJD4';
        var fcm = new FCM("AIzaSyA0PfeFcDYeQOhx6HPo1q4r2mD7xY4BJD4");
        var message = {
          to: token, 
          data: {
            notification_id: notification,
            car_id: car,
            notification_type: type,
            answered_at: new Date,
            estimated_time: time,
            latitude: lat,
            longitude: longit,
          },
          notification: {
            title: 'Parking-Alert',
            body: 'Ati primit notificare', 
            sound: 'enabled'
          }
        };
        console.log("token "+token);
        console.log(notification+" car "+car+" type "+type+" time "+time+" lat "+lat+" long "+longit);
        fcm.send(message, function(err, response){
          if (err) {
              console.log("Something has gone wrong!");
              console.log(err);
          } else {
              console.log("Successfully sent with response: ", response);
          }
        });
      }

var findUsersByNotification = function(db, callback, notificationID) {
    /**
    * Function to get users by notificationID,
    * @name findUsersByNotification
    * @param {String} :notificationID
    */   

    if(/[a-f0-9]{24}/.test(notificationID)) {
    var o_id = new ObjectId(notificationID);
      db.collection('notifications').findOne({"_id": o_id},
        function(err, result) {
            assert.equal(err, null);
            if(result)
              response = {
                "sender_id": result.sender_id,
                "receiver_id": result.receiver_id,
                "sender_token": result.sender_token,
                "vehicle": result.vehicle,
                "receiver_token": result.receiver_token
              }
            else
              response = null;  

            callback(response);
        }); 
      }
      else {
        callback("");
      }          
    }

var findUserToken = function(db, callback, userID) {
    /**
    * Function to get tokens by userID,
    * @name findUserToken
    * @param {String} :userId
    */ 
    if(/[a-f0-9]{24}/.test(userID)) {
      var o_id = new ObjectId(userID);
      db.collection('parking').findOne({"_id": o_id},
        function(err, result) {
              assert.equal(err, null);
              if(result.security && result.profile_picture){
              callback({
                "profile_picture": result.profile_picture,
                "token": result.security.device_token});
              }
              else {
                if(result.security){
                  callback({
                      "token": result.security.device_token});
                    }
                else {
                callback("")
                }
              }
        });   
    }
    else{callback("");}       
    }

/* GET home page. */
router.post('/notification', function(req, res, next) {
    /**
    * Route to insert notifications,
    * @name /notification
    */
  var notificationReceiverToken = "";
  var notificationSenderToken = "";
  var senderPicture = null;
  var receiverPicture = null;
  var notificationID ="";
  var car_id ="";
  var insertDocument = function(db, callback) {
   db.collection('notifications').insertOne( {
      "status": req.body.status,
      "is_active": true,
      "is_ontime": true,
      "sender_read": true,
      "sender_deleted": false,
      "receiver_read": false,
      "receiver_deleted": false,
      "review": {
        
      },
      "location": {
          "type": "Point",
          "coordinates": [
            req.body.latitude,
            req.body.longitude
          ]
      },
      "reverse_geocode": req.body.reverse_geocode,
      "create_date": toLocalTime(new Date()),
      "vehicle": req.body.vehicle,
      "sender_id": new ObjectId(req.body.sender_id),
      "answer": {
        "read_at": null,
        "answered_at": null,
        "estimated": null
      },
      "estimations": [
        {"sender": {
          "type": "Point",
          "coordinates": [
            req.body.latitude,
            req.body.longitude
          ]}}
      ],
      "receiver_token": notificationReceiverToken,
      "sender_token": notificationSenderToken,
      "sender_nickname": req.body.sender_nickname,
      "sender_picture": senderPicture,
      "receiver_id": new ObjectId(req.body.receiver_id),
      "receiver_nickname": req.body.receiver_nickname,
      "receiver_picture": receiverPicture
   }, function(err, result) {
    car_id = req.body.vehicle;
    assert.equal(err, null);
    notificationID = result.insertedId;
    console.log("Sent a notification "+result.insertedId);
    callback();
  });
};
MongoClient.connect(dbConfig.url, function(err, db) {
  assert.equal(null, err);
  findUserToken(db, function(receiver){console.log(receiver); notificationReceiverToken=receiver.token;
        if(receiver.profile_picture) {receiverPicture = receiver.profile_picture; }
        }, req.body.receiver_id);
  findUserToken(db, function(sender){console.log(sender); notificationSenderToken=sender.token;
    if(sender.profile_picture) {senderPicture = sender.profile_picture; }
    insertDocument(db, function() {
      db.close();
      res.status(200).send(notificationID)
      sendNotification(notificationReceiverToken, notificationID, car_id, "sender", 0, req.body.latitude, req.body.longitude)
    });
  }, req.body.sender_id);
});
});

router.get('/receiverRead/:notificationID', function(req, res, next) {
    /**
    * Route to mark notifications as read,
    * @name /receiverRead/:notificationID
    * @param {String} :notificationID
    */
  var vehicle = "";
  var sender_token = "";   
  if(/[a-f0-9]{24}/.test(req.params.id)) {
  var deleteCar = function(db, callback) {
  var o_id = new ObjectId(req.params.notificationID);
    db.collection('notifications').update({"_id": o_id}, 
             {$set: { 
                      "receiver_read": true,
                      "answer.read_at": toLocalTime(new Date())
                    }
             },function(err, result) {
            assert.equal(err, null);
            console.log("Receiver has read "+req.params.notificationID);
            callback();
      });            
  }
  MongoClient.connect(dbConfig.url, function(err, db) {
    assert.equal(null, err);
    findUsersByNotification(db, function(sender){console.log(sender); notificationSenderToken=sender.sender_token;
      vehicle=sender.vehicle;
      deleteCar(db, function() {
        db.close();
        res.status(200).send(req.params.notificationID)
      });
      }, req.params.notificationID);      
  });
  }
  else {
    res.status(200).send("invalid notification ID")
    }
  });

router.get('/senderRead/:notificationID', function(req, res, next) {
    /**
    * Route to mark notifications as read by the sender,
    * @name /receiverRead/:notificationID
    * @param {String} :notificationID
    */
  var vehicle = "";
  var sender_token = "";
  var deleteCar = function(db, callback) {   
  var o_id = new ObjectId(req.params.notificationID);
    db.collection('notifications').update({"_id": o_id}, 
             {$set: { 
                      "sender_read": true
                    }
             },function(err, result) {
            assert.equal(err, null);
            console.log("Receiver has read "+req.params.notificationID);
            callback();
      });            
  }
  MongoClient.connect(dbConfig.url, function(err, db) {
    assert.equal(null, err);
    console.log(req.params.notificationID);
    findUsersByNotification(db, function(sender){console.log(sender); notificationSenderToken=sender.receiver_token;
      vehicle=sender.vehicle;
      deleteCar(db, function() {
        db.close();
        res.status(200).send(req.params.notificationID)
      });
      }, req.params.notificationID);      
  });
  });

router.post('/receiverAnswered/:notificationID', function(req, res, next) {
    /**
    * Route to set answers,
    * @name /receiverRead/:notificationID
    * @param {String} :notificationID
    */
  var vehicle = "";
  var sender_token = "";
  if(/[a-f0-9]{24}/.test(req.params.notificationID)) {
  var deleteCar = function(db, callback) {   
  var o_id = new ObjectId(req.params.notificationID);
    db.collection('notifications').update({"_id": o_id}, 
             {$set: { 
                      "sender_read": false,
                      "answer.answered_at": toLocalTime(new Date()),
                      "answer.estimated": req.body.estimated
                    },
              $push:{
                    "estimated": req.body.estimated,
                    "estimations": {
                      "receiver": {
                          "type": "Point",
                          "coordinates": [
                            req.body.latitude,
                            req.body.longitude
                          ]}
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
      findUsersByNotification(db, function(sender){console.log(sender); notificationSenderToken=sender.sender_token;
        vehicle=sender.vehicle;
        deleteCar(db, function() {
          db.close();
          res.status(200).send(req.params.notificationID)
          console.log("estimated "+req.body.estimated);
          sendNotification(notificationSenderToken, req.params.notificationID, vehicle, "receiver", req.body.estimated, 0, 0)
        });
        }, req.params.notificationID);     
    });
  }
  else {
    res.status(200).send("invalid notificationID")
  }
})

router.post('/receiverExtended/:notificationID', function(req, res, next) {
    /**
    * Route to extend timers,
    * @name /receiverRead/:notificationID
    * @param {String} :notificationID
    */
  var vehicle = "";
  var sender_token = "";
  var deleteCar = function(db, callback) {   
  var o_id = new ObjectId(req.params.notificationID);
    db.collection('notifications').update({"_id": o_id}, 
             {$set: { 
                      "sender_read": false
                    },
              $push:{
                    "extesions": {
                      "extended": true,
                      "extended_at": new Date(),
                      "extension_time": req.body.extension_time
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
      findUsersByNotification(db, function(sender){console.log(sender); notificationSenderToken=sender.sender_token;
        vehicle=sender.vehicle;
        deleteCar(db, function() {
          db.close();
          res.status(200).send(req.params.notificationID)
          sendNotification(notificationSenderToken, req.params.notificationID, vehicle, "extended", 0, 0, 0)
        });
        }, req.params.notificationID);     
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

  if(/[a-f0-9]{24}/.test(req.params.notificationID)) {
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
  }
  res.status(200).send()
})

router.get('/getNotification/:notificationID', function(req, res, next) {
    /**
    * Route to get a notification,
    * @name /getNotification/:notificationID
    * @param {String} :notificationID
    */

    if(/[a-f0-9]{24}/.test(req.params.notificationID)) {
    var findNotification = function(db, callback) {   
    var o_id = new ObjectId(req.params.notificationID);
      db.collection('notifications').findOne({"_id": o_id},
        function(err, result) {
              assert.equal(err, null);
              console.log("Found notification "+req.params.notificationID);
              res.status(200).send(result)
              callback();
        });            
    }
    var senderID;
    MongoClient.connect(dbConfig.url, function(err, db) {
        assert.equal(null, err);
        findNotification(db, function() {
            var senderID;
            findUsersByNotification(db, function(notificationSenderID){
              console.log("notificationSenderID.sender_id: "+notificationSenderID);
            senderID = null;
            }, req.params.notificationID);
            db.close();
        });
      });
  }
  else {
    res.status(200).send("invalid notificationID")
  }
});

router.post('/sendReview/:notificationID', function(req, res, next) {
    /**
    * Route to send reviews,
    * @name /sendReview/:notificationID
    * @param {String} :notificationID
    */

  var vehicle = "";
  var sender_token = "";   
  if(/[a-f0-9]{24}/.test(req.params.notificationID)) {
  var sendReview = function(db, callback) {
  var o_id = new ObjectId(req.params.notificationID);
    db.collection('notifications').update({"_id": o_id}, 
             {$set: { 
                      "is_active": false,
                      "is_ontime": req.body.is_ontime,
                      "review.feedback": req.body.feedback,
                      "review.answered_at": toLocalTime(new Date()),

                    }
             },function(err, result) {
            assert.equal(err, null);
            console.log("Receiver has read "+req.params.notificationID);
            callback();
      });            
  }
   MongoClient.connect(dbConfig.url, function(err, db) {
      assert.equal(null, err);
      findUsersByNotification(db, function(sender){console.log("token "+sender.receiver_token); notificationReceiverToken=sender.receiver_token;
        vehicle=sender.vehicle;
        sendReview(db, function() {
          db.close();
          res.status(200).send(req.params.notificationID)
          sendNotification(notificationReceiverToken, req.params.notificationID, vehicle, "review", 0, 0, 0)
        });
        }, req.params.notificationID);     
    });
}
else {
  res.status(200).send("invalid notificationID");
}
})

module.exports = router;