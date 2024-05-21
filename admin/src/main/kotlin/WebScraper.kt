import kotlinx.serialization.Serializable
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

@Serializable
data class WeatherResults(val weatherTableRows: MutableList<Weather> = mutableListOf())

@Serializable
data class QualityResults(val qualityTableRows: MutableList<AirQuality> = mutableListOf())

object WebScraper {
    fun scrapeWeatherData(): WeatherResults {
        val requestUrl =
            "https://meteo.arso.gov.si/uploads/probase/www/observ/surface/text/sl/observationAms_si_latest.html"
        val results = WeatherResults()

        try {
            skrape(HttpFetcher) {
                request {
                    url = requestUrl
                }
                extractIt<WeatherResults> {
                    htmlDocument {
                        val tableRows = table(".meteoSI-table") {
                            findFirst {
                                tr {
                                    findAll { this }
                                }
                            }
                        }

                        tableRows.drop(1).forEach { row ->
                            val cells = row.td {
                                findAll { map { it.text } }
                            }
                            val weather = Weather(
                                name = "Weather ${cells[0]}",
                                data = mapOf(
                                    "temperature" to cells[2],
                                    "windSpeed" to cells[5],
                                    "windGusts" to cells[6],
                                    "precipitation" to cells[8]
                                )
                            )
                            results.weatherTableRows.add(weather)

                        }
                    }
                }
            }
            return results
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
            return results
        }
    }

    fun scrapeQualityData(): QualityResults {
        val requestUrl = "https://www.arso.gov.si/zrak/kakovost%20zraka/podatki/dnevne_koncentracije.html"
        val results = QualityResults()
        try {

            skrape(HttpFetcher) {
                request {
                    url = requestUrl
                }

                extractIt<QualityResults> {
                    htmlDocument {
                        val tableRows = table(".online") {
                            findFirst {
                                tr {
                                    findAll { this }
                                }
                            }
                        }

                        tableRows.drop(2).dropLast(1).forEach { row ->
                            val cells = row.td {
                                findAll { map { it.text } }
                            }
                            val quality = AirQuality(
                                name = "AirQuality ${cells[0]}",

                                data = mapOf(
                                    "pm10" to cells[1],
                                    "pm25" to cells[2],
                                    "so2" to cells[3],
                                    "co" to cells[4],
                                    "ozon" to cells[5],
                                    "no2" to cells[6],
                                    "benzen" to cells[7]
                                )
                            )
                            results.qualityTableRows.add(quality)

                        }
                    }
                }
            }
            return results

        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
            return results
        }
    }

    fun sendWeatherData(weather: Weather) {
        val client = OkHttpClient()
        val jsonWeather = Json.encodeToString(Weather.serializer(), weather)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonWeather.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://localhost:3001/data")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("Request failed with code ${response.code}")
                println("Response message: ${response.message}")
                println("Response body: ${response.body?.string()}")
                throw IOException("Unexpected code $response")
            }
            println("Response: ${response.body?.string()}")
        }
    }

    fun sendQualityData(airQuality: AirQuality) {
        val client = OkHttpClient()
        val jsonWeather = Json.encodeToString(AirQuality.serializer(), airQuality)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonWeather.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://localhost:3001/data")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("Request failed with code ${response.code}")
                println("Response message: ${response.message}")
                println("Response body: ${response.body?.string()}")
                throw IOException("Unexpected code $response")
            }
            println("Response: ${response.body?.string()}")
        }
    }
}