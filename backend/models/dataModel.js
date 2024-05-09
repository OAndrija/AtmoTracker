var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var dataSchema = new Schema({
	'data_series_id' : {
	 	type: Schema.Types.ObjectId,
	 	ref: 'dataSeries',
		required: true
	},
	timestamp: {
		type: Date,
		required: true
	},
	data: {
		type: Map,
		of: Schema.Types.Mixed,
		required: true
	}
});

module.exports = mongoose.model('data', dataSchema);
