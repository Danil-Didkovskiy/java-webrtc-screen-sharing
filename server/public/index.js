const RECEIVER_PEER_ID = 'receiver';
const STREAMER_PEER_ID = 'streamer';

const socket = io('/');

let receiverPeer;
let streamerPeer;
let call;

/**
 * Connects the receiver browser to the WebRTC server.
 */
function connectReceiverToWebRtcServer() {
    receiverPeer = new Peer(RECEIVER_PEER_ID);
    receiverPeer.on('open', () => {
        socket.emit('join-receiver');
    });

    receiverPeer.on('call', (call) => {
        call.answer();
        call.on('stream', (stream) => {
            addVideoStream(stream);
        });
    });

    socket.on('remove-video', () => {
        removeVideoStream();
    });

    socket.on('streamer-disconnected', () => {
        removeVideoStream();
    });
}

/**
 * Connects the streamer browser to the WebRTC server.
 */
function connectStreamerToWebRtcServer() {
    streamerPeer = new Peer(STREAMER_PEER_ID);
    streamerPeer.on('open', () => {
        socket.emit('join-streamer');
    });

    socket.on('receiver-disconnected', () => {
        if (call) {
            call.close();
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
            call = streamerPeer.call(RECEIVER_PEER_ID, stream);
        }
        stream.getVideoTracks()[0].onended = closeRemoteSharing;
    });
}

/**
 * Notifies the server that screen sharing is stopped.
 */
function closeRemoteSharing() {
    socket.emit('screen-sharing-stopped');
    call.close();
}

/**
 * Displays the video element with the given stream as the source of the media.
 *
 * @param {MediaStream} stream media stream to be displayed
 */
function addVideoStream(stream) {
    const video = document.querySelector('video');
    video.srcObject = stream;
    video.style.display = 'block';
}

/**
 * Clears the video element and hides it.
 */
function removeVideoStream() {
    const video = document.querySelector('video');
    video.srcObject = null;
    video.style.display = 'none';
}
