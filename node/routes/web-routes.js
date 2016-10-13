var express = require('express');
var router = express.Router();
var dbConfig = require('../db');
var db = require('mongoskin').db(dbConfig.url);
var assert = require('assert');
var MongoClient = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectId;
var Crypto = require('crypto'); 

router.get('/login', function(req, res, next) {
  res.render('login', { title: 'Express' });
});


router.get('/dashboard', function(req, res, next) {
  res.render('newpage', { 
    title: 'Express'
  });
});

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
		         callback(); res.status(200).send(result)
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

router.get('/users', function(req, res, next) {
		/**
    	* Route to get all users,
    	* @name /getNotifications
    	* @param {String} :userId
    	*/

    	var findUsers = function(db, callback) {   
	 	var o_id = new ObjectId(req.params.userID);
	 		var result = [];
		    var cursor =db.collection('parking').find();
		    cursor.each(function(err, doc) {
		      assert.equal(err, null);
		      if (doc != null) {
		         result.push(doc);
		      } else {
		          callback();
		         //res.status(200).send(result);
              res.render('users', { 
                title: 'Express',
                users: result
              });
		      }
		   });
		};	

		MongoClient.connect(dbConfig.url, function(err, db) {
			  assert.equal(null, err);
			  findUsers(db, function() {
			      db.close();
			  });
			});
});

router.get('/users/ban/:userID', function(req, res, next) {
	/**
    * Route to ban a user,
    * @name /updateUser/:userID
    * @param {String} :userId
    */
	var findUser = function(db, callback) {   
	var o_id = new ObjectId(req.params.userID);
	    db.collection('parking').update({"_id": o_id},
	    	 {$set: { 
                      "is_banned": true,
                     }
             },
	    	function(err, result) {
					    assert.equal(err, null);
					    console.log("Found user "+req.params.userID);
					    res.status(200).send(result)
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

router.get('/users/unban/:userID', function(req, res, next) {
	/**
    * Route to ban a user,
    * @name /updateUser/:userID
    * @param {String} :userId
    */
	var findUser = function(db, callback) {   
	var o_id = new ObjectId(req.params.userID);
	    db.collection('parking').update({"_id": o_id},
	    	 {$set: { 
                      "is_banned": false,
                     }
             },
	    	function(err, result) {
					    assert.equal(err, null);
					    console.log("Found user "+req.params.userID);
					    res.status(200).send(result)
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

router.post('/login', function(req, res, next) {
    /**
      * Route to login,
      * @name /getUser/:userID
      * @param {String} :userId
      */
      const secret = 'Friendly';
      const hash = Crypto.createHmac('sha256', secret).update(req.body.password).digest('hex');
      var findUser = function(db, callback) {
      db.collection('parkingAdmins').findOne({"email": req.body.email},
        function(err, result) {
              assert.equal(err, null);
              console.log("Found email "+result._id);
              var payload = {
              "user_id"   : result._id,
              "email"     : result.email
              };
      var response = {
        "userID": result._id
      }
      if(result.password == hash){
          res.redirect("/web-routes/dashboard");
      }
      else 
          res.status(200).send({"error": "Invalid email or password"})
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

router.post('/addUser', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
  var userID ="";
      const secret = 'Friendly';
      const hash = Crypto.createHmac('sha256', secret).update(req.body.password).digest('hex');
  var insertDocument = function(db, callback) {
  db.collection('parkingAdmins').insertOne( {
      "first_name": req.body.first_name,
      "last_name": req.body.last_name,
      "email": req.body.email,
      "password": hash,
   }, function(err, result) {
    assert.equal(err, null);
    userID = result.insertedId;
    console.log("Inserted a user in the admins collection. "+result.insertedId);
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
  var result = {
    "userID": userID
  }
      res.status(200).send(result)
  });
});
});

module.exports = router;
