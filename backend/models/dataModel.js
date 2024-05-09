var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var dataSchema = new Schema({
	'data_series_id' : {
	 	type: Schema.Types.ObjectId,
	 	ref: 'dataSeries'
	},
	'timestamp' : Date,
	'data' : String
});

module.exports = mongoose.model('data', dataSchema);
