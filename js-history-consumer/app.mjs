import pg from "pg";
import dotenv from 'dotenv';


const s = async () => {
    const { Client } = pg;
    const { config } = dotenv;

    config();
    const client = new Client();
    
    await client.connect()
    const res = await client.query('SELECT $1::text as message', ['Hello world!'])
    console.log(res.rows[0].message) // Hello world!
    
    await client.end()
};

s();