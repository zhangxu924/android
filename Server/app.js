
/**
 * Module dependencies.
 */

var express = require('express');
var http = require('http');
var path = require('path');
var https = require('https');
var fs = require('fs');
var passport = require('passport');

var record = require('./routes/record');
var db = require('./lib/db');
db.connect();

var routes = require('./routes');
var user = require('./routes/user');

var app = express();

// all environments
app.set('port', process.env.PORT || 3003);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.cookieParser());
app.use(express.json());
app.use(express.urlencoded());
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(express.session({ secret: 'keyboard cat' }));
app.use(passport.initialize());
app.use(passport.session());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));


// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

function ensureAuthenticated(req, res, next) {
  var token = req.header('token');

  if (token == null) {
    return res.end(JSON.stringify({
      code: 401,
      message: 'Unauthorized'
    }));
  }

  db.findUserByApiKey(token, function(err, user) {
    if (err || user == null) {
      return res.end(JSON.stringify({
        code: 401,
        message: 'Unauthorized'
      }));
    }

    return next();
  });
}

// api
app.get('/', ensureAuthenticated, routes.index);

// saml login
app.get('/samlLogin',
  passport.authenticate('saml', { failureRedirect: '/', failureFlash: true }),
  function(req, res) {
    res.end('OK!');
  }
);
app.post('/saml/callback', passport.authenticate('saml', { failureRedirect: '/', failureFlash: true }),
  user.loginCallback
);

// api login
app.post('/login', ensureAuthenticated, user.login);

// record
app.post('/record', ensureAuthenticated, record.create);
app.get('/record/:id', ensureAuthenticated,  record.findRecordById);

var privateKey = fs.readFileSync('resource/key.pem');
var certificate = fs.readFileSync('resource/cert.pem');
var credentials = {key: privateKey, cert: certificate};

var httpsServer = https.createServer(credentials, app).listen(app.get('port')+1, function(){
  console.log('Certifoto server listening on port ' + (app.get('port')+1));
});

