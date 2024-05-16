var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var dataSeriesSchema = new Schema({
    name: {
        type: String,
        required: true
    },
    tags: [
        {
            type: [String],
            required: true
        }
    ],
    location: {
        type: {
            type: String,
            enum: ['Point'],
            required: true
        },
        coordinates: {
            type: [Number],
            required: true
        }
    }
});

dataSeriesSchema.index({ location: '2dsphere' });

module.exports = mongoose.model('dataSeries', dataSeriesSchema);