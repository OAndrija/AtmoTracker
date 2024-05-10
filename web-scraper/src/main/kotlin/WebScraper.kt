package org.example

import kotlinx.serialization.Serializable


import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.*

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
}