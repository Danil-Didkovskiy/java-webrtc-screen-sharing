const STREAMER_PEER_ID = 'streamer';
const socket = io('/');

let streamerPeer;
let connection;

/**
 * Connects the streamer browser to the WebRTC server.
 */
function connectStreamerToWebRtcServer() {
    streamerPeer = new Peer(STREAMER_PEER_ID);
    streamerPeer.on('open', () => {
        socket.emit('join-streamer');
    });

    socket.on('receiver-disconnected', () => {
        if (connection) {
            connection.close();
        }
    });
}

/**
 * Starts a screen sharing session between the streamer and the receiver.
 */
function startScreenSharing() {
    navigator.mediaDevices.getDisplayMedia({
        video: {
            cursor: 'always'
        },
        audio: {
            echoCancellation: true,
            noiseSuppression: true
        }
    }).then(stream => {
        if (streamerPeer) {
            connection = streamerPeer.call("receiver", stream);
        }
        stream.getVideoTracks()[0].onended = closeRemoteSharing;
    });
}

/**
 * Notifies the server that screen sharing is stopped.
 */
function closeRemoteSharing() {
    socket.emit('screen-sharing-stopped');
    connection.close();
}
