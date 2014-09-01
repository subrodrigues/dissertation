module.exports = function(io, streams) {

    // SERVER
  io.sockets.on('connection', function(client) { // CLIENT LIGA-SE A SERVER

    console.log('-- ' + client.id + ' joined --');
    client.emit('id', client.id); // SERVER ENVIA MSG PARA CLIENT

    client.on('message', function (details) {
      var otherClient = io.sockets.sockets[details.to];

      if (!otherClient) {
        return;
      }
        delete details.to;
        details.from = client.id;
        otherClient.emit('message', details);
    });

    client.on('actuator_protocol', function (details) {
        var otherClient = io.sockets.sockets[details.to];

        if (!otherClient) {
          return;
        }
          delete details.to;
          details.from = client.id;
          otherClient.emit('actuator_protocol', details);
      });

    client.on('readyToStream', function(options) {
      console.log('-- ' + client.id + ' is ready to stream --');
      
      streams.addStream(client.id, options.name); 
    });
    
    client.on('update', function(options) {
      streams.update(client.id, options.name);
    });

    function leave() {
      console.log('-- ' + client.id + ' left --');
      streams.removeStream(client.id);
    }
    
    client.on('disconnect', leave);
    client.on('leave', leave);
  });
};