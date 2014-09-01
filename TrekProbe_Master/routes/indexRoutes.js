module.exports = function(app, streams) {

    // GET home
    var index = function(req, res) {
    res.render('index', {
                          title: 'Immersive Telerobotic Modular Framework',
                          header: 'TrekProbe [Master]',
                          footer: 'Filipe Rodrigues - Immersive Telerobotic Modular Framework',
                          id: req.params.id
                        });
    };

    // GET streams as JSON
    var getStreamsAsJSON = function(req, res) {
        var streamList = streams.getStreams();
        var data = (JSON.parse(JSON.stringify(streamList)));
        res.json(200, data);
    };

    app.get('/streams', getStreamsAsJSON);
    app.get('/', index);
    app.get('/:id', index); // recebe um GET com o parametro "id"
}