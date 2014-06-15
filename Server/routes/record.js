/**
 * Created by bhou on 6/10/14.
 */
var moments = require('moment');
var mongoose = require('mongoose');
var db = require('../lib/db');
var Record = require('../lib/dao').Record;

/**
 * trust level: 0 not trusted, 1 partial trusted, F fully trusted
 * 'xxxxx' from left to right, who, which, when, where, what
 * @param req
 * @param res
 */
function create(req, res) {
    // get body
    var user = req.body.user;
    var device = req.body.device;
    var takenTime = req.body.takenTime;
    var regTime = moments().valueOf();
    var coords = req.body.coords;
    var md5 = req.body.md5;

    // generated parameter
    var upTime = 0;
    var position = 'undefined';
    var server = '';
    var link = '';

    // trust levels
    var who = 'F';
    var which = 'F';
    var when = 'F';
    var where = 'F';
    var what = 'F';

    // check parameter
    var errCode;
    var errMsg;
    if (user == null) {
        errCode = 701;
        errMsg = "Need permission";
        who = '0';
    }

    if (device == null) {
        errCode = 702;
        errMsg = "Device information is missing";
        which = '0';
    }

    if (regTime - takenTime > 1000 * 60 * 10) {   // register after 10 minutes
        errCode = 703;
        errMsg = "photo not registered on time";
        when = '0';
    }

    if (coords == null || coords.altitude == null || coords.longitude == null) {
        errCode = 704;
        errMsg = "no position information";
        where = '0';
    }

    if (md5 == null) {
        errCode = 705;
        errMsg = "md5 of the photo could not be null";
        where = '0';

        res.end(JSON.stringify({
            code: errCode,
            msg: errMsg
        }));

        return;
    }

    var trustLvl = who + which + when + where + what;
    var recordParam = {
        user: mongoose.Types.ObjectId(),
        device: device,
        takenTime: takenTime,
        regTime: regTime,
        coords: coords,
        md5: md5,

        upTime: upTime,
        position: position,
        server: server,
        link: link,
        trustLvl: trustLvl
    };

    // create new db record
    db.newRecord(recordParam, function (err, record) {
        if (err) {
            console.error(err);
            res.end(JSON.stringify({
                code: 400,
                msg: err,
                trustLvl: trustLvl
            }));
            return;
        }
        res.end(JSON.stringify({
            code: 200,
            msg: "succeed",
            id: record._id,
            trustLvl: trustLvl
        }));
    });

}

function findRecordById(req, res) {
    var id = req.params.id;

    Record.findById(id, function (err, record) {
        if (err) {
            res.status(404);
            return res.end('No such photo!');
        }

        return res.end(JSON.stringify(record));
    });
}

module.exports = {
    create: create,
    findRecordById: findRecordById
}