# Java Remote Screen Sharing via WebRTC

Java implementation of WebRTC screen sharing between customer client application
and a technical support client application using [JxBrowser](https://www.teamdev.com/jxbrowser).

For the sake of simplicity, this implementation demonstrates
the one-to-one connection between two hardcoded clients.

## Requirements

- JDK >= 9.
- Node.js 16.15.0

## Setup

Open a terminal and run the following commands:

```bash
cd server/
npm install
```

## Run

Start the server with specifying the `port` value. 
The default `port` value is 3000.

```bash
node server.js -p port
```

Then run clients in other terminals with the same `port` value in arguments.
Make sure you are in the root directory.

Run `CustomerClient`:

```bash
./gradlew runCustomerClient --args='-p port'
```

Run `TechSupportClient`:

```bash
./gradlew runTechSupportClient --args='-p port'
```