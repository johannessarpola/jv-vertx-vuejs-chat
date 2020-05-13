import { Client } from "pg";


class Database {
  constructor(host, port, database, user, password) {
    this.host = host;
    this.port = port;
    this.database = database;
    this.user = user;
    this.password = password;
  }

  connectedClient = async () => {
    const client = new Client({
        user: this.user,
        host: this.host,
        database: this.database,
        password: this.password,
        port: this.port,
      });
      await client.connect();
      return client;
  }
}
