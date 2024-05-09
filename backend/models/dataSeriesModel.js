var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var dataSeriesSchema = new Schema({
	name: {
		type: String,
		required: true
	},
	tags: [{
		type: [String],
		required: true
	}],
	location: {
		latitude: {
		  type: Number,
		  required: true
		},
		longitude: {
		  type: Number,
		  required: true
		}
	}
});

module.exports = mongoose.model('dataSeries', dataSeriesSchema);
