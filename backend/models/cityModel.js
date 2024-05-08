var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var citySchema = new Schema({
	'name' : String,
	'location' : {
		latitude: { type: Number, required: true },
		longitude: { type: Number, required: true }
	},
	'weather_series' : {
	 	type: Schema.Types.ObjectId,
	 	ref: 'weather'
	},
	'quality_series' : {
	 	type: Schema.Types.ObjectId,
	 	ref: 'quality'
	}
});

module.exports = mongoose.model('city', citySchema);
