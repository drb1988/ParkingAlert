var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var session = require('express-session');

var routes = require('./routes/index'); 
var users = require('./routes/users');

var passport = require('passport');
var initPassport = require(__dirname + '/config/passport/init.js');
initPassport(passport);

var webRoutes = require('./routes/web-routes')(passport);
var signup = require('./routes/signup-routes');
var notifications = require('./routes/notificationRoutes');
var jwtMiddleware = require('express-jwt');
var config = require('./config');

var dbConfig = require('./db');
var mongoose = require('mongoose');

//mongoose.connect(dbConfig.url);//ORM for Schema data like users
var parkingCollection = dbConfig.collection;
var app = express();

var expressSession = require('express-session');
app.use(expressSession({
    secret: '2C23-4D14-WpPQ38S',
    resave: false,
    saveUninitialized: false
}));
app.use(passport.initialize());
app.use(passport.session());
// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
app.use(favicon(path.join(__dirname, 'public/img', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', routes);
app.use('/users', jwtMiddleware({ secret: config.jwt.secret }), users);
app.use('/notifications', jwtMiddleware({ secret: config.jwt.secret }), notifications);
app.use('/signup', signup);
app.use('/web-routes', webRoutes);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});

app.listen(3000, function() {
  console.log("App listening on port " + 3000 );
});

module.exports = app;
