const express = require('express');
const app = express();
const http = require('http');
const server = http.createServer(app);
const { Server } = require("socket.io");
const io = new Server(server);

const room = 'room';

const args = process.argv.slice(2);
const port = args.length > 0 ? args[0] === '-p' ? args[1] : 3000 : 3000;

server.listen(port);

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