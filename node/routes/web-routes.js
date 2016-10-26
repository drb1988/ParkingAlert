'use strict';
var bodyParser = require('body-parser');
var express = require('express');
var adminRouter = express.Router();
var dbConfig = require('../db');
var db = require('mongoskin').db(dbConfig.url);
var assert = require('assert');
var MongoClient = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectId;
var Crypto = require('crypto'); 
var passport = require('passport');
var jwt = require('json-web-token');
var Chance = require('chance');
var chance = new Chance();
var inside = require('point-in-polygon');
var collide = require('point-circle-collision')

var isAuthenticated = function (req, res, next) {
  if (req.isAuthenticated()){
    console.log("autentificat");
    return next();
  }
  res.redirect('/web-routes/login');
}

var verifyIfIsInsideACircle = function (CircleCenterLat, CircleCenterLng, PointLat, PointLng) {
  var rad = function(x) {
    return x * Math.PI / 180;
  };

  var R = 6378137; // Earth’s mean radius in meter
  var dLat = rad(CircleCenterLat - PointLat);
  var dLong = rad(CircleCenterLng - PointLng);
  var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(rad(CircleCenterLat)) * Math.cos(rad(CircleCenterLng)) *
    Math.sin(dLong / 2) * Math.sin(dLong / 2);
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  var d = R * c;
  console.log("dddddddddddddddddddddu: "+d);
  return d
}

module.exports = function(passport){

  adminRouter.get('/login', function(req, res, next) {
    res.render('login', { 
      title: 'Friendly | Login' 
    });
  });

  adminRouter.post('/login', passport.authenticate('login', { 
    successRedirect: '/web-routes/dashboard', 
    failureRedirect: '/web-routes/login', 
    failureFlash : true
  }));

  adminRouter.get('/logout', isAuthenticated, function(req, res) {
    req.logout();
    res.redirect('/web-routes/login');
  });


  adminRouter.get('/dashboard', isAuthenticated, function(req, res, next) {
    res.render('newpage', { 
      title: 'Express',
      user: {
        name: req.user.first_name + " " + req.user.last_name,
        email: req.user.email
      }
    });
  });

  adminRouter.get('/users-cars', isAuthenticated, function(req, res, next) {
    res.render('users-cars', { 
      title: 'Express'
    });
  });

  adminRouter.get('/heatmaps_ajax', isAuthenticated, function(req, res, next) {
  /**
    * Base route,
    * @name /
    */
//     describe('Array', function() {
//     describe('#indexOf()', function() {
//     it('should return -1 when the value is not present', function() {
//       assert.equal(-1, [1,2,3].indexOf(4));
//     });
//   });
// });

  res.render('heatmap_ajax', { title: 'Express' });
});

  adminRouter.post('/MapAjaxCallback', isAuthenticated, function(req, res, next) {
    /**
      * Base route,
      * @MapAjaxCallback /
      */
    req.body.startDateTime = new Date(req.body.startDateTime);
    req.body.endDateTime = new Date(req.body.endDateTime);
    if(req.body.polygon)
      for(var i=0;i<req.body.polygon.length;i++) {
        req.body.polygon[i][0]=parseFloat(req.body.polygon[i][0]);
        req.body.polygon[i][1]=parseFloat(req.body.polygon[i][1]);
      }
    console.log("body request", req.body);
    if(req.user) {
      console.log("req.user",req.user);
    }
    var findNotifications = function(db, callback) {   
      var o_id = new ObjectId(req.params.userID);
        var result = [];
          var cursor =db.collection('notifications').find().sort({ _id: -1}).skip(parseInt(req.params.offset)).limit(parseInt(req.params.limit));
          cursor.each(function(err, doc) {
            assert.equal(err, null);
            if (doc != null) {
              if(doc.location.coordinates[0] && doc.location.coordinates[1] 
                && req.body.startDateTime.getTime()<=doc.create_date.getTime() 
                && doc.create_date.getTime()<=req.body.endDateTime.getTime()
                ) {
                if(req.body.polygon)
                  if(inside([ doc.location.coordinates[0], doc.location.coordinates[1] ], req.body.polygon) && (doc.sender_id || doc.reciver_id)) {
                    result.push({
                      "lat": doc.location.coordinates[0],
                      "lng": doc.location.coordinates[1]
                    });
                    // db.collection('parking').findOne({"_id": { $in: [doc.sender_id, doc.reciver_id]}},
                    //   function(err, result) {
                    //     console.log(result.email)
                    //     assert.equal(err, null);
                    //     callback();
                    //   });
                  }
                if(req.body.circle) {
                  var circle = [parseFloat(req.body.circle.center.lat), parseFloat(req.body.circle.center.lng)],
                  radius =  parseFloat(req.body.circle.radius),
                  point = [doc.location.coordinates[0], doc.location.coordinates[1]]
                  console.log("is in circle: "+collide(point, circle, radius));
                  if(collide(point, circle, radius))
                    result.push({
                      "lat": doc.location.coordinates[0],
                      "lng": doc.location.coordinates[1]
                    });
                }
              }
            } else {
               callback(); 
               console.log("result: ",result);
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

  adminRouter.post('/UsersAjaxCallback', function(req, res, next) {
    /**
      * Base route,
      * @MapAjaxCallback /
      */
    req.body.startDateTime = new Date(req.body.startDateTime);
    req.body.endDateTime = new Date(req.body.endDateTime);
    if(req.body.polygon)
      for(var i=0;i<req.body.polygon.length;i++) {
        req.body.polygon[i][0]=parseFloat(req.body.polygon[i][0]);
        req.body.polygon[i][1]=parseFloat(req.body.polygon[i][1]);
      }
    console.log("body request", req.body);
    var findNotifications = function(db, callback) {   
      var o_id = new ObjectId(req.params.userID);
        var result = [];
          var cursor =db.collection('notifications').find().sort({ _id: -1}).skip(parseInt(req.params.offset)).limit(parseInt(req.params.limit));
          cursor.each(function(err, doc) {
            assert.equal(err, null);
            if (doc != null) {
              if(doc.location.coordinates[0] && doc.location.coordinates[1] 
                && req.body.startDateTime.getTime()<=doc.create_date.getTime() 
                && doc.create_date.getTime()<=req.body.endDateTime.getTime()
                ) {
                if(req.body.polygon)
                  if(inside([ doc.location.coordinates[0], doc.location.coordinates[1] ], req.body.polygon) && (doc.sender_id || doc.reciver_id)) {
                    db.collection('parking').findOne({"_id": { $in: [doc.sender_id, doc.reciver_id]}},
                      function(err, user) {
                        console.log(result.email)
                        assert.equal(err, null);
                        result.push(user);
                        callback();
                      });
                    }
                if(req.body.circle) {
                  var circle = [parseFloat(req.body.circle.center.lat), parseFloat(req.body.circle.center.lng)],
                  radius =  parseFloat(req.body.circle.radius),
                  point = [doc.location.coordinates[0], doc.location.coordinates[1]]
                  console.log("is in circle: "+collide(point, circle, radius));
                  if(collide(point, circle, radius)){
                    db.collection('parking').findOne({"_id": { $in: [doc.sender_id, doc.reciver_id]}},
                      function(err, user) {
                        console.log(result.email)
                        assert.equal(err, null);
                        result.push(user);
                        callback();
                    });
                  }
                }
              }
            } else {
               callback(); 
               console.log("result: ",result);
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


  adminRouter.get('/getNotifications', isAuthenticated, function(req, res, next) {
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

  adminRouter.get('/getNotificationsExtended/:offset&:limit', function(req, res, next) {
      /**
        * Route to get notification with offset and limit,
        * @name /getNotifications
        * @param {String} :userId
        */

      var findNotifications = function(db, callback) {   
      var o_id = new ObjectId(req.params.userID);
        var result = [];
          var cursor =db.collection('notifications').find().sort({ _id: -1}).skip(parseInt(req.params.offset)).limit(parseInt(req.params.limit));
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

  adminRouter.get('/users', function(req, res, next) {
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

  adminRouter.get('/users/ban/:userID', function(req, res, next) {
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

  adminRouter.get('/users/unban/:userID', function(req, res, next) {
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

  // adminRouter.post('/login', function(req, res, next) {
  //     /**
  //       * Route to login,
  //       * @name /getUser/:userID
  //       * @param {String} :userId
  //       */
  //       const secret = 'Friendly';
  //       const hash = Crypto.createHmac('sha256', secret).update(req.body.password).digest('hex');
  //       var findUser = function(db, callback) {
  //       db.collection('parkingAdmins').findOne({"email": req.body.email},
  //         function(err, result) {
  //               assert.equal(err, null);
  //               console.log("Found email "+result._id);
  //               var payload = {
  //               "user_id"   : result._id,
  //               "email"     : result.email
  //               };
  //       var response = {
  //         "userID": result._id
  //       }
  //       if(result.password == hash){
  //           res.redirect("/web-routes/dashboard");
  //       }
  //       else 
  //           res.status(200).send({"error": "Invalid email or password"})
  //       callback();
  //         });            
  //     }
  //     MongoClient.connect(dbConfig.url, function(err, db) {
  //         assert.equal(null, err);
  //         findUser(db, function() {
  //             db.close();
  //         });
  //       });
  // });

  adminRouter.post('/addUser', function(req, res, next) {
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

  adminRouter.get('/generateQrCode/:userID', function(req, res, next) {
  var testCode = generateCode(req.params.userID);
  console.log("test decodare");
  console.log(decodeJwt(testCode));
  res.status(200).send({
    "carCode":  generateCode(req.params.userID) 
  })
});

  adminRouter.post('/updateAdmin/:userID', function(req, res, next) {
  /**
    * Route to update user information,
    * @name /updateUser/:userID
    * @param {String} :userId
    */

  const secret = 'Friendly';
  const hash = Crypto.createHmac('sha256', secret).update(req.body.password).digest('hex');
  var findUser = function(db, callback) {   
  var o_id = new ObjectId(req.params.userID);
      db.collection('parkingAdmins').update({"_id": o_id},
         {$set: { 
                "first_name": req.body.first_name,
                "last_name": req.body.last_name,
                "email": req.body.email,
                "password": hash
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

  adminRouter.get('/getAdmin/:userID', function(req, res, next) {
    /**
      * Route to get and admin by ID,
      * @name /getUser/:userID
      * @param {String} :userId
      */
      var findUser = function(db, callback) {   
    var o_id = new ObjectId(req.params.userID);
      db.collection('parkingAdmins').findOne({"_id": o_id},
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

  var upload = multer({ storage : storage}).single('file');

adminRouter.post('/setPicture/:userID', function(req, res) {
  /**
    * Route to update user information,
    * @name /updateUser/:userID
    * @param {String} :userId
    */

    var uploadPicture = function(db, profilePic, callback) {   
    var o_id = new ObjectId(req.params.userID);
      db.collection('parkingAdmins').update({"_id": o_id},
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


  return adminRouter;
}
