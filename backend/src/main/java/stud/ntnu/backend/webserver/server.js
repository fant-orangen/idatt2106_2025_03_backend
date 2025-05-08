/**
 * Set up HTTPS-server with Node.js and Express.
 */

const https = require('https');
const fs = require('fs');
const express = require('express');

const app = express();

app.get('/', (req, res) => {
    res.send('Hello from HTTPS server!');
});

// Read certificate and key
const options = {
    key: fs.readFileSync('server.key'),
    cert: fs.readFileSync('server.cert')
};

// Create the HTTPS server
https.createServer(options, app).listen(443, () => {
    console.log('HTTPS server running on port 443');
});