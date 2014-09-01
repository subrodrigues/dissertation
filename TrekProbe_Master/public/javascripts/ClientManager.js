var ClientManager = function () {
    // CLIENT
	var localId;
	var config = {
			peerConnectionConfig: {
				iceServers: [
				             {"url": "stun:23.21.150.121"},
				             {"url": "stun:stun.l.google.com:19302"}
				             ]
			},
			peerConnectionConstraints: {
				optional: [
				           {"DtlsSrtpKeyAgreement": (browser === 'firefox')}
				           ]
			},
			mediaConstraints: {
				'mandatory': {
					'OfferToReceiveAudio': true,
					'OfferToReceiveVideo': true
				}
			}
	};
	var clientDatabase = {};
	var localStream,
	server = io.connect(window.location.origin);

	server.on('message', handleMessage); // RECEBE MSG DO SERVER E LIDA COM ELA
	server.on('id', function(id) {
		localId = id;
	});

	function addClient(remoteId, name) {
		var peer = new Client(config.peerConnectionConfig, config.peerConnectionConstraints, name);

		peer.pc.onicecandidate = function(event) {
			if (event.candidate) {
				send('candidate', remoteId, {
					label: event.candidate.sdpMLineIndex,
					id: event.candidate.sdpMid,
					candidate: event.candidate.candidate
				});
			}
		};
		peer.pc.onaddstream = function(event) {
			attachMediaStream(peer.remoteVideoEl, event.stream);
			remoteVideosContainer.appendChild(peer.remoteVideoEl);
        };
		peer.pc.onremovestream = function(event) {
			peer.remoteVideoEl.src = '';
			remoteVideosContainer.removeChild(peer.remoteVideoEl);
        };
		peer.pc.oniceconnectionstatechange = function(event) {
			switch(
					(  event.srcElement // Chrome
							|| event.target   ) // Firefox
							.iceConnectionState) {
							case 'disconnected':
								remoteVideosContainer.removeChild(peer.remoteVideoEl);
                                break;
			}
		};
		clientDatabase[remoteId] = peer;

		return peer;
	}

	function answer(remoteId) {
		var pc = clientDatabase[remoteId].pc;
		pc.createAnswer(
				function(sessionDescription) {
					pc.setLocalDescription(sessionDescription);
					send('answer', remoteId, sessionDescription);
				}, 
				function(error) { 
					console.log(error);
				},
				config.mediaConstraints
		);
	}

	function offer(remoteId) {
		var pc = clientDatabase[remoteId].pc;
		pc.createOffer(
				function(sessionDescription) {
					pc.setLocalDescription(sessionDescription);
					send('offer', remoteId, sessionDescription);
				}, 
				function(error) { 
					console.log(error);
				},
				config.mediaConstraints
		);
	}

	function handleMessage(message) {
		var type = message.type,
            from = message.from,
            name = message.name,
            pc = (clientDatabase[from] || addClient(from, name)).pc;

		console.log('received ' + type + ' from ' + from);

		switch (type) {
		case 'init':
			toggleLocalStream(pc);
			offer(from);
			break;
		case 'offer':
			pc.setRemoteDescription(new RTCSessionDescription(message.payload));
			answer(from);
			break;
		case 'answer':
			pc.setRemoteDescription(new RTCSessionDescription(message.payload));
			break;
		case 'candidate':
			if(pc.remoteDescription) {

				pc.addIceCandidate(new RTCIceCandidate({
					sdpMLineIndex: message.payload.label,
					sdpMid: message.payload.id,
					candidate: message.payload.candidate
				}));
			}
			break;
		}
	}

	function send(type, to, payload) {
		console.log('sending ' + type + ' to ' + to);

		server.emit('message', {
			to: to,
			type: type,
			payload: payload
		});
	}

	function sendActuatorProtocol(type, to, payload){
		server.emit('actuator_protocol', {
			to: to,
			type: type,
			payload: payload
		});
	}

	function toggleLocalStream(pc) {
		if(localStream) {
			(!!pc.getLocalStreams().length) ? pc.removeStream(localStream) : pc.addStream(localStream);
		}
	}

	return {
		getId: function() {
			return localId;
		},

		setLocalStream: function(stream) {

			// if local cam has been stopped, remove it from all outgoing streams.
			if(!stream) {
				for(id in clientDatabase) {
					pc = clientDatabase[id].pc;
					if(!!pc.getLocalStreams().length) {
						pc.removeStream(localStream);
						offer(id);
					}
				}
			}

			localStream = stream;
		},

		clientInit: function(remoteId, name) {
			peer = clientDatabase[remoteId] || addClient(remoteId, name);
			send('init', remoteId, null);
		},

        send: function(type, payload) {
            server.emit(type, payload);
        },

        sendMsg: function(remoteId, keycode) {
			switch(keycode){
                case 87:
                    sendActuatorProtocol('actuator_protocol', remoteId, {msg: 'front'});
                 //   alert('message sent ' + remoteId);
                    break;
                case 83:
                    sendActuatorProtocol('actuator_protocol', remoteId, {msg: 'back'});
                    //   alert('message sent ' + remoteId);
                    break;
			}
		},

        // TODO: REMOVE THIS
        pushStream: function(remoteId, name) {
			peer = clientDatabase[remoteId] || addClient(remoteId, name);
			toggleLocalStream(peer.pc);
			send('init', remoteId, null);
		},

        removeStream: function(){
            peer.remoteVideoEl.src = '';
            remoteVideosContainer.removeChild(peer.remoteVideoEl);
        }
	};
};

var Client = function (pcConfig, pcConstraints, name) {
    this.pc = new RTCPeerConnection(pcConfig, pcConstraints);
    this.remoteVideoEl = document.createElement('video');
    this.remoteVideoEl.controls = true;
    this.remoteVideoEl.id = name; // Give name to <video> id

  //  alert(name + " was added!");
}