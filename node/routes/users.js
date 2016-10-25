var express = require('express');
var router = express.Router();
var dbConfig = require('../db');
var db = require('mongoskin').db(dbConfig.url);
var assert = require('assert');
var MongoClient = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectId; 
var jwt = require('json-web-token');
var Chance = require('chance');
var chance = new Chance();
var multer = require('multer');

var storage =   multer.diskStorage({
  destination: function (req, file, callback) {
    callback(null, './uploads');
  },
  filename: function (req, file, callback) {
  	var i = file.originalname.lastIndexOf('.');
    var file_extension= (i < 0) ? '' : file.originalname.substr(i);
    callback(null, file.fieldname + '-' + Date.now()+ file_extension);
  }
});

var decodeJwt = function (token) {
	var secret = "Friendly2016";
	jwt.decode(secret, token, function(err, decode){
    if (err) {
      return(err.message);
    } else {
      console.log("decode: ",decode);
     return(decode);
    }
  });
}

var generateCode = function (user)
 {	
 	var secret = "Friendly2016"
 	var seed = chance.integer({min: 100000, max: 999999}).toString();
 	var payload = {
        "user_id"   : user,
        "seed"     : seed,
        "usage": "QRCode"
    };
    var result = jwt.encode( secret, payload);
    return result.value;
 }



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
      								"enable_notifications": req.body.enable_notifications,
      								"is_owner": true,
      								"qr_code": req.body.qr_code,
      								"enable_others": req.body.enable_others 
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
             {$set: { 
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
		    var cursor =db.collection('notifications').find({$or: [
		    										{"receiver_id": o_id, "receiver_deleted": false}, 
		    										{"sender_id": o_id, "sender_deleted": false}]
		    										}).sort([['_id', -1]]);
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

router.get('/getNotification/:userID', function(req, res, next) {
		/**
    	* Route to get all notifications for a user ID,
    	* @name /getNotifications/:userID
    	* @param {String} :userId
    	*/

    	var findNotification = function(db, callback) {   
	 	var o_id = new ObjectId(req.params.userID);
	 		var result = [];
		    var cursor =db.collection('notifications').find({$or: [
		    										{"receiver_id": o_id, "receiver_deleted": false}, 
		    										{"sender_id": o_id, "sender_deleted": false}]
		    										}).sort({"create_date": -1}).limit(1);
		    cursor.each(function(err, doc) {
		      assert.equal(err, null);
		      if (doc != null) {
		         result = doc;
		      } else {
		         callback();
		         res.status(200).send(result)
		      }
		   });
		};	

		MongoClient.connect(dbConfig.url, function(err, db) {
			  assert.equal(null, err);
			  findNotification(db, function() {
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
				      // "nickname": req.body.nickname,
				      "email": req.body.email,
				      "phone_number": req.body.phone_number,
				      // "driver_license": req.body.driver_license,
				      // "photo": req.body.photo,
				      "platform": req.body.platform,
				      // "user_city": req.body.user_city
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

var upload = multer({ storage : storage}).single('file');

router.post('/setPicture/:userID', function(req, res) {
	/**
    * Route to update user information,
    * @name /updateUser/:userID
    * @param {String} :userId
    */

    var uploadPicture = function(db, profilePic, callback) {   
	var o_id = new ObjectId(req.params.userID);
	    db.collection('parking').update({"_id": o_id},
	    	 {$set: { 
                    "profile_picture": "http://82.76.188.13:3000/"+profilePic	
                    }
             },
	    	function(err, result) {
					    assert.equal(err, null);
					    console.log("Found user "+req.params.userID);
					    callback();
				});            
		}

	upload(req,res,function(err) {
		console.log(req.file)
        if(err) {
            return res.end("Error uploading file.");
        }
        else {
        	MongoClient.connect(dbConfig.url, function(err, db) {
			  assert.equal(null, err);
			  uploadPicture(db, req.file.filename, function() {
			      db.close();
			  });
			});
        res.send({ 
                    "profile_picture": "http://82.76.188.13:3000/"+req.file.filename	
                });
        }
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
                        "cars.$.plates": req.body.plates,
      					"cars.$.given_name": req.body.given_name,
      					"cars.$.make": req.body.make,
      					"cars.$.model": req.body.model,
      					"cars.$.year": req.body.year,
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

router.post('/chanceQR/:userID&:plates', function(req, res, next) {
    /**
    * Route to change the QR code for a car,
    * @name /chanceQR/:userID&:plates
    * @param {String} :userID&:plates
    */
    var deleteCar = function(db, callback) {   
 	var o_id = new ObjectId(req.params.userID);
    db.collection('parking').update({"_id": o_id, "cars.plates":req.params.plates}, 
             {$set: { 
                        "cars.$.qr_code": req.body.qr_code 
                    }
             },function(err, result) {
				    assert.equal(err, null);
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
    * @name /enableNotifications/:userID&:plates
    * @param {String} :userID&:plates
    */
    var deleteCar = function(db, callback) {   
 	var o_id = new ObjectId(req.params.userID);
    db.collection('parking').update({"_id": o_id, "cars.plates":req.params.plates}, 
             {$set: { 
                        "cars.$.enable_notifications": true 
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



router.get('/allowOthers/:userID&:plates&:action', function(req, res, next) {
    /**
    * Route to enable others for using a car,
    * @name /allowOthers/:userID&:plates&:action
    * @param {String} :userID&:plates
    */
    var allowOthers = function(db, callback) {   
 	var o_id = new ObjectId(req.params.userID);
 	if((req.params.action == true) || (req.params.action == "true")){
 		console.log("in if");
 		db.collection('parking').update({"_id": o_id, "cars.plates":req.params.plates}, 
             {$set: { 
                        "cars.$.enable_others": true
                    }
             },function(err, result) {
				    assert.equal(err, null);
				    callback();
			});  
 	}
 	else {
 		console.log("in else")
 		db.collection('parking').update({"_id": o_id, "cars.plates":req.params.plates}, 
             {$set: { 
                        "cars.$.enable_others": false
                    }
             },function(err, result) {
				    assert.equal(err, null);
				    callback();
			});  
 	}              
	}
	MongoClient.connect(dbConfig.url, function(err, db) {
		  assert.equal(null, err);
		  allowOthers(db, function() {
		      db.close();
		      res.status(200).send(req.params.userID)
		  });
		});
})

router.get('/disableNotifications/:userID&:plates', function(req, res, next) {
    /**
    * Route to enable notifications for a car,
    * @name /disableNotifications/:userID&:plates
    * @param {String} :userID&:plates
    */
    var deleteCar = function(db, callback) {   
 	var o_id = new ObjectId(req.params.userID);
    db.collection('parking').update({"_id": o_id, "cars.plates":req.params.plates}, 
             {$set: { 
                        "cars.$.enable_notifications": false 
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

router.get('/getUsersForCode/:token', function(req, res, next) {
		/**
    	* Route to get all notifications for a user ID,
    	* @name /getNotifications/:userID
    	* @param {String} :userId
    	*/

    	var findNotifications = function(db, callback) {   
	 	var o_id = new ObjectId(req.params.userID);
	 		var result = [];
		    var cursor =db.collection('parking').find({"cars.qr_code": req.params.token});
		    cursor.each(function(err, doc) {
		      assert.equal(err, null);
		      if (doc != null) {
		      	var foundCar = {
		      		"userID": "",
		      		"car": "disabled"
		      	};
		         foundCar.userID = doc._id;
		         for (var i = doc.cars.length - 1; i >= 0; i--) {
		         	console.log(doc.cars[i].enable_notifications + " " + doc.cars[i].qr_code);
		         	if((doc.cars[i].qr_code == req.params.token) && ((doc.cars[i].enable_notifications == true) || (doc.cars[i].enable_notifications == "true")))
		         	{
		         		foundCar.car = doc.cars[i];
		         	}
		         };
		         result.push(foundCar);
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

router.get('/generateCarCode/:userID', function(req, res, next) {
	var testCode = generateCode(req.params.userID);
	console.log("test decodare");
	console.log(decodeJwt(testCode));
	res.status(200).send({
		"carCode":  generateCode(req.params.userID) 
	})
});

module.exports = router;
