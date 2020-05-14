import pg from "pg";
import dotenv from 'dotenv';
import kafka from 'kafkajs';

const { config } = dotenv;
const { Kafka } = kafka;
const { Client, Pool } = pg;

const conf = config();

const kafkaHost = process.env.KAFKA_HOST;
const kafkaPort = process.env.KAFKA_PORT;

const kafkaClient = new Kafka({
    clientId: 'message-history-consumer',
    brokers: [`${kafkaHost}:${kafkaPort}`]
});
const consumer = kafkaClient.consumer({ groupId: process.env.KAFKA_GROUP_ID })

class MessageHistoryDatabase {
  constructor(host, port, database, user, password) {
    this.host = host;
    this.port = port;
    this.database = database;
    this.user = user;
    this.password = password;
  }

  createPool = async () => {
    const pool = new Pool({
        user: this.user,
        host: this.host,
        database: this.database,
        password: this.password,
        port: this.port,
      });
      this.client = pool;
      return pool;
  }

  

  storeMessage = async (message) =>  {
    
  }

}

function store_message(message) {
    const date = new Date();
    const { type } = message;
    console.log(date.getTime());
    console.log(type);
}

async function eventHandler(event) {
    const { topic, partition, message } = event;
    const prefix = `${topic}[${partition} | ${message.offset}] / ${message.timestamp}`
    const json = JSON.parse(message.value.toString());
    console.log(`${prefix}: ${JSON.stringify(json)}`);
    store_message(json);
}


async function subscribe(consumer, handler) {
    await consumer.connect()
    await consumer.subscribe({ topic: 'test', fromBeginning: true })
    await consumer.run({
        eachMessage: handler,
    });
}

const s = async () => {
    subscribe(consumer, eventHandler) 
};

s();