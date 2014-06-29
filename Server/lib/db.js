/**
 * Created by bhou on 6/10/14.
 */

var config = require('../config');
var mongoose = require('mongoose');
var	Schema = mongoose.Schema;
var ObjectId = mongoose.Schema.Types.ObjectId;

var passport = require('passport');
var dao = require('./dao');
var Record = dao.Record;
var User = dao.User;

SamlStrategy = require('./passport-saml').Strategy;

passport.serializeUser(function(user, done) {
  done(null, user.email);
});

passport.deserializeUser(function(id, done) {
  findByEmail(id, function (err, user) {
    done(err, user);
  });
});

passport.use(new SamlStrategy(
  config.samlOptions,
  function(profile, done) {
    console.log("Auth with", profile);
    if (!profile.email) {
      return done(new Error("No email found"), null);
    }
    // asynchronous verification, for effect...
    process.nextTick(function () {
      findByEmail(profile.email, function(err, user) {
        if (err) {
          return done(err);
        }
        if (!user) {
          // "Auto-registration"
          return new User({email: profile.email, token: profile.token}).save(function(err, user) {
            return done(null, profile);
          });
        }
        return done(null, user);
      })
    });
  }
));
/**
 * connect to db
 */
function connect() {
    var conn = 'mongodb://' + config.mongostore.username + ':' + config.mongostore.password + '@'
        + config.mongostore.host + ':' + config.mongostore.port + '/' + config.mongostore.db;

    mongoose.connect(conn);

    mongoose.connection.on('open', function(){
       console.log('connected to db');
    });
}

function findUserByApiKey(apikey, fn) {
  User.find({"token": apikey}, function(err, users) {
    if (err) {
      return fn(err, null);
    }

    if (users == null || users.length == 0){
      return fn(null, null);
    }

    return fn(null, users[0]);
  });
}

function findByEmail(email, fn) {
  User.find({"email": email}, function(err, users) {
    if (err) {
      return fn(err, null);
    }

    if (users == null || users.length == 0){
      return fn(null, null);
    }

    return fn(null, users[0]);
  });
}


/**
 * create a new record
 */
function newRecord(param, cb) {
  var record = new Record(param);

  record.save(function(err, obj){
    cb(err, obj);
  });
}

module.exports = {
    connect: connect,
    newRecord: newRecord,
    findUserByApiKey: findUserByApiKey
}