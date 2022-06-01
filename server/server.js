import express from 'express';
import http from 'http';
import { Server as SocketIO } from "socket.io";
import { Command } from 'commander';
import * as url from 'url';

const app = express();
const httpServer = http.createServer(app);
const io = new SocketIO(httpServer);
const room = 'room';
const __dirname = url.fileURLToPath(new URL('.', import.meta.url));

app.use(express.static('public'));
app.get('/streamer', (req, res) => {
    res.sendFile(__dirname + 'public/streamer.html');
});
app.get('/receiver', (req, res) => {
    res.sendFile(__dirname + 'public/receiver.html');
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

httpServer.listen(getPortValue());

function getPortValue() {
    const program = new Command();
    const defaultPort = 3000;

    program
        .option('-p, --port <value>', 'port value');
    program.parse(process.argv);

    const options = program.opts();
    return options.port ? options.port : defaultPort;
}