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
var QRCode = require('qrcode-npm');

var collide = require('point-circle-collision');
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


var isAuthenticated = function (req, res, next) {
  if (req.isAuthenticated()){
    console.log("autentificat");
    return next();
  }
  res.redirect('/web-routes/login');
}
function unique(arr) {
    var hash = {}, result = [];
    for ( var i = 0, l = arr.length; i < l; ++i ) {
        if ( !hash.hasOwnProperty(arr[i]) ) {
            hash[ arr[i] ] = true;
            result.push(arr[i]);
        }
    }
    return result;
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

    adminRouter.get('/add/admin', isAuthenticated, function(req, res, next) {
        res.render('user', {
            title: 'Friendly | Add admin',
            user: {
                name: req.user.first_name+" "+req.user.last_name,
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
    var findUsers = function(db, callback) {   
        var result = [];
          var cursor = db.collection('parking').find();
          cursor.each(function(err, doc) {
            assert.equal(err, null);
            if (doc != null) {
               result.push(doc);
            } else {
                callback();
                console.log("aici");
               res.render('heatmap_ajax', { title: 'Express',
                users: result,
                message: null,
                message_type: null 
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

  // res.render('heatmap_ajax', { title: 'Express',
  // users: result,
  // message: null,
  // message_type: null });
});

  adminRouter.post('/map/', function(req, res) {
    var type = req.body.type;
    var lat = req.body.lat;
    var lon = req.body.lon;
    var rad = req.body.rad;
    var poly = req.body.polygonPoints;
 
    requests.saveCoordinates(type, lat, lon, rad, poly, function(callback) {
        res.send(callback);
    });
  });

  var notifications;
var admins;
var users;
 
MongoClient.connect('mongodb://192.168.0.185:27017/local', function(err, database) {
    if(err) throw err;
    db = database;
    admins = db.collection('parkingAdmins');
    users = db.collection('parking');
    notifications = db.collection('notifications');
 
    admins.find({}).toArray(function (err, items) {
     if(items){
         console.log('admins', items);
     }
    });
 
    users.find({}).toArray(function (err, items) {
        if(items){
            console.log('users', items);
        }
    });
});
 
 
 
exports.saveCoordinates = function (gtype, lat, lon, rad, polygonPoints, callback) {
 
    var zone;
 
    switch (gtype) {
        case 'circle':
             zone = {
                    'center': new Object({
                        'lat': lat,
                        'lng': lon,
                        'radius': rad
                    }),
                 type: gtype
            };
            break;
        case 'polygon':
             zone = {
                    'center': polygonPoints,
                 type: gtype
            };
            break;
    }
    console.log('zone', zone);
    admins.update({'_id': new ObjectId('57fceaa64521512290f2b37b')},
        {$push: {
           zone: zone
           }
        }
    );
    callback({'ok': 200});
};

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
          var cursor = db.collection('notifications').find().sort({ _id: -1}).skip(parseInt(req.params.offset)).limit(parseInt(req.params.limit));
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
                  }
                if(req.body.circle) {
                  var circle = [parseFloat(req.body.circle.center.lat), parseFloat(req.body.circle.center.lng)],
                  radius =  parseFloat(req.body.circle.radius),
                  point = [doc.location.coordinates[0], doc.location.coordinates[1]]
                //  console.log("is in circle: "+collide(point, circle, radius));
                  if(collide(point, circle, radius))
                    result.push({
                      "lat": doc.location.coordinates[0],
                      "lng": doc.location.coordinates[1]
                    });
                }
              }
            } else {
                callback();
              //  console.log("result: ",result);

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

  adminRouter.post('/StatisticsAjaxCallback', isAuthenticated, function(req, res, next) {
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
   //   console.log("req.user",req.user);
    }
    var findNotifications = function(db, callback) {   
      var o_id = new ObjectId(req.params.userID);
        var result = [];
        var startDate = new Date(req.body.startDateTime.toISOString());
        var endDate = new Date(req.body.endDateTime.toISOString());
          var cursor = db.collection('notifications').aggregate(
              {
                "$match": {
                    create_date: {
                        $gte: startDate,
                        $lte: endDate
                    }
                }
              },
              { "$sort": { create_date: 1 } },
              { "$project": {
                "y":{"$year":"$create_date"},
                "m":{"$month":"$create_date"},
                "d":{"$dayOfMonth":"$create_date"},
                "h":{"$hour":"$create_date"},
                "is_ontime": 1,
                "review.feedback": 1,
                "location.coordinates": 1
                }
              },
              { "$group":{ 
                 "_id": { "year":"$y","month":"$m","day":"$d","hour":"$h"}
             }
           });
          cursor.each(function(err, doc) {
            assert.equal(err, null);
            if (doc != null) {
              if(doc.location.coordinates[0] && doc.location.coordinates[1]) {
                if(req.body.polygon)
                  if(inside([ doc.location.coordinates[0], doc.location.coordinates[1] ], req.body.polygon)) {
                    result.push({
                      "is_ontime": doc.is_ontime,
                      "feedback": doc.review.feedback,
                      "year": doc.y,
                      "month": doc.m,
                      "day": doc.d,
                      "hour": doc.h
                    });
                  }
                if(req.body.circle) {
                  var circle = [parseFloat(req.body.circle.center.lat), parseFloat(req.body.circle.center.lng)],
                  radius =  parseFloat(req.body.circle.radius),
                  point = [doc.location.coordinates[0], doc.location.coordinates[1]]
                  if(collide(point, circle, radius))
                  {
                    result.push({
                      "is_ontime": doc.is_ontime,
                      "feedback": doc.review.feedback,
                      "year": doc.y,
                      "month": doc.m,
                      "day": doc.d,
                      "hour": doc.h
                    });
                  }
                }
              }
            } else {
                callback();
                var filteredResult = {
                  positive_feedback: [],
                  negative_feedback: [],
                  no_feedback: []
                }
              for (var i in result){
                console.log("result "+i,result[i])
                // var objName = result[i].year+"-"+result[i].month+"-"+result[i].day+"_"+result[i].hour;
                var objName = Date.UTC(result[i].year, result[i].month, result[i].day, result[i].hour)
                if(result[i].feedback == true || result[i].feedback == 'true')
                {
                  if(filteredResult.positive_feedback.length>0 && filteredResult.positive_feedback[filteredResult.positive_feedback.length-1][0]==objName )
                        {
                          filteredResult.positive_feedback[filteredResult.positive_feedback.length-1][1] = filteredResult.positive_feedback[filteredResult.positive_feedback.length-1][1] + 1;
                        }
                  else{
                    filteredResult.positive_feedback.push([objName,1])
                  }
                }
                else {
                  if(result[i].feedback == false || result[i].feedback == 'false')
                    {
                      if(filteredResult.negative_feedback.length>0 && filteredResult.negative_feedback[filteredResult.negative_feedback.length-1][0]==objName )
                        {
                          filteredResult.negative_feedback[filteredResult.negative_feedback.length-1][1] = filteredResult.negative_feedback[filteredResult.negative_feedback.length-1][1] + 1;
                        }
                      else{
                        filteredResult.negative_feedback.push([objName,1]) 
                      }
                    }
                  else 
                    {
                      if(filteredResult.no_feedback.length>0 && filteredResult.no_feedback[filteredResult.no_feedback.length-1][0]==objName )
                      {
                        filteredResult.no_feedback[filteredResult.no_feedback.length-1][1] = filteredResult.no_feedback[filteredResult.no_feedback.length-1][1] + 1;
                      }
                      else {
                        filteredResult.no_feedback.push([objName,1])
                      }
                    }
                }
              }
              console.log("filtrat ",filteredResult)
               res.status(200).send(filteredResult)
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
    console.log("body request users", req.body);
      var user_ids = [];
        var findNotifications = function(db, callback) {
      var o_id = new ObjectId(req.params.userID);
        var result = [];
          var cursor = db.collection('notifications').find().sort({ _id: -1}).skip(parseInt(req.params.offset)).limit(parseInt(req.params.limit));
          cursor.each(function(err, doc) {
            assert.equal(err, null);
            if (doc != null) {
              if(doc.location.coordinates[0] && doc.location.coordinates[1] 
                && req.body.startDateTime.getTime()<=doc.create_date.getTime() 
                && doc.create_date.getTime()<=req.body.endDateTime.getTime()
                ) {
                if(req.body.polygon)
                  if(inside([ doc.location.coordinates[0], doc.location.coordinates[1] ], req.body.polygon) && (doc.sender_id || doc.reciver_id)) {
                      user_ids.push(new ObjectId(doc.sender_id));
                      user_ids.push(new ObjectId(doc.reciver_id));
                  }
                if(req.body.circle) {
                  var circle = [parseFloat(req.body.circle.center.lat), parseFloat(req.body.circle.center.lng)],
                  radius =  parseFloat(req.body.circle.radius),
                  point = [doc.location.coordinates[0], doc.location.coordinates[1]]
               //   console.log("is in circle: "+collide(point, circle, radius));
                  if(collide(point, circle, radius)){
                      user_ids.push(new ObjectId(doc.sender_id));
                      user_ids.push(new ObjectId(doc.reciver_id));
                  }
                }
              }
            } else {
                callback();
                var uniqueUserIds = unique(user_ids);
                var findUsers = function(db, callback) {
                    var result = [];
                    var cursor = db.collection('parking').find({"_id": { $in: uniqueUserIds}});
                    cursor.each(function(err, doc) {
                        assert.equal(err, null);
                        if (doc != null) {
                            result.push(doc);
                        } else {
                            callback();
                      //      console.log("result user in finduser", result);
                        }
                    });
                };

                MongoClient.connect(dbConfig.url, function(err, db) {
                    assert.equal(null, err);
                    findUsers(db, function() {
                        db.close();
                    });
                });
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


  adminRouter.post('/addUser', isAuthenticated, function(req, res, next) {
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
        res.status(200).send("The admin user was inserted")
    });
  });
  });

  var generateCode = function (user, count)
     { 
      var results = [];
      for(var i=0; i<count; i++)
      {
        var secret = "Friendly2016";
        var seed = chance.integer({min: 100000, max: 999999}).toString();
        var payload = {
              "user_id"   : user,
              "seed"     : seed,
              "usage": "QRCode",
              "timestamp": new Date()
          };
          var result = jwt.encode( secret, payload);
          results.push(result.value);
      }
      return(results);
     }

  adminRouter.post('/generateQrCode', isAuthenticated, function(req, res, next) {
    var qr = QRCode.qrcode(4, 'M');
    qr.addData(req.user._id);
    qr.make();
    var qr_code = qr.createImgTag(4);
    console.log("body qr:",req.body);
      console.log(generateCode(req.user._id, parseInt(req.body.count)));
    res.status(200).send(generateCode(req.user._id, parseInt(req.body.count)))
  });

  adminRouter.get('/qr', isAuthenticated, function(req, res, next) {
    res.render('qr-generator', { 
      title: 'Friendly | QR Generator'
    });
  });

  adminRouter.post('/admin', isAuthenticated, function(req, res, next) {
  /**
    * Route to update admin information,
    * @name /admin
    */

  const secret = 'Friendly';
  // const hash = Crypto.createHmac('sha256', secret).update(req.body.password).digest('hex');
  var findUser = function(db, callback) {   
  var o_id = new ObjectId(req.user._id);
      db.collection('parkingAdmins').update({"_id": o_id},
         {$set: { 
                "first_name": req.body.first_name,
                "last_name": req.body.last_name,
                "email": req.body.email
                }
             },
        function(err, result) {
              assert.equal(err, null);
              console.log("Found user "+req.params.userID);
              res.redirect("/web-routes/dashboard");
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

  adminRouter.get('/admin', isAuthenticated, function(req, res, next) {
    /**
      * Route to return the admin who is log in,
      * @name /admin
      */
    var result = {
      first_name: req.user.first_name,
      last_name: req.user.last_name,
      email: req.user.email,
      password: req.user.password
    }
    res.render('admin', { 
      title: 'Friendly | Profil',
      admin: result,
      user: {
        name: req.user.first_name+" "+req.user.last_name,
        email: req.user.email
      }
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

    adminRouter.get('/stats', isAuthenticated, function(req, res, next) {
        /**
         * Route to return the admin who is log in,
         * @name /stats
         */
        var result = {
            positive_feedback: [
                [Date.UTC(2013,5,16,1), 1],
                [Date.UTC(2013,5,17,3), 2],
                [Date.UTC(2013,5,18.4), 3],
                [Date.UTC(2013,5,19,1), 4]
            ],
            negative_feedback: [
                [Date.UTC(2013,5,23,1), 2],
                [Date.UTC(2013,5,24,1), 1],
                [Date.UTC(2013,5,25,1), 5],
                [Date.UTC(2013,5,26,1), 1],
                [Date.UTC(2013,5,27,1), 2],
                [Date.UTC(2013,5,28,1), 1]
            ],
            no_feedback: [
                [Date.UTC(2013,5,28,1), 1],
                [Date.UTC(2013,5,30,1), 3],
                [Date.UTC(2013,6,1,1), 5],
                [Date.UTC(2013,6,2,1), 6],
                [Date.UTC(2013,6,3,1), 2],
                [Date.UTC(2013,6,4,1), 1],
                [Date.UTC(2013,6,5,1), 1]
            ]
        };

        res.render('stats', {
            resources: result
        });
    });

  return adminRouter;
}
