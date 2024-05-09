var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var dataSeriesSchema = new Schema({
	'name' : String,
	'tags' : Array,
	'location' : String
});

module.exports = mongoose.model('dataSeries', dataSeriesSchema);
