package org.example

import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.*
import it.skrape.selects.html5.td
import java.time.LocalDateTime
import java.util.*

data class WeatherResults(val weatherTableRows: MutableList<Weather> = mutableListOf(), var count: Int = 0)

data class QualityResults(val tableRows: MutableList<QualityResults> = mutableListOf(), var count: Int = 0)

object WebScraper {
    fun scrapeWeatherData() {
        val requestUrl =
            "https://meteo.arso.gov.si/uploads/probase/www/observ/surface/text/sl/observationAms_si_latest.html"

        try {
            val result = skrape(HttpFetcher) {
                request {
                    url = requestUrl
                }

                extractIt<WeatherResults> { results ->
                    htmlDocument {
                        val tableRows = table(".meteoSI-table") {
                            tr {
                                findAll { this }
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
                            println("Added: $weather")
                        }
                    }
                }
            }
            println("Scraping completed with ${result.weatherTableRows.size} entries fetched.")
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }

    fun scrapeQualityData() {
        val requestUrl =
            "https://meteo.arso.gov.si/uploads/probase/www/observ/surface/text/sl/observationAms_si_latest.html"

        try {
            val result = skrape(HttpFetcher) {
                request {
                    url = requestUrl
                }

                extractIt<WeatherResults> { results ->
                    htmlDocument {
                    }
                }
            }
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }
}