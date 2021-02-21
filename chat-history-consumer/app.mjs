import pg from "pg";
import dotenv from 'dotenv';
import kafka from 'kafkajs';

const {config} = dotenv;
const {Kafka} = kafka;
const {Client, Pool} = pg;

const createPool = async (host, port, database, user, password) => {
  return new Pool({
    host: host,
    port: port,
    database: database,
    user: user,
    password: password,
  });
}


const storeMessage = async (pool, message) => {
  const insertion = 'INSERT INTO message_history(stamp, message) VALUES($1, $2) RETURNING id'

  console.log("inserting")
  if (pool != null) {
    pool.connect((err, client, done) => {
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
        client.query(queryText, [new Date(), message], (err, res) => {
          if (shouldAbort(err)) return
          client.query('COMMIT', err => {
            if (err) {
              console.error('Error committing transaction', err.stack)
            }
            console.log(`inserted ${JSON.stringify(message)} successfully`)
            done()
          })
        })
      })
    })

  } else {
    console.error("Initialize the database pool first");
  }
}

const eventHandler = async (event) => {
  const {topic, partition, message} = event;
  const prefix = `${topic}[${partition} | ${message.offset}] / ${message.timestamp}`
  try {
    this.messageSink(JSON.parse(message.value.toString()));
  } catch (e) {
    console.log(e); // error in the above string (in this case, yes)!
  }
}

/*
class KafkaEventHandler {
  constructor(messageSink) {
    this.messageSink = messageSink;
  }

  eventHandler = async (event) => {
    const {topic, partition, message} = event;
    const prefix = `${topic}[${partition} | ${message.offset}] / ${message.timestamp}`
    try {
      this.messageSink(JSON.parse(message.value.toString()));
    } catch (e) {
      console.log(e); // error in the above string (in this case, yes)!
    }
  }

}


 */

async function subscribe(consumer, handler) {
  await consumer.connect()
  await consumer.subscribe({topic: process.env.KAFKA_TOPIC, fromBeginning: true})
  await consumer.run({
    eachMessage: (msg) => {
      handler(msg)
    },
  });
}


// Load .env
config();

const kafkaClient = new Kafka({
  clientId: 'message-history-consumer',
  brokers: [`${process.env.KAFKA_HOST}:${process.env.KAFKA_PORT}`]
});

const consumer = kafkaClient.consumer({groupId: process.env.KAFKA_GROUP_ID})
const pool = await createPool(process.env.DB_HOST, process.env.DB_PORT, process.env.DB, process.env.DB_USER, process.env.DB_PASSWORD);

const event_pipe = async (event) => {
  const {topic, partition, message} = event;
  const prefix = `${topic}[${partition} | ${message.offset}] / ${message.timestamp}`
  try {
    await storeMessage(pool, JSON.parse(message.value.toString()));
  } catch (e) {
    console.log(e); // error in the above string (in this case, yes)!
  }
}

subscribe(consumer, event_pipe)