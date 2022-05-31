import express from 'express';
import http from 'http';
import { Server as SocketIO } from "socket.io";
import { Command } from 'commander';

const app = express();
const httpServer = http.createServer(app);
const io = new SocketIO(httpServer);
const room = 'room';
const program = new Command();

program
    .option('-p, --port <value>', 'port value');

program.parse(process.argv);

const options = program.opts();
const defaultPort = 3000;
const port = options.port ? options.port : defaultPort;

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
