var express = require('express');
var router = express.Router();
var dbConfig = require('../db');
var db = require('mongoskin').db(dbConfig.url);
var assert = require('assert');
var MongoClient = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectId; 
var jwt = require('json-web-token');
var config = require(__dirname + '/../config.js');
var nodemailer = require('nodemailer');
var Crypto = require('crypto');
var Chance = require('chance');
var chance = new Chance();

var findUserID = function(db, callback, email) {
    /**
    * Function to get userID by email address,
    * @name findUserToken
    * @param {String} :userId
    */ 
      db.collection('parking').findOne({"email": email},
        function(err, result) {
              assert.equal(err, null);
              if(result != null){
              callback(result._id);
              }
              else {
                callback("404")
              }
        });            
    }

router.post('/user', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
  var userID ="";
      const secret = 'Friendly';
      const hash = Crypto.createHmac('sha256', secret).update(req.body.password).digest('hex');
  var insertDocument = function(db, callback) {
   db.collection('parking').insertOne( {
      "first_name": req.body.first_name,
      "last_name": req.body.last_name,
      // "nickname": req.body.nickname,
      "email": req.body.email,
      "cars": [],
      "security": [],
      "password": hash,
      "is_banned": false,
      "phone_number": req.body.phone_number,
      // "driver_license": req.body.driver_license,
      // "photo": req.body.photo,
      "platform": req.body.platform,
      // "user_city": req.body.user_city
   }, function(err, result) {
    assert.equal(err, null);
    userID = result.insertedId;
    console.log("Inserted a user in the users collection. "+result.insertedId);
    callback();
  });
};
MongoClient.connect(dbConfig.url, function(err, db) {
  assert.equal(null, err);
  insertDocument(db, function() {
      db.close();
      var payload = {
        "user_id"   : userID,
        "email"     : req.body.email
    };
  var token = jwt.encode( config.jwt.secret, payload);
  var result = {
    "userID": userID,
    "token": token
  }
      res.status(200).send(result)
  });
});
});

router.post('/facebookLogin', function(req, res, next) {
    /**
    * Route for facebook login,
    * @name /users/:userId
    * @param {String} :userId
    */
  var userID ="";
  var insertDocument = function(db, callback) {
   db.collection('parking').insertOne( {
      "first_name": req.body.first_name,
      "last_name": req.body.last_name,
      "email": req.body.email,
      "facebookID": req.body.facebookID,
      "cars": [],
      "security": [],
      "is_banned": false,
      "platform": req.body.platform
   }, function(err, result) {
    assert.equal(err, null);
    userID = result.insertedId;
    console.log("Inserted a user in the users collection. "+result.insertedId);
    callback();
  });
};
MongoClient.connect(dbConfig.url, function(err, db) {
  assert.equal(null, err);
  findUserID(db, function(user){console.log(user); 
      if(user != "404") {
        var payload = {
              "user_id"   : user,
              "email"     : req.body.email
          };
        var token = jwt.encode( config.jwt.secret, payload);
        var result = {
          "userID": user,
          "token": token
        }
        res.status(200).send(result)
      }
      else {
          insertDocument(db, function() {
              db.close();
              var payload = {
                "user_id"   : userID,
                "email"     : req.body.email
            };
          var token = jwt.encode( config.jwt.secret, payload);
          var result = {
            "userID": userID,
            "token": token
          }
              res.status(200).send(result)
          });
      }
  }, req.body.email);
});
});

router.post('/sendEmailVerification', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
  var userID ="";
  var token ="";
  Crypto.randomBytes(3, function(err, buffer) {
      token = buffer.toString('hex');
  });
  var insertDocument = function(db, callback) {
   db.collection('tempUsers').insertOne( {
      "email": req.body.email,
      "token": token
      }, function(err, result) {
    assert.equal(err, null);
    userID = result.insertedId;
    console.log("Inserted a temporary user"+result.insertedId);
    callback();
  });
};
MongoClient.connect(dbConfig.url, function(err, db) {
  assert.equal(null, err);
  insertDocument(db, function() {
      db.close();
  var result = {
    "verificationID": userID
  }   
  var transporter = nodemailer.createTransport({
          service: 'Gmail',
          auth: {
              user: 'mugurel.mitrut@gmail.com', // Your email id
              pass: 'banana1!' // Your password
            }
          });
  var mailOptions = {
            from: '"Parking Alert" <mugurel.mitrut@gmail.com>', // sender address
            to: req.body.email,// list of receivers
            subject: 'Notification', // Subject line
            text: token // plaintext body
          };
  transporter.sendMail(mailOptions, function(error, info){if(error){
              return console.log(error);  
            } else {
              return console.log(info);  
            }});   
      res.status(200).send(result)
  });
});
});

router.post('/verifyEmail/:userID', function(req, res, next) {
    /**
      * Route to get and user by ID,
      * @name /getUser/:userID
      * @param {String} :userId
      */
    var findUser = function(db, callback) {   
    var o_id = new ObjectId(req.params.userID);
      db.collection('tempUsers').findOne({"_id": o_id},
        function(err, result) {
              console.log(result)
              assert.equal(err, null);
              if(result.email==req.body.email && result.token==req.body.token){
                res.status(200).send("Success")
              }
              else{
                res.status(200).send("Email verification failure")
              }
              callback();
        });            
    }
    MongoClient.connect(dbConfig.url, function(err, db) {
        assert.equal(null, err);
        findUser(db, function() {
            db.close();
        });
      });
});

router.get('/login/:email&:password', function(req, res, next) {
    /**
      * Route to login,
      * @name /getUser/:userID
      * @param {String} :userId
      */
      const secret = 'Friendly';
      const hash = Crypto.createHmac('sha256', secret).update(req.params.password).digest('hex');
      var findUser = function(db, callback) {
      db.collection('parking').findOne({"email": req.params.email},
        function(err, result) {
          if(result){
              assert.equal(err, null);
              var payload = {
              "user_id"   : result._id,
              "email"     : result.email
              };
      var token = jwt.encode( config.jwt.secret, payload);
      var response = {
        "token": token,
        "userID": result._id,
        "user": result
      }
      if(result.password == hash){
          res.status(200).send(response)
      }
      else 
          res.status(202).send({"error": "Invalid password"})
      callback();
        }
      else {
          res.status(201).send({"error": "Invalid email"})
      }
      });            
    }
    MongoClient.connect(dbConfig.url, function(err, db) {
        assert.equal(null, err);
        findUser(db, function() {
            db.close();
        });
      });
});

router.get('/resetPassword/:email', function(req, res, next) {
    /**
      * Route to login,
      * @name /getUser/:userID
      * @param {String} :userId
      */
      const secret = 'Friendly';
      var password = chance.integer({min: 100000, max: 999999}).toString();
      const hash = Crypto.createHmac('sha256', secret).update(password).digest('hex');
      var resetPassword = function(db, callback) {
      db.collection('parking').update({"email": req.params.email}, 
                 {$set: { 
                          "password": hash    
                        }
                 },function(err, result) {
                assert.equal(err, null);
                console.log("Inserted the password "+hash);
                callback();
          });            
      }
      MongoClient.connect(dbConfig.url, function(err, db) {
          assert.equal(null, err);
          resetPassword(db, function() {
              db.close();
              var result = {
                "result": "Mail Sent"
              }   
              var transporter = nodemailer.createTransport({
                      service: 'Gmail',
                      auth: {
                          user: 'mugurel.mitrut@gmail.com', // Your email id
                          pass: 'banana1!' // Your password
                        }
                      });
              var mailOptions = {
                        from: '"Parking Alert" <mugurel.mitrut@gmail.com>', // sender address
                        to: req.params.email,// list of receivers
                        subject: 'Notification', // Subject line
                        text: password // plaintext body
                      };
              transporter.sendMail(mailOptions, function(error, info){if(error){
                          return console.log(error);  
                        } else {
                          return console.log(info);  
                        }});   
                          res.status(200).send(result)
                      });
        });
});

module.exports = router