const express = require('express');
const { MongoClient } = require('mongodb');

const app = express();
const mongoUri = 'mongodb+srv://stanojabozinov:nojco123@p1.allgmev.mongodb.net/?retryWrites=true&w=majority&appName=P1';

async function main() {
    const client = new MongoClient(mongoUri, { useNewUrlParser: true, useUnifiedTopology: true });

    try {
        await client.connect();

    
        await listDatabases(client);

    } catch (e) {
        console.error(e);
    } finally {
        //close
        await client.close();
    }
}

main().catch(console.error);

async function listDatabases(client) {
    const databasesList = await client.db().admin().listDatabases();
    console.log("Databases:");
    databasesList.databases.forEach(db => console.log(` - ${db.name}`));
};

app.get('/', (req, res) => {
    res.send('Hello World connected to DB!');
});

const port = process.env.PORT || 3000;
app.listen(port, () => {
    console.log(`App running on port ${port}`);
});
