import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val barColor = Color(0xFFb0c985)
val secondColor = Color(0xFFd1dfb8)
val selectedColor = Color(0xFF9ebd68)
val selectedSecondColor = Color(0xFFc6d8a7)
enum class MenuState { DATA, ABOUT_APP, SCRAPER, GENERATOR }
enum class ScraperChoice { NONE, WEATHER, AIR_QUALITY, SEND_DATA }

@Composable
fun Menu(menuState: MutableState<MenuState>, modifier: Modifier = Modifier) {
    val borderColor = Color.Black
    val borderWidth = 0.2.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(barColor)
            .drawBehind {
                val strokeWidth = borderWidth.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = borderColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .background(if (menuState.value == MenuState.DATA) selectedColor else barColor)
                .clickable { menuState.value = MenuState.DATA },
            contentAlignment = Alignment.Center
        ) {
            DataNavbarItem(menuState, modifier)
        }

        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .background(if (menuState.value == MenuState.SCRAPER) selectedColor else barColor)
                .clickable { menuState.value = MenuState.SCRAPER },
            contentAlignment = Alignment.Center
        ) {
            ScraperNavbarItem(menuState, modifier)
        }

        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .background(if (menuState.value == MenuState.GENERATOR) selectedColor else barColor)
                .clickable { menuState.value = MenuState.GENERATOR },
            contentAlignment = Alignment.Center
        ) {
            GeneratorNavbarItem(menuState, modifier)
        }

        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .background(if (menuState.value == MenuState.ABOUT_APP) selectedColor else barColor)
                .clickable { menuState.value = MenuState.ABOUT_APP },
            contentAlignment = Alignment.Center
        ) {
            AboutAppNavbarItem(menuState, modifier)
        }
    }
}

@Composable
fun ScraperMenu(scraperChoice: ScraperChoice, onScraperChoiceChange: (ScraperChoice) -> Unit, weatherScraped: Boolean, airQualityScraped: Boolean) {
    val borderColor = Color.Black
    val borderWidth = 0.2.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(secondColor)
            .drawBehind {
                val strokeWidth = borderWidth.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = borderColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .background(if (scraperChoice == ScraperChoice.WEATHER) selectedSecondColor else secondColor)
                .clickable { onScraperChoiceChange(ScraperChoice.WEATHER) },
            contentAlignment = Alignment.Center
        ) {
            WeatherScraperNavbarItem(Modifier)
        }

        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .background(if (scraperChoice == ScraperChoice.AIR_QUALITY) selectedSecondColor else secondColor)
                .clickable { onScraperChoiceChange(ScraperChoice.AIR_QUALITY) },
            contentAlignment = Alignment.Center
        ) {
            AirScraperNavbarItem(Modifier)
        }

        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .background(if (scraperChoice == ScraperChoice.SEND_DATA) selectedSecondColor else secondColor)
                .clickable(enabled = weatherScraped || airQualityScraped) { onScraperChoiceChange(ScraperChoice.SEND_DATA) },
            contentAlignment = Alignment.Center
        ) {
            SendNavbarItem(Modifier, enabled = weatherScraped || airQualityScraped)
        }
    }
}

@Composable
fun DataRow(name: String?, data: Map<String, String?>, onDelete: () -> Unit,onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
            .border(
                width = 0.5.dp,
                color = Color.LightGray,
                shape = MaterialTheme.shapes.small
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(text = name ?: "Unknown location", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            data.forEach { (key, value) ->
                Text("$key: ${value?.ifEmpty { "N/A" } ?: "N/A"}", modifier = Modifier.padding(bottom = 4.dp))
            }
        }
        Row(
            modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onEdit, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)) {
                Text("Edit")
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                Text("Delete")
            }

        }
    }
}

@Composable
fun DataNavbarItem(menuState: MutableState<MenuState>, modifier: Modifier) {
    val invoiceIcon: Painter = painterResource("images/menu_icon.png")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = invoiceIcon,
            contentDescription = "Data",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Data")
    }
}

@Composable
fun ScraperNavbarItem(menuState: MutableState<MenuState>, modifier: Modifier) {
    val scraperIcon: Painter = painterResource("images/scraper_icon.png")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = scraperIcon,
            contentDescription = "Scraper",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Scraper")
    }
}

@Composable
fun GeneratorNavbarItem(menuState: MutableState<MenuState>, modifier: Modifier) {
    val generatorIcon: Painter = painterResource("images/generator_icon.png")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = generatorIcon,
            contentDescription = "Generator",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Generator")
    }
}

@Composable
fun AboutAppNavbarItem(menuState: MutableState<MenuState>, modifier: Modifier) {
    val aboutAppIcon: Painter = painterResource("images/about_app_icon.png")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = aboutAppIcon,
            contentDescription = "About App",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "About")
    }
}

@Composable
fun WeatherScraperNavbarItem(modifier: Modifier) {
    val weatherIcon: Painter = painterResource("images/weather_icon.png")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = weatherIcon,
            contentDescription = "Weather",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Scrape Weather Data")
    }
}

@Composable
fun AirScraperNavbarItem(modifier: Modifier) {
    val airQualityIcon: Painter = painterResource("images/air_quality_icon.png")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = airQualityIcon,
            contentDescription = "Air Quality",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Scrape Air Quality Data")
    }
}

@Composable
fun SendNavbarItem(modifier: Modifier, enabled: Boolean) {
    val sendIcon: Painter = painterResource("images/send_icon.png")

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (enabled) {
            Image(
                painter = sendIcon,
                contentDescription = "Send",
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Send Data", color = if (enabled) Color.Unspecified else Color.Gray)
    }
}

@Composable
fun DataTab(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("DATA")
    }
}
@Composable
fun EditWeatherDialog(
    weather: Weather,
    onDismiss: () -> Unit,
    onSave: (Weather) -> Unit
) {
    var newName by remember { mutableStateOf(weather.name ?: "") }
    var newData by remember { mutableStateOf(weather.data.toMutableMap()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Weather Data") },
        text = {
            Column {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                newData.forEach { (key, value) ->
                    var updatedValue by remember { mutableStateOf(value ?: "") }
                    TextField(
                        value = updatedValue,
                        onValueChange = { newValue ->
                            updatedValue = newValue
                            newData[key] = newValue
                        },
                        label = { Text(key) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(weather.copy(name = newName, data = newData))
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditAirQualityDialog(
    airQuality: AirQuality,
    onDismiss: () -> Unit,
    onSave: (AirQuality) -> Unit
) {
    var newName by remember { mutableStateOf(airQuality.name ?: "") }
    var newData by remember { mutableStateOf(airQuality.data.toMutableMap()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Air Quality Data") },
        text = {
            Column {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                newData.forEach { (key, value) ->
                    var updatedValue by remember { mutableStateOf(value ?: "") }
                    TextField(
                        value = updatedValue,
                        onValueChange = { newValue ->
                            updatedValue = newValue
                            newData[key] = newValue
                        },
                        label = { Text(key) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(airQuality.copy(name = newName, data = newData))
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun ScraperTab(modifier: Modifier = Modifier) {
    var scraperChoice by remember { mutableStateOf(ScraperChoice.NONE) }
    var weatherScraped by remember { mutableStateOf(false) }
    var airQualityScraped by remember { mutableStateOf(false) }
    val weatherResults = remember { mutableStateOf(WeatherResults(mutableListOf())) }
    val qualityResults = remember { mutableStateOf(QualityResults(mutableListOf())) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var editingWeather by remember { mutableStateOf<Weather?>(null) }
    var editingAirQuality by remember { mutableStateOf<AirQuality?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        ScraperMenu(scraperChoice, onScraperChoiceChange = { choice ->
            if (choice == ScraperChoice.SEND_DATA) {
                coroutineScope.launch {
                    scraperChoice = ScraperChoice.SEND_DATA
                    delay(2000)
                    if (weatherScraped) {
                        weatherResults.value.weatherTableRows.forEach { weather ->
                            WebScraper.sendWeatherData(weather)
                        }
                        scraperChoice = ScraperChoice.WEATHER
                    } else if (airQualityScraped) {
                        qualityResults.value.qualityTableRows.forEach { airQuality ->
                            WebScraper.sendQualityData(airQuality)
                        }
                        scraperChoice = ScraperChoice.AIR_QUALITY
                    } else {
                        println("No data to send")
                    }
                }
            } else {
                scraperChoice = choice
            }
        }, weatherScraped, airQualityScraped)

        when (scraperChoice) {
            ScraperChoice.WEATHER -> {
                LaunchedEffect(scraperChoice) {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            weatherResults.value = WebScraper.scrapeWeatherData()
                            weatherScraped = true
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(state = listState) {
                        items(weatherResults.value.weatherTableRows) { weather ->
                            DataRow(
                                name = weather.name,
                                data = weather.data,
                                onDelete = {
                                    weatherResults.value = weatherResults.value.copy(
                                        weatherTableRows = weatherResults.value.weatherTableRows.toMutableList().apply {
                                            remove(weather)
                                        }
                                    )
                                },
                                onEdit = { editingWeather = weather }
                            )
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(scrollState = listState)
                    )
                }
            }

            ScraperChoice.AIR_QUALITY -> {
                LaunchedEffect(scraperChoice) {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            qualityResults.value = WebScraper.scrapeQualityData()
                            airQualityScraped = true
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(state = listState) {
                        items(qualityResults.value.qualityTableRows) { quality ->
                            DataRow(
                                name = quality.name,
                                data = quality.data,
                                onDelete = {
                                    qualityResults.value = qualityResults.value.copy(
                                        qualityTableRows = qualityResults.value.qualityTableRows.toMutableList().apply {
                                            remove(quality)
                                        }
                                    )
                                },
                                onEdit = { editingAirQuality = quality }
                            )
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(scrollState = listState)
                    )
                }
            }

            ScraperChoice.SEND_DATA -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sending data...")
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Select an option to scrape data")
                }
            }
        }
    }

    editingWeather?.let { weather ->
        EditWeatherDialog(
            weather = weather,
            onDismiss = { editingWeather = null },
            onSave = { updatedWeather ->
                weatherResults.value = weatherResults.value.copy(
                    weatherTableRows = weatherResults.value.weatherTableRows.toMutableList().apply {
                        val index = indexOfFirst { it.name == weather.name }
                        if (index != -1) {
                            set(index, updatedWeather)
                        }
                    }
                )
                editingWeather = null
            }
        )
    }

    editingAirQuality?.let { airQuality ->
        EditAirQualityDialog(
            airQuality = airQuality,
            onDismiss = { editingAirQuality = null },
            onSave = { updatedAirQuality ->
                qualityResults.value = qualityResults.value.copy(
                    qualityTableRows = qualityResults.value.qualityTableRows.toMutableList().apply {
                        val index = indexOfFirst { it.name == airQuality.name }
                        if (index != -1) {
                            set(index, updatedAirQuality)
                        }
                    }
                )
                editingAirQuality = null
            }
        )
    }
}

@Composable
fun GeneratorTab(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("GENERATOR")
    }
}

@Composable
fun AboutAppTab(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("About application", style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Subject: Principles of programming languages")
        Text("Author: Digitalni Trojcki")
    }
}

@Composable
fun Content(menuState: MutableState<MenuState>, modifier: Modifier = Modifier) {
    when (menuState.value) {
        MenuState.DATA -> DataTab(modifier = modifier)
        MenuState.SCRAPER -> ScraperTab(modifier = modifier)
        MenuState.GENERATOR -> GeneratorTab(modifier = modifier)
        MenuState.ABOUT_APP -> AboutAppTab(modifier = modifier)
    }
}

@Composable
@Preview
fun App() {
    val menuState = remember { mutableStateOf(MenuState.DATA) }

    Column {
        Menu(menuState)
        Content(menuState = menuState, modifier = Modifier.weight(1f))
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Admin App",
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(800.dp, 700.dp)
        ),
        undecorated = false,
        resizable = true
    ) {
        App()
    }
}
