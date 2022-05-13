const express = require('express');
const app = express();
const server = require('http').Server(app);
const io = require('socket.io')(server);

const room = 'room';

server.listen(3000);

app.use(express.static('public'));

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/public/index.html');
});

io.on('connection', (socket) => {
    socket.on('join-tech-support', () => {
        socket.join(room);
        socket.on('disconnect', () => {
            socket.to(room).emit('tech-support-disconnected');
        });
    });

    socket.on('join-customer', () => {
        socket.join(room);
        socket.on('disconnect', () => {
            socket.to(room).emit('customer-disconnected');
        });
    });

    socket.on('support-requested', (customerId) => {
        socket.to(room).emit('answer-support-request', customerId);
    });

    socket.on('support-request-accepted', () => {
        socket.to(room).emit('share-screen');
    });

    socket.on('screen-sharing-stopped', () => {
        socket.to(room).emit('remove-video');
    });
});