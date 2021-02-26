# Anonymous chat application

This application is microservice oriented room-based chatting application with complete anonymity. Everyone gets only assigned a animal name and can chat 
with each other through a room system. 

## Module explanations

- chat-backend
    - Vert.X application 
    - build with Gradle. 
    - Runs with JDK 11+.
    - Runs backend to handle room chatting between clients and internal event bus to pass the chat history to Kafka pipe.
    - Identities are random and display names are just random animal names.
    - Has no UI.
- chat-ui
    - Is a Vue.js application
    - UX done mostly with Quasar Framework 
    - You can connect to different rooms from UI and then start chatting.
    - By default accessed locally by localhost:8080
- chat-history-consumer
    - Is a barebones consumer of kafka pipe to handle message history. 
    - Consumes the Kafka stream and the stores the data to postgres as `JSONB` data.
    - Has no UI
- chat-infrastructure
    - Has the required infrastructure containers
    - You need to build postgres database image locally from postgres-folder with `make build`
    - Once database is built you can run the infrastructure with `docker-compose up -d` in cluster folder.
    - Starts 5 containers
        - kafka_zookeeper, kafka are used for Kafka
        - kafka-setup is a container which setups the topics
        - kafka-ubuntu is a virtual ubuntu with KafkaCat installed for debugging
        - history-postgres is the custom postgres container
  
## Implementation details

Technologies used
- [Websockets](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)
- [Vert.X](https://vertx.io/)
- [Kafka](https://kafka.apache.org/)
- [Postgres](https://www.postgresql.org/)
- [Vue](https://vuejs.org/)
- [Quasar Framework](https://quasar.dev/)
- [Docker](https://www.docker.com/)

Other libraries worth of mentioning
- [Node Postgres](https://node-postgres.com/)
- [KafkaJs](https://kafka.js.org/)

