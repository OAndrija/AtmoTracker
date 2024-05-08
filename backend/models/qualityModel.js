var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var qualitySchema = new Schema({
	'city_id' : {
	 	type: Schema.Types.ObjectId,
	 	ref: 'city',
		required: 'true'
	},
	'timestamp' : { type: Date, default: Date.now },
	'pm10' : Number,
	'pm25' : Number,
	'so2' : String,
	'co' : String,
	'ozon' : String,
	'no2' : String,
	'benzen' : String
});

module.exports = mongoose.model('quality', qualitySchema);
