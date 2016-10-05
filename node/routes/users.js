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

router.post('/addCar/:userID', function(req, res, next) {
    /**
    * Route to add a car for a user ID,
    * @name /addCar/:userID
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
      								"year": req.body.year,
      								"enable_notifications": true 
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
    * Route to add tokens for a user ID,
    * @name /addSecurity/:userID
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
		/**
    	* Route to get and user by ID,
    	* @name /getUser/:userID
    	* @param {String} :userId
    	*/
	    var findUser = function(db, callback) {   
	 	var o_id = new ObjectId(req.params.userID);
	    db.collection('parking').findOne({"_id": o_id},
	    	function(err, result) {
	    				console.log(result)
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

router.get('/getNotifications/:userID', function(req, res, next) {
		/**
    	* Route to get all notifications for a user ID,
    	* @name /getNotifications/:userID
    	* @param {String} :userId
    	*/

    	var findNotifications = function(db, callback) {   
	 	var o_id = new ObjectId(req.params.userID);
	 		var result = [];
		    var cursor =db.collection('notifications').find({$or: [{"receiver_id": o_id}, {"sender_id": o_id}]}).sort([['_id', -1]]);
		    cursor.each(function(err, doc) {
		      assert.equal(err, null);
		      if (doc != null) {
		         console.log("doc "+doc);
		         result.push(doc);
		      } else {
		         callback();
		         res.status(200).send(result)
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

router.get('/getCars/:userID', function(req, res, next) {
	    /**
    	* Route to get all cars for a user ID,
    	* @name /getCars/:userID
    	* @param {String} :userId
    	*/
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
    * Route to remove a car for a user ID,
    * @name /removeCar/:userID
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
	/**
    * Route to update user information,
    * @name /updateUser/:userID
    * @param {String} :userId
    */
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
    * Route to edit car information,
    * @name /editCar/:userID&:plates
    * @param {String} :userID&:plates
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

router.get('/enableNotifications/:userID&:plates', function(req, res, next) {
    /**
    * Route to enable notifications for a car,
    * @name /editCar/:userID&:plates
    * @param {String} :userID&:plates
    */
    var deleteCar = function(db, callback) {   
 	var o_id = new ObjectId(req.params.userID);
    db.collection('parking').update({"_id": o_id, "cars.plates":req.params.plates}, 
             {$set: { 
                         "cars":{ 	
                        			"enable_notifications": true 
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

router.get('/disableNotifications/:userID&:plates', function(req, res, next) {
    /**
    * Route to enable notifications for a car,
    * @name /editCar/:userID&:plates
    * @param {String} :userID&:plates
    */
    var deleteCar = function(db, callback) {   
 	var o_id = new ObjectId(req.params.userID);
    db.collection('parking').update({"_id": o_id, "cars.plates":req.params.plates}, 
             {$set: { 
                         "cars":{ 	
                        			"enable_notifications": false 
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
