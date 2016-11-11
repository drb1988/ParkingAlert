var request = require('supertest');
var express = require('express');
var app = require('../app.js');

  //var app = express();

request(app)
  .get('/users/getUser/5820a1665624760c68f8e039')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'application/json; charset=utf-8')
  .expect('Content-Length', '2015')
  .expect(200)
  .end(function(err, res) {
    if (err) throw err;
  });

  request(app)
  .get('/users/getUser/5820a1665624760c68f8e039')
 // .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'text/html; charset=utf-8')
  .expect('Content-Length', '3062')
  .expect(401)
  .end(function(err, res) {
    if (err) throw err;
  });

  request(app)
  .get('/users/getNotifications/5820a1665624760c68f8e039')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(200)
  .end(function(err, res) {
    if (err) throw err;
  });

  request(app)
  .get('/users/getNotification/5820a1665624760c68f8e039')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(200)
  .end(function(err, res) {
    if (err) throw err;
  });

  request(app)
  .get('/users/getNotification/5820a1665624760c68f8e')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'text/html; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(201)
  .end(function(err, res) {
    if (err) throw err;
  });

   request(app)
  .get('/users/getCars/5820a1665624760c68f8e039')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(200)
  .end(function(err, res) {
    if (err) throw err;
  });

  request(app)
  .get('/users/getUsersForCode/5820a1665624760c68f8e039')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(200)
  .end(function(err, res) {
    if (err) throw err;
  });

  request(app)
 .get('/users//enableNotifications/5820a1665624760c68f8e039&plm')
 // .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
 // .expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(401)
  .end(function(err, res) {
    if (err) throw err;
  });

   request(app)
  .get('/users/generateCarCode/5820a1665624760c68f8e039')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(200)
  .end(function(err, res) {
    if (err) throw err;
  });

   request(app)
  .get('/signup/login/mugurel.mitrut@gmail.com&plm')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(201)
  .end(function(err, res) {
    if (err) throw err;
  });

   request(app)
  .get('/signup/login/bogdandrb@gmail.com&plm')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(202)
  .end(function(err, res) {
    if (err) throw err;
  });

   request(app)
  .get('/signup/resetPassword/docmateas@yahoo.com')
 // .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(200)
  .end(function(err, res) {
    if (err) throw err;
  });

   request(app)
  .get('/notifications/receiverRead/582478d6fed0ed1d409a1db8')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  //.expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(200)
  .end(function(err, res) {
    if (err) throw err;
  });

   request(app)
  .get('/notifications/receiverRead/582478d6fed0ed1d401d')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'text/html; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(201)
  .end(function(err, res) {
    if (err) throw err;
  });

   request(app)
  .get('/notifications/receiverRead/582478d6fed0ed1d409a1db8')
  //.set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  //.expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(401)
  .end(function(err, res) {
    if (err) throw err;
  });

   request(app)
  .get('/notifications/getNotification/582478d6fed0ed1d409a1db8')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(200)
  .end(function(err, res) {
    if (err) throw err;
  });

 request(app)
  .get('/notifications/getNotification/582478d6fed0ed1d401d')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
 // .expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(201)
  .end(function(err, res) {
    if (err) throw err;
  });

   request(app)
  .get('/notifications/getNotification/582478d6fed0ed1d409a1db8')
  //.set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  //.expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(401)
  .end(function(err, res) {
    if (err) throw err;
  });


 var notificationData = {
        sender_id: '5821a3b5a1d05213408fb884',
        receiver_id: '5821d90db2453a175c7cd0c8',
        receiver_nickname: 'Fasie',
        latitude:  '47.0806636',
        longitude: '21.9242613', 
        vehicle: 'Caruta din Tileagd',
        status: 1
      };

request(app)
  .post('/notifications/notification')
  .set('Authorization', 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlM2Q3MmNjNmMyMzcxMTM4OTAyNTUxIiwiZW1haWwiOiJib2dkYW5kcmJAZ21haWwuY29tIn0.3OCqudkE0JXwjVZJrnmwylfKabp7iD4q1Wft0kMP8pM')
  .send(notificationData)
  .expect('Content-Type', 'application/json; charset=utf-8')
 // .expect('Content-Length', '2015')
  .expect(200)
  .end(function(err, res) {
    if (err) throw err;
    if (res) console.log("res test",res.body);
  });