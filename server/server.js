import express from 'express';
import http from 'http';
import { Server as SocketIO } from "socket.io";

const app = express();
const httpServer = http.createServer(app);
const io = new SocketIO(httpServer);
const room = 'room';

const defaultPort = 3000;
const args = process.argv.slice(2);
const port = args.length > 0 ? args[0] === '-p' ? args[1] : defaultPort : defaultPort;

httpServer.listen(port);

app.use(express.static('public'));

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/public/index.html');
});

io.on('connection', (socket) => {
    socket.on('join-receiver', () => {
        socket.join(room);
        socket.on('disconnect', () => {
            socket.to(room).emit('receiver-disconnected');
        });
    });

    socket.on('join-streamer', () => {
        socket.join(room);
        socket.on('disconnect', () => {
            socket.to(room).emit('streamer-disconnected');
        });
    });

    socket.on('screen-sharing-stopped', () => {
        socket.to(room).emit('remove-video');
    });
});
