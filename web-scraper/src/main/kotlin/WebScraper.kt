package org.example

import kotlinx.serialization.Serializable


import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.*

@Serializable
data class WeatherResults(val weatherTableRows: MutableList<Weather> = mutableListOf(), var count: Int = 0)
@Serializable
data class QualityResults(val qualityTableRows: MutableList<AirQuality> = mutableListOf(), var count: Int = 0)

object WebScraper {
    fun scrapeWeatherData():WeatherResults {
        val requestUrl =
            "https://meteo.arso.gov.si/uploads/probase/www/observ/surface/text/sl/observationAms_si_latest.html"
        val results = WeatherResults()
        try {
             skrape(HttpFetcher) {
                request {
                    url = requestUrl
                }

                extractIt<WeatherResults> { results ->
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
                                city = cells[0],
                                temperature = cells[2],
                                windSpeed = cells[5],
                                windGusts = cells[6],
                                precipitation = cells[8]
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
    fun scrapeQualityData():QualityResults {
        val requestUrl = "https://www.arso.gov.si/zrak/kakovost%20zraka/podatki/dnevne_koncentracije.html"
val results=QualityResults()
        try {

            skrape(HttpFetcher) {
                request {
                    url = requestUrl
                }

                extractIt<QualityResults> { results ->
                    htmlDocument {
                        val tableRows = table(".online") {
                            findFirst {
                                tr {
                                    findAll { this }
                                }
                            }
                        }

                        tableRows.drop(2).forEach { row ->
                            val cells = row.td {
                                findAll { map { it.text } }
                            }
                            val quality = AirQuality(
                                city = cells[0],
                                pm10 = cells[1],
                                pm25 = cells[2],
                                so2 = cells[3],
                                co = cells[4],
                                ozon = cells[5],
                                no2 = cells[6],
                                benzen = cells[7]
                            )
                            results.qualityTableRows.add(quality)

                        }
                    }
                }
            }
            return  results

        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
            return results
        }
    }
}