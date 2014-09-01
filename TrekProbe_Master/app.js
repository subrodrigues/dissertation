/**
 * Modules
 */

var express = require('express');
var favicon = require('serve-favicon');
var logger = require('morgan');
var bodyParser = require('body-parser');
var http = require('http');
var path = require('path');
var methodOverride = require('method-override');
var router = express.Router();
var errorhandler = require('errorhandler');
var streams = require('./public/app_modules/streams.js')();

// start express server
var app = express()
    , server = http.createServer(app)
    , io = require('socket.io').listen(server);

// view engine setup
app.set('port', 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(favicon(__dirname + '/public/images/favicon.ico'));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded());
app.use(methodOverride());
app.use('/', router);

app.use(express.static(path.join(__dirname, 'public')));


/// error handlers
//development only
if ('development' == app.get('env')) {
    app.use(errorhandler());
}

//routing
require('./routes/indexRoutes.js')(app, streams);

// Server event Handler => Socket.io
require('./public/app_modules/serverHandler.js')(io, streams);

server.listen(app.get('port'), function(){
    console.log('Express server listening on port ' + app.get('port'));
});