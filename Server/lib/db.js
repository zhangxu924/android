/**
 * Created by bhou on 6/10/14.
 */

var config = require('../config');
var mongoose = require('mongoose');
var Record = require('./dao').Record;

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
    newRecord: newRecord
}