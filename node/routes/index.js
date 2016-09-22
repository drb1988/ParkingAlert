var express = require('express');
var router = express.Router();
var assert = require('assert');

/* GET home page. */
router.get('/', function(req, res, next) {
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
  var resources = [];
  resources.hour_1 = [
  {
    lat: 37.782551,
    lng: -122.445368
  },
  {
    lat: 37.782745,
    lng: -122.444586
  }
  ];
  resources.hour_2 = [
  {
    lat: 37.782551,
    lng: -122.445368
  },
  {
    lat: 37.782745,
    lng: -122.444586
  }
  ];
  resources.hour_3 = [
  {
    lat: 37.782551,
    lng: -122.445368
  }
  ];
  resources.hour_4 = [
    { lat: 37.782551, lng: -122.445368 },
    { lat: 37.782745, lng: -122.444586 }
  ];
  resources.hour_5 = [
    { lat: 37.783842, lng: -122.439591 },
    { lat: 37.784147, lng: -122.439668 }
  ];
  resources.hour_6 = [
    { lat: 37.784206, lng: -122.439686 },
    { lat: 37.784386, lng: -122.439790 }
  ];
  resources.hour_7 = [
    { lat: 37.785010, lng: -122.439947 },
    { lat: 37.785360, lng: -122.439952 }
  ];
  resources.hour_8 = [
    { lat: 37.786905, lng: -122.440270 },
    { lat: 37.786956, lng: -122.440279 }
  ];
  resources.hour_9 = [
    { lat: 37.800938, lng: -122.434650 },
    { lat: 37.801024, lng: -122.434889 }
  ];
  resources.hour_10 = [
    { lat: 37.784437, lng: -122.422924 },
    { lat: 37.784746, lng: -122.422818 }
  ];
  resources.hour_11 = [
    { lat: 37.787282, lng: -122.394426 },
    { lat: 37.787783, lng: -122.393767 }
  ];
  resources.hour_12 = [
    { lat: 37.778160, lng: -122.438442 },
    { lat: 37.778414, lng: -122.438508 }
  ];
  resources.hour_13 = [
    { lat: 37.782286, lng: -122.439266 },
    { lat: 37.797847, lng: -122.429388 }
  ];
  resources.hour_14 = [
    { lat: 37.799320, lng: -122.429251 },
    { lat: 37.799590, lng: -122.429309 }
  ];
  resources.hour_15 = [
    { lat: 37.801242, lng: -122.429685 },
    { lat: 37.801375, lng: -122.429702 }
  ];
  resources.hour_16 = [
    { lat: 37.763115, lng: -122.415196 },
    { lat: 37.762967, lng: -122.415183 }
  ];
  resources.hour_17 = [
    { lat: 37.795203, lng: -122.400304 },
    { lat: 37.778738, lng: -122.415584 }
  ];
  resources.hour_18 = [
    { lat: 37.764768, lng: -122.426089 },
    { lat: 37.764766, lng: -122.426117 }
  ];
  resources.hour_19 = [
    { lat: 37.778409, lng: -122.408839 },
    { lat: 37.777842, lng: -122.409501 }
  ];
  resources.hour_20 = [
    { lat: 37.773533, lng: -122.413636 },
    { lat: 37.773021, lng: -122.413009 },
  ];
  resources.hour_21 = [
    { lat: 37.768564, lng: -122.406469 },
    { lat: 37.767980, lng: -122.405745 }
  ];
  resources.hour_22 = [
    { lat: 37.761344, lng: -122.406215 },
    { lat: 37.760556, lng: -122.406495 }
  ];
  resources.hour_23 = [
    { lat: 37.757676, lng: -122.405118 },
  ];
  resources.hour_24 = [
    { lat: 37.754665, lng: -122.403242 },
    { lat: 37.753837, lng: -122.403172 }
  ];
  res.render('index', { 
    title: 'Express',
    resources: resources
  });
});
router.get('/login', function(req, res, next) {
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
  res.render('login', { title: 'Express' });
});

router.get('/chart', function(req, res, next) {
    res.render('chart', { 
    title: 'Express',
    resources: "aa"
  });
});
router.get('/users', function(req, res, next) {
	/**
    * Route to get all users,
    * @name /users
    */
  res.render('users', { title: 'Express' });
});

router.get('/users/:userId', function(req, res, next) {
    /**
    * Route to get users by ID,
    * @name /users/:userId
    * @param {String} :userId
    */
  res.render('user', { title: 'Express' });
});

module.exports = router;
