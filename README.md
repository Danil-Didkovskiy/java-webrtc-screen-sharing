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

Then run clients in other terminals:

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