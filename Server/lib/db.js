/**
 * Created by bhou on 6/10/14.
 */

var config = require('../config');
var mongoose = require('mongoose');

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
function newRecord() {

}

module.exports = {
    connect: connect
}