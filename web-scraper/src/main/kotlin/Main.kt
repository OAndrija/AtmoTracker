package org.example

import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.eachText
import it.skrape.selects.html5.*
import it.skrape.selects.html5.td

data class Country(
    val city: String,
    val temperature: String,
    val windSpeed: String,
    val windGusts: String,
    val precipitation: String
)

data class ScrapingResult(val countries: MutableList<Country> = mutableListOf(), var count: Int = 0)

fun main() {
    val website_url = "https://en.wikipedia.org/wiki/List_of_countries_and_dependencies_by_population"
    val places = skrape(HttpFetcher) {
        request {
            url = "https://meteo.arso.gov.si/uploads/probase/www/observ/surface/text/sl/observationAms_si_latest.html"
        }

        extractIt<ScrapingResult> { results ->
            htmlDocument {
                val countryRows = table(".meteoSI-table") {
                    tr {
                        findAll { this }
                    }
                }

                countryRows
                    .drop(1)
                    .forEach { row ->
                        val cells = row.td {
                            findAll { map { it.text } }
                        }
                        val country = Country(
                            city = cells[0],
                            temperature = cells[2],
                            windSpeed = cells[5],
                            windGusts = cells[6],
                            precipitation = cells[8]
                        )
                        results.countries.add(country)
                        println("Added: $country")
                        println(countryRows.size)
                    }
            }
        }
    }
}