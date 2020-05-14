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
        /*const pool = new Pool({
            user: this.user,
            host: this.host,
            database: this.database,
            password: this.password,
            port: this.port,
        }); */
        this.pool = new Pool();
        return pool;
    }

    storeMessage = async (type, stamp, message) => {
        const insertion = 'INSERT INTO message_history(type, stamp, data) VALUES($1, $2, $3) RETURNING id'
        console.log("inserting")
        // TODO Use store pool
        new Pool().connect((err, client, done) => {
            const shouldAbort = err => {
                if (err) {
                    console.error('Error in transaction', err.stack)
                    client.query('ROLLBACK', err => {
                        if (err) {
                            console.error('Error rolling back client', err.stack)
                        }
                        // release the client back to the pool
                        done()
                    })
                }
                return !!err
            }
            client.query('BEGIN', err => {
                if (shouldAbort(err)) return
                const queryText = insertion
                client.query(queryText, [type, stamp, JSON.stringify(message)], (err, res) => {
                    console.log(res);
                    if (shouldAbort(err)) return
                    client.query('COMMIT', err => {
                        if (err) {
                            console.error('Error committing transaction', err.stack)
                        }
                        done()
                    })
                })
            })
        })
    }

}

const db = new MessageHistoryDatabase('a', 'a', 'a', 'a', 'a');
function store_message(message) {
    const date = new Date();
    const { type } = message;
    console.log(date.getTime());
    console.log(type);
    db.storeMessage(type, date, message);
}

async function eventHandler(event) {
    const { topic, partition, message } = event;
    const prefix = `${topic}[${partition} | ${message.offset}] / ${message.timestamp}`
    try {
        const json = JSON.parse(message.value.toString());
        console.log(`${prefix}: ${JSON.stringify(json)}`);
        store_message(json);
    } catch (e) {
        console.log(e); // error in the above string (in this case, yes)!
    }
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