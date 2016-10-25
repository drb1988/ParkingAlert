var LocalStrategy   = require('passport-local').Strategy;
var express = require('express');
var adminRouter = express.Router();
var url = require('url');
var Crypto = require('crypto');

var dbConfig = require('../../db.js');
var db = require('mongoskin').db(dbConfig.url);

module.exports = function(passport){
    console.log("App passport login working");
    passport.use('login', new LocalStrategy({
        usernameField: 'email',
        passwordField: 'password',
        passReqToCallback : true
    },  function (req, email, password, done) {
        const secret = 'Friendly';
        const hash = Crypto.createHmac('sha256', secret).update(req.body.password).digest('hex');
        db.collection('parkingAdmins').findOne({"email": req.body.email}, function(err, result) {
            if(err)
                return done(null, false);
            console.log("Found email "+result._id);
            var payload = result;
            var response = {
              "userID": result._id
            }
            if(result.password == hash)
                return done(null, payload)
            else 
                return done(null, false)
            callback();
        });    
    }));
}