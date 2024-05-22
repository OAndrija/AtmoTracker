import io.github.serpro69.kfaker.Faker
import kotlin.random.Random

data class FakeData(
    val name: String,
    val temperature: Int = 0,
    val windSpeed: Int = 0,
    val windGusts : Int = 0,
    val precipitation: Int = 0,
    val pm10: Int = 0,
    val pm25: Int = 0,
    val so2: Int = 0,
    val co: Int = 0,
    val ozon: Int = 0,
    val no2: Int = 0,
    val benzen: Int = 0
)


class DataGenerator {
    private val faker = Faker()

    fun generateFakeWeatherData(
        numberOfRecords: Int,
        temperatureRange: IntRange,
        windSpeedRange: IntRange,
        windGustsRange: IntRange,
        precipitation: IntRange,
    ): List<FakeData> {
        return List(numberOfRecords) {
            FakeData(
                name = faker.address.city(),
                temperature = Random.nextInt(temperatureRange.first, temperatureRange.last + 1),
                windSpeed = Random.nextInt(windSpeedRange.first, windSpeedRange.last + 1),
                windGusts = Random.nextInt(windGustsRange.first, windGustsRange.last + 1),
                precipitation = Random.nextInt(precipitation.first, precipitation.last + 1),
            )
        }
    }

    fun generateFakeAirQualityData(
        numberOfRecords: Int,
        pm10Range: IntRange,
        pm25Range: IntRange,
        so2Range: IntRange,
        coRange: IntRange,
        ozonRange: IntRange,
        no2Range: IntRange,
        benzenRange: IntRange
    ): List<FakeData> {
        return List(numberOfRecords) {
            FakeData(
                name = faker.address.city(),
                pm10 = Random.nextInt(pm10Range.first, pm10Range.last + 1),
                pm25 = Random.nextInt(pm25Range.first, pm25Range.last + 1),
                so2 = Random.nextInt(so2Range.first, so2Range.last + 1),
                co = Random.nextInt(coRange.first, coRange.last + 1),
                ozon = Random.nextInt(ozonRange.first, ozonRange.last + 1),
                no2 = Random.nextInt(no2Range.first, no2Range.last + 1),
                benzen = Random.nextInt(benzenRange.first, benzenRange.last + 1),
            )
        }
    }
}
