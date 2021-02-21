# Message history consumer

This microservice consumes Kafka topic defined in the .env file and 
stores them to the message-history-db in defined format. Message
contents are stored as jsonb to postgres.

Barebones POC for consuming kafka stream and storing it to postgres
as json. Could be improved at some point to be fancier.

## Requirements

- Build the custom docker postgres database in infrastructure/postgres
- Run the infra with `docker-compose up -d`
- Then use `node app.mjs`

You can customize environment variables in .env file.