var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var cors = require('cors');
var mongoose = require('mongoose');
var session = require('express-session');
var MongoStore = require('connect-mongo');
var WebSocket = require('ws');

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/userRoutes');
var dataRouter = require('./routes/dataRoutes');
var dataSeriesRouter = require('./routes/dataSeriesRoutes');

var app = express();

// MongoDB Atlas connection URI
var mongoDB = "mongodb+srv://stanojabozinov:nojco123@p1.allgmev.mongodb.net/?retryWrites=true&w=majority&appName=P1";
mongoose.connect(mongoDB, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log("MongoDB connected successfully"))
  .catch(err => console.error('MongoDB connection error:', err));

// Ensure mongoose connection is established before handling requests
mongoose.connection.on('connected', () => {
  console.log('Mongoose is connected');
});

// CORS configuration
var allowedOrigins = ['http://localhost:3000', 'http://localhost:3001'];
app.use(cors({
  credentials: true,
  origin: function(origin, callback) {
    if (!origin) return callback(null, true);
    if (allowedOrigins.indexOf(origin) === -1) {
      var msg = "The CORS policy does not allow access from the specified Origin.";
      return callback(new Error(msg), false);
    }
    return callback(null, true);
  }
}));

// View engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'hbs');

// Middleware setup
app.use(logger('dev'));
app.use(express.json()); // Ensure this is before the routes
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// Session configuration using MongoDB Atlas
app.use(session({
  secret: 'work hard',
  resave: true,
  saveUninitialized: false,
  store: MongoStore.create({ mongoUrl: mongoDB })
}));

// Make session variables accessible in all views
app.use(function(req, res, next) {
  res.locals.session = req.session;
  next();
});

// Routes setup
app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/data', dataRouter);
app.use('/dataSeries', dataSeriesRouter);

// Setting up the WebSocket server
const server = require('http').createServer(app);
const wss = new WebSocket.Server({ server });

wss.on('connection', (ws) => {
  console.log('Client connected (WebSocket)');
  broadcastData(ws); // Broadcast data immediately when client connects
  ws.on('message', (message) => {
    console.log('Received message from client:', message);
  });
  ws.on('close', () => console.log('Client disconnected'));
});

// Function to fetch and broadcast data to a specific client or all clients
const broadcastData = async (ws = null) => {
  try {
    console.log('Fetching data from API...');
    const response = await fetch('http://localhost:3001/data/all');
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    const data = await response.json();
    console.log('Data fetched succesfuly'); // Log the fetched data
    const dataToSend = JSON.stringify(data);

    if (ws) {
      // Send data to a specific client
      if (ws.readyState === WebSocket.OPEN) {
        console.log('Sending data to connected client');
        ws.send(dataToSend);
      }
    } else {
      // Send data to all clients
      wss.clients.forEach((client) => {
        if (client.readyState === WebSocket.OPEN) {
          console.log('Sending data to client');
          client.send(dataToSend);
        }
      });
    }
  } catch (err) {
    console.error('Error fetching data:', err);
  }
};

// Function to calculate the milliseconds until the next whole hour
const getTimeUntilNextHour = () => {
  const now = new Date();
  const nextHour = new Date(now);
  nextHour.setHours(now.getHours() + 1);
  nextHour.setMinutes(0, 0, 0);
  return nextHour - now;
};

// Set a timeout to broadcast data at the next whole hour
setTimeout(() => {
  broadcastData();
  // Set an interval to broadcast data every subsequent hour
  setInterval(broadcastData, 60 * 60 * 1000);
}, getTimeUntilNextHour());

// Start the server
const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});

// Catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// Error handler
app.use(function(err, req, res, next) {
  // Set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // Respond with JSON
  res.status(err.status || 500);
  res.json({ message: err.message, error: res.locals.error });
});

module.exports = app;
