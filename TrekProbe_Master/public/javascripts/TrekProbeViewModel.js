// Model
var StreamModel = function(id, data, name) {
    this.id = id;
    this.streamName = name;
    this.name = ko.observable();
    this.isPlaying = ko.observable(false);

    this.update(data);
};

ko.utils.extend(StreamModel.prototype, {
    update: function(data) {
        this.name(data.name);
    }
});

// Knockout View Model
var TrekProbeViewModel = function (client, path) {
    /**
     * View Options
     */
    viewOptionsIsOn = ko.observable(true);
    viewHMDOffUni = ko.observable(false);
    viewHMDOnMono = ko.observable(false);
    viewHMDOffBiDir = ko.observable(false);
    viewHMDOnStereo = ko.observable(false);

    oculusViewOn = ko.observable(false);
 //   toggleRender = ko.observable();

    /**
     * Stream Variables and methods
     */
    var client = client,
        path = path,
        mediaConfig = {
            audio:true,
            video: {
                mandatory: {},
                optional: []
            }
        },
        availableStreams = ko.observable([]),
        isStreaming = ko.observable(false),
        name = ko.observable('Master'),
        link = ko.observable(),
        localVideoEl = document.getElementById('localVideo');

    // push changes to server
    ko.computed(function() {
        if(isStreaming()) {
            client.send('update', {
                name: name()
            });
        }
    }).extend({throttle: 500});

    function getReadyToStream(stream) {
        attachMediaStream(localVideoEl, stream);
        client.setLocalStream(stream);
        client.send('readyToStream', {
                name: name()
            }
        );
        link(window.location.host + "/" + client.getId());
        isStreaming(true);
    }

    function getStreamById(id) {
        for(var i=0; i<availableStreams().length;i++) {
            if (availableStreams()[i].id === id) {return availableStreams()[i];}
        }
    }
    function loadStreamsFromServer() {
        // Load JSON data from server
        $.getJSON(path, function(data) {

            var mappedStreams = [];
            for(var remoteId in data) {
                var stream = getStreamById(remoteId);
                var streamName = "Master";

                // To control android flags
                if (data[remoteId].name == "SlaveTrekProbe") {
                    slaveDroidIsOn(true);
                    streamName = data[remoteId].name;
                }
                else if (data[remoteId].name == "SecondCameraTrekProbe") {
                    secondCameraDroidIsOn(true);
                    streamName = data[remoteId].name;
                }
                else{
                    secondCameraDroidIsOn(false);
                    slaveDroidIsOn(false);
                }

                // if stream is known, keep its state and update it
                if(!!stream) {
                    stream.update(data[remoteId]);
                    mappedStreams.push(stream);
                    // else create a new stream (escape own stream)
                } else {
                    if(remoteId !== client.getId()) {
                        mappedStreams.push(new StreamModel(remoteId, data[remoteId], streamName));
                    }
                }
            }

            if(mappedStreams.length <= 0){
                secondCameraDroidIsOn(false);
                slaveDroidIsOn(false);
            }

            availableStreams(mappedStreams);

        });
    }
    /**
     * Stream Variables and methods
     */

    /**
     * Options variables
     */
    var hmdIsOn = ko.observable(false),
        // Vision Options related variables
        stereoVision = ko.observable(false),
        bidirectionalVideo = ko.observable(false),

        // Controls Options related variables
        gestAndMovControls = ko.observable(false);

    /**
     * Android Slave FLAGS
     */
    var slaveDroidIsOn = ko.observable(false),
        secondCameraDroidIsOn = ko.observable(false);

    /**
     * Options variables
     */

    return {

        /**
         * Options methods
         */
        viewOptionsIsOn: viewOptionsIsOn, // change between options and view (Turn Options menu on/off)
        viewHMDOffUni: viewHMDOffUni,
        viewHMDOnMono: viewHMDOnMono,
        viewHMDOffBiDir: viewHMDOffBiDir,
        viewHMDOnStereo: viewHMDOnStereo,
        hmdIsOn: hmdIsOn,
        stereoVision : stereoVision, // Turn Stereoscopic Vision on/off
        bidirectionalVideo: bidirectionalVideo, // Turn Bidirectional Video (Telepresence Call) on/off

        gestAndMovControls: gestAndMovControls, // Turn Gesture and Movements controls on/off

        slaveDroidIsOn: slaveDroidIsOn,
        secondCameraDroidIsOn: secondCameraDroidIsOn,

        oculusViewOn: oculusViewOn,

  //      toggleRender: toggleRender,

        viewOptionsOnOff: function(){
            if (!viewOptionsIsOn()) {
                // client.removeStream(); // To kill streams
                viewOptionsIsOn(true);

                viewHMDOffUni(false);
                viewHMDOffBiDir(false);
                viewHMDOnMono(false);
                viewHMDOnStereo(false);

                oculusViewOn(false);

        //        oculusRender.setShowingView(false);
            } else {
                viewOptionsIsOn(false);

                if(!hmdIsOn() && !bidirectionalVideo()) { // Case: HMD is off and stream is unidirectional
                    viewHMDOffUni(true);
                    viewHMDOffBiDir(false);
                    viewHMDOnMono(false);
                    viewHMDOnStereo(false);

                    oculusViewOn(false);

          //          oculusRender.setShowingView(false);
                }
                else if(!hmdIsOn() && bidirectionalVideo){
                    viewHMDOffUni(false);
                    viewHMDOffBiDir(true);
                    viewHMDOnMono(false);
                    viewHMDOnStereo(false);

                    oculusViewOn(false);

             //       oculusRender.setShowingView(false);
                }
                else if(hmdIsOn && !stereoVision()){
                    viewHMDOffUni(false);
                    viewHMDOffBiDir(false);
                    viewHMDOnMono(true);
                    viewHMDOnStereo(false);

                    oculusViewOn(true);

           //         oculusRender.setShowingView(true);
                }
                else if(hmdIsOn && stereoVision()){
                    viewHMDOffUni(false);
                    viewHMDOffBiDir(false);
                    viewHMDOnMono(false);
                    viewHMDOnStereo(true);

                    oculusViewOn(true);

        //         oculusRender.setShowingView(true);
                }
            }
        },

        hmdOnOff: function(){
            var currentvalue = document.getElementById('hmdOnOff').value;
            if (currentvalue == "Off") {
                document.getElementById("hmdOnOff").value = "On";
                hmdIsOn(true);
            } else {
                document.getElementById("hmdOnOff").value = "Off";
                hmdIsOn(false);
            }
        },

        stereoVisionOnOff: function(){
            var currentvalue = document.getElementById('stereoVisionOnOff').value;
            if (currentvalue == "Off") {
                document.getElementById("stereoVisionOnOff").value = "On";
                stereoVision(true);
            } else {
                document.getElementById("stereoVisionOnOff").value = "Off";
                stereoVision(false);
            }
        },

        bidirVideoOnOff: function(){
            var currentvalue = document.getElementById('bidirVideoOnOff').value;
            if (currentvalue == "Off") {
                document.getElementById("bidirVideoOnOff").value = "On";
                bidirectionalVideo(true);
            } else {
                document.getElementById("bidirVideoOnOff").value = "Off";
                bidirectionalVideo(false);
            }
        },

        gestAndMovControls: function(){
            var currentvalue = document.getElementById('gestAndMovControls').value;
            if (currentvalue == "Off") {
                document.getElementById("gestAndMovControls").value = "On";
                gestAndMovControls(true);
            } else {
                document.getElementById("gestAndMovControls").value = "Off";
                gestAndMovControls(false);
            }
        },

/*        toggleRender: function(){
            oculusRender.toggleRender();
        },
  */
        /**
         * Options methods
         */

        /**
         * Stream and protocol methods
         */
        streams: availableStreams,
        isStreaming: isStreaming,
        name: name,
        link: link,

        localCamButtonText: ko.computed(
            function() {
                return isStreaming() ? "stop" : "start";
            }
        ),

        refresh: loadStreamsFromServer,

        sendMsgProtocol: function(stream, event) {
            client.sendMsg(stream.id, event.which);
        },

        toggleLocalVideo: function() {
            if(isStreaming()){
                client.send('leave');
                localVideoEl.src = '';
                client.setLocalStream(null);
                isStreaming(false);
            } else {
                getUserMedia(mediaConfig, getReadyToStream, function () {
                    throw new Error('Failed to get access to local media.');
                });
            }
        },

        toggleRemoteVideo: function(stream) {
            client.clientInit(stream.id, stream.streamName);
            stream.isPlaying(!stream.isPlaying());
        },

        // TODO: REMOVE THIS
        answerCall: function(stream) {
            var remoteId = stream.id || stream;
            getUserMedia(
                mediaConfig,
                function (strm) {
                    getReadyToStream(strm);
                    client.pushStream(remoteId, stream.streamName);
                    getStreamById(remoteId).isPlaying(true);
                },
                function () {
                    throw new Error('Failed to get access to local media.');
                }
            );
        }
        /**
         * Stream and protocol methods
         */
    }
}