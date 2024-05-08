var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var weatherSchema = new Schema({
	'city_id' : {
	 	type: Schema.Types.ObjectId,
	 	ref: 'city'
	},
	'timestamp' : Date,
	'temperature' : Number,
	'wind_speed' : Number,
	'wind_gust' : Number,
	'precipitation' : Number
});

module.exports = mongoose.model('weather', weatherSchema);
