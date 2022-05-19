# Java Remote Screen Sharing via WebRTC

This example project demonstrates how to share the entire screen with a single button click in one Java desktop application and observe the result in another using [JxBrowser](https://www.teamdev.com/jxbrowser).

## Description

This project has two parts. The first part is a simple server implementation on Node.js, and the second part is two Java desktop applications representing clients.

On the server-side, we define the signals by which clients can communicate with the server and how we provide a screen-sharing session.
For simplicity, we have configured a one-to-one WebRTC connection between two hardcoded clients.

On the Java side, we create two desktop applications and teach them to communicate with the server.
So that one application transmits a media stream, and the second displays it.

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
