## Setup

```bash
cd server/
npm install
```

## Run

Start the server:

```bash
node server.js -p port
```

Then run clients in other terminals
with the same `port` value in arguments.
The default `port` value is 3000.

```bash
cd ..
```

Run `CustomerClient`:

```bash
./gradlew runCustomerClient --args='-p port'
```

Run `TechSupportClient`:

```bash
./gradlew runTechSupportClient --args='-p port'
```