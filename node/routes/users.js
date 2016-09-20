var express = require('express');
var router = express.Router();
var dbConfig = require('../db');
var db = require('mongoskin').db(dbConfig.url);
var assert = require('assert');
var MongoClient = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectId; 

/* GET users listing. */
router.get('/', function(req, res, next) {
  res.send('respond with a resource');
});

router.get('/chart', function(req, res, next) {
  res.send('respond with a resource');
});

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
      "nickname": req.body.nickname,
      "email": req.body.email,
      "cars": [],
      "security": [],
      "driver_license": req.body.driver_license,
      "photo": req.body.photo,
      "platform": req.body.platform,
      "user_city": req.body.user_city
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
      res.status(200).send(userID)
  });
});
});

router.post('/addCar/:userID', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
    var insertCar = function(db, callback) {   
 	var o_id = new ObjectId(req.params.userID);
    db.collection('parking').update({"_id": o_id}, 
             {$push: { 
                         "cars":{ 	
                        			"plates": req.body.plates,
      								"given_name": req.body.given_name,
      								"make": req.body.make,
      								"model": req.body.model,
      								"year": req.body.year 
      							} 
                      }
             },function(err, result) {
				    assert.equal(err, null);
				    console.log("Inserted a car for the user "+req.params.userID);
				    callback();
			});            
	}
	MongoClient.connect(dbConfig.url, function(err, db) {
		  assert.equal(null, err);
		  insertCar(db, function() {
		      db.close();
		      res.status(200).send(req.params.userID)
		  });
		});
})

router.post('/addSecurity/:userID', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
    var insertSecurity = function(db, callback) {   
 	var o_id = new ObjectId(req.params.userID);
    db.collection('parking').update({"_id": o_id}, 
             {$push: { 
                         "security":{ 	
                        			"device_token": req.body.device_token,
                        			"password": req.body.password,
                        			"created_at": new Date(),
                        			"updated_at": new Date(),
                        			"last_auth": new Date(), 
                        			"reg_ip": req.body.reg_ip 
      								} 
                      }
             },function(err, result) {
				    assert.equal(err, null);
				    console.log("Inserted security options for the user "+req.params.userID);
				    callback();
			});            
	}
	MongoClient.connect(dbConfig.url, function(err, db) {
		  assert.equal(null, err);
		  insertSecurity(db, function() {
		      db.close();
		      res.status(200).send(req.params.userID)
		  });
		});
})

router.get('/getUser/:userID', function(req, res, next) {
	   var findUser = function(db, callback) {   
	 	var o_id = new ObjectId(req.params.userID);
	    db.collection('parking').findOne({"_id": o_id},
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

router.get('/getCars/:userID', function(req, res, next) {
	   var findUser = function(db, callback) {   
	 	var o_id = new ObjectId(req.params.userID);
	    db.collection('parking').findOne({"_id": o_id},
	    	function(err, result) {
					    assert.equal(err, null);
					    console.log("Found user "+req.params.userID);
					    res.status(200).send(result.cars)
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

router.post('/removeCar/:userID', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
    var deleteCar = function(db, callback) {   
 	var o_id = new ObjectId(req.params.userID);
    db.collection('parking').update({"_id": o_id}, 
             {$pull: { 
                         "cars":{ 	
                        			"plates": req.body.plates,
      							} 
                      }
             },function(err, result) {
				    assert.equal(err, null);
				    console.log("Inserted a car for the user "+req.params.userID);
				    callback();
			});            
	}
	MongoClient.connect(dbConfig.url, function(err, db) {
		  assert.equal(null, err);
		  deleteCar(db, function() {
		      db.close();
		      res.status(200).send(req.params.userID)
		  });
		});
})

router.post('/updateUser/:userID', function(req, res, next) {
	   var findUser = function(db, callback) {   
	 var o_id = new ObjectId(req.params.userID);
	    db.collection('parking').update({"_id": o_id},
	    	 {$set: { 
                      "first_name": req.body.first_name,
      				  "last_name": req.body.last_name,
				      "nickname": req.body.nickname,
				      "email": req.body.email,
				      "driver_license": req.body.driver_license,
				      "photo": req.body.photo,
				      "platform": req.body.platform,
				      "user_city": req.body.user_city
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

router.post('/editCar/:userID&:plates', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
    var deleteCar = function(db, callback) {   
 	var o_id = new ObjectId(req.params.userID);
    db.collection('parking').update({"_id": o_id, "cars.plates":req.params.plates}, 
             {$set: { 
                         "cars":{ 	
                        			"plates": req.body.plates,
      								"given_name": req.body.given_name,
      								"make": req.body.make,
      								"model": req.body.model,
      								"year": req.body.year 
      							}  
                      }
             },function(err, result) {
				    assert.equal(err, null);
				    console.log("Inserted a car for the user "+req.params.userID);
				    callback();
			});            
	}
	MongoClient.connect(dbConfig.url, function(err, db) {
		  assert.equal(null, err);
		  deleteCar(db, function() {
		      db.close();
		      res.status(200).send(req.params.userID)
		  });
		});
})

module.exports = router;
