const RECEIVER_PEER_ID = 'receiver';

/**
 * Connects the receiver browser to the WebRTC server.
 */
function connectReceiverToWebRtcServer() {
    const receiverPeer = new Peer(RECEIVER_PEER_ID);
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
