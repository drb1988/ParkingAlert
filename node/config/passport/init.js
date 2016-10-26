var login = require('./login');
var express = require('express');

var MongoClient = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectId;
var dbConfig = require('../../db.js');
var db = require('mongoskin').db(dbConfig.url);

var assert = require('assert');

module.exports = function(passport){
	// Passport needs to be able to serialize and deserialize users to support persistent login sessions
    passport.serializeUser(function(user, done) {
        // console.log('serializing user: ', user._id);
        done(null, user._id);
    });

    passport.deserializeUser(function(_id, done) {
      var findUser = function(db, callback) {   
        var o_id = new ObjectId(_id);
        db.collection('parkingAdmins').findOne({"_id": o_id},
          function(err, result) {
            // console.log(result)
            assert.equal(err, null);
            // console.log("Found user "+result);
            done(null, result);
            callback();
        });            
      };

      MongoClient.connect(dbConfig.url, function(err, db) {
          assert.equal(null, err);
          findUser(db, function() {
              db.close();
          });
      });
    });

    // Setting up Passport Strategies for Login and SignUp/Registration
    login(passport);
}