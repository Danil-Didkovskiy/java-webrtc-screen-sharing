# Java Remote Screen Sharing via WebRTC

This example project demonstrates how to share the entire screen with a single button click in one Java desktop application and observe the result in another using [JxBrowser](https://www.teamdev.com/jxbrowser).

For simplicity, we have configured a one-to-one WebRTC connection between two hardcoded clients.

## Requirements

- JDK 8+
- Node.js 16.15.0

## Setup

Open a terminal and run the following commands:

```bash
cd server/
npm install
```

## Run

Start the server:

```bash
node server.js [-p 3000]
```

Run clients in other terminals with the same `port` value in arguments:

```bash
cd ..
./gradlew runCustomerClient -Pport=3000
```

```bash
./gradlew runTechSupportClient -Pport=3000
```
