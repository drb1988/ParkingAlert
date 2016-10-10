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


router.post('/user', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
  var userID ="";
  var insertDocument = function(db, callback) {
   db.collection('parking').insertOne( {
      "first_name": req.body.first_name,
      "last_name": req.body.last_name,
      // "nickname": req.body.nickname,
      "email": req.body.email,
      "cars": [],
      "security": [],
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
      "security": [],
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

module.exports = router