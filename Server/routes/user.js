
/*
 * GET users listing.
 */

var request = require('request');
var config = require('../config');
var dao = require('../lib/dao');
var User = dao.User;

var passport = require('passport');

function login(req, res) {
  var email = req.body.email;
  var password = req.body.password;

  // request to idp
  request({
      method: 'post',
      url: config.idpUrl + '/api/login',
      headers: {
        app: config.app,
        token: config.token,
        'content-type': 'application/json'
      },
      body: JSON.stringify({
        email: email,
        password: password
      })
    }, function(err, response, body) {
      if (err) {
        return res.end(JSON.stringify({
          code: 401,
          message: err.message
        }));
      }

      if (response == null || body == null){
        return res.end(JSON.stringify({
          code: 401,
          message: 'Unauthorized'
        }));
      }

      try{
        var bodyObj = JSON.parse(body);
        if (bodyObj.code == '200') {
          // update user token
          User.find({email: email}, function(err, users) {
            if (err) {
              return res.end(JSON.stringify({
                code: 401,
                message: err.message
              }));
            }

            var user = null;
            if (users == null || users.length == 0) {
              user = new User({email: email, token: bodyObj.message.token})
            } else {
              user = users[0];
              user.token = bodyObj.message.token;
            }

            user.save(function(err, user) {
              if (err) {
                return res.end(JSON.stringify({
                  code: 401,
                  message: err.message
                }));
              }

              return res.end(JSON.stringify({
                code: 200,
                message: bodyObj.message
              }));
            });
          });
        } else {
          return res.end(JSON.stringify({
            code: 401,
            message: 'Unauthorized'
          }));
        }
      } catch( err ) {
        return res.end(JSON.stringify({
          code: 401,
          message: err.message
        }));
      }
    });

}

function loginCallback(req, res) {
  var user = req.user;

  if (user == null) {
    return res.end(JSON.stringify({
      code: 401,
      message: 'Unauthorized'
    }))
  }

  res.cookie('token', user.token, { maxAge: 900000, httpOnly: false });
  return res.end(JSON.stringify({
    code: 200,
    message: 'OK'
  }));
}

module.exports = {
  login: login,
  loginCallback: loginCallback
}