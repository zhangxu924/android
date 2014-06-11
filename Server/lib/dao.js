/**
 * Created by bhou on 6/9/14.
 */
var mongoose = require("mongoose");
var Schema = mongoose.Schema;
var ObjectId = Schema.Types.ObjectId;


/**
 * Schema of a photo record
 * @type {Schema}
 */
var RecordSchema = new Schema({
    user: ObjectId,                     // who takes this photo
    mobile: String,                     // which mobile phone is used to take this photo
    takenTime: Number,                      // when this photo is taken
    regTime: Number,                      // when this photo is registered
    upTime: Number,                         // photo upload time
    coords: {
        latitude: Number,
        longitude: Number,
        altitude: Number,
        accuracy: Number,
        altitudeAccuracy: Number,
        heading: Number,
        speed: Number,
        timestamp: Number
    },                   // where this photo is taken
    position: String,                    // human readable position
    md5: String,                         // md5 of the photo content
    server: String,                     // hosted server name
    link: String,                       // link of the image
    trustLvl: Number                    // the level of trust, 5 digitals (0-9)
});

module.exports = {
    Record: mongoose.model('Record', RecordSchema)
}