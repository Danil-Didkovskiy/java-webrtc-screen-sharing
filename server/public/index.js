const TECH_SUPPORT_PEER_ID = 'tech-support';

const socket = io('/');

let customerPeer;
let techSupportPeer;
let call;

/**
 * Provides tech support peer initialization
 * and subscribes to all events related to the tech support side.
 */
function initializeTechSupport() {
    techSupportPeer = new Peer(TECH_SUPPORT_PEER_ID);
    techSupportPeer.on('open', () => {
        socket.emit('join-tech-support');
    });

    techSupportPeer.on('call', (call) => {
        call.answer();
        call.on('stream', (stream) => {
            addVideoStream(stream);
        });
    });

    socket.on('answer-support-request', (customerId) => {
        window.techSupportBrowser.displayAcceptMessage(customerId);
    });

    socket.on('remove-video', () => {
        removeVideoStream();
    });

    socket.on('customer-disconnected', () => {
        removeVideoStream();
    });
}

/**
 * Provides customer peer initialization with given customer id
 * and subscribes to all events related to the customer side.
 *
 * @param {string} customerId
 */
function initializeCustomer(customerId) {
    customerPeer = new Peer(customerId);
    customerPeer.on('open', () => {
        socket.emit('join-customer');
    });

    socket.on('share-screen', () => {
        startScreenSharing();
    });

    socket.on('tech-support-disconnected', () => {
        if (call) {
            call.close();
        }
    });
}

/**
 * Emits an event to the server to notify that customer has requested a support session.
 */
function notifySupportRequested() {
    socket.emit('support-requested', customerPeer.id);
}

/**
 * Emits an event to the server to notify that the support request was accepted.
 */
function notifySupportRequestAccepted() {
    socket.emit('support-request-accepted');
}

/**
 * Receives a video stream of the customer's screen and makes a call
 * to tech support peer with the provision of the received stream.
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
        if (customerPeer) {
            call = customerPeer.call(TECH_SUPPORT_PEER_ID, stream);
        }
        listenToStreamEnded(stream);
    });
}

/**
 * Listens for the end of the stream and emits an event to the server
 * to notify that screen sharing has stopped.
 *
 * @param {MediaStream} stream
 */
function listenToStreamEnded(stream) {
    stream.getVideoTracks()[0].onended = () => {
        socket.emit('screen-sharing-stopped');
        call.close();
    };
}

/**
 * Displays the video element with the given stream as the source of the media.
 *
 * @param {MediaStream} stream
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
