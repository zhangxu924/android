/**
 * Created by bhou on 6/10/14.
 */
var moments = require('moment');
var db = require('../lib/db');
var Record = require('../lib/dao').Record;

function create (req, res) {
    // get body
    var user = req.body.user;
    var mobile = req.body.mobile;
    var takenTime = req.body.takenTime;
    var regTime = moments().valueOf();
    var coords = req.body.coords;
    var md5 = req.body.md5;

    // generated parameter
    var upTime = 0;
    var position = 'undefined';
    var server = '';
    var link = '';
    var trustLvl = '00000';

    console.log('user: ' + user);
    console.log('mobile: ' + mobile);
    console.log('takenTime: ' +takenTime);
    console.log('coords: ' +coords);
    console.log('md5: ' +md5);

    console.log('body' + JSON.stringify(req.body));
    res.end('body: ' + JSON.stringify(req.body));
}

module.exports = {
    create: create
}