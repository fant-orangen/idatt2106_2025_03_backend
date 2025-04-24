// run by: node server.js
const https = require('https');
const fs = require('fs');
const express = require('express');

const app = express();

app.get('/', (req, res) => {
    res.send('Hello from HTTPS server!');
});

// Read certificate and key
const options = {
    key: fs.readFileSync('path/to/your/server.key'),
    cert: fs.readFileSync('path/to/your/server.cert') // endre disse til der hvor key osv ligger
};

// Create the HTTPS server
https.createServer(options, app).listen(443, () => {
    console.log('HTTPS server running on port 443');
});
