var login = require('./login');
var express = require('express');

var dbConfig = require('../../db.js');
var db = require('mongoskin').db(dbConfig.url);

module.exports = function(passport){
	// Passport needs to be able to serialize and deserialize users to support persistent login sessions
    passport.serializeUser(function(user, done) {
        console.log('serializing user: ', user._id);
        done(null, user._id);
    });

    passport.deserializeUser(function(_id, done) {
      db.collection('parkingAdmins').findOne({"_id": _id},
        function(err, result) {
          if(!err) {
            console.log('deserializing user:', _id);
            done(null, _id);
          } else
            throw err;
        });            
      });

    // Setting up Passport Strategies for Login and SignUp/Registration
    login(passport);
}