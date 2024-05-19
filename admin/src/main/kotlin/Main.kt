import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button

val barColor = Color(0xFFA4BE5C)
enum class MenuState { DATA, ABOUT_APP, SCRAPER, GENERATOR }
enum class ScraperChoice { NONE, WEATHER, AIR_QUALITY }

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
            }    ) {
        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .clickable { menuState.value = MenuState.DATA },
            contentAlignment = Alignment.Center
        ) {
            DataNavbarItem(menuState, modifier)
        }

        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .clickable { menuState.value = MenuState.SCRAPER },
            contentAlignment = Alignment.Center
        ) {
            ScraperNavbarItem(menuState, modifier)
        }

        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .clickable { menuState.value = MenuState.GENERATOR },
            contentAlignment = Alignment.Center
        ) {
            GeneratorNavbarItem(menuState, modifier)
        }

        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .clickable { menuState.value = MenuState.ABOUT_APP },
            contentAlignment = Alignment.Center
        ) {
            AboutAppNavbarItem(menuState, modifier)
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
fun DataTab(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("DATA")
    }
}

@Composable
fun ScraperTab(modifier: Modifier = Modifier) {
    var scraperChoice by remember { mutableStateOf(ScraperChoice.NONE) }
    val weatherResults = remember { mutableStateOf(WeatherResults()) }
    val qualityResults = remember { mutableStateOf(QualityResults()) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { scraperChoice = ScraperChoice.WEATHER }) {
                Text("Scrape Weather Data")
            }
            Button(onClick = { scraperChoice = ScraperChoice.AIR_QUALITY }) {
                Text("Scrape Air Quality Data")
            }
        }

        when (scraperChoice) {
            ScraperChoice.WEATHER -> {
                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            weatherResults.value = WebScraper.scrapeWeatherData()
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(state = listState) {
                        items(weatherResults.value.weatherTableRows) { weather ->
                            ScrapedDataRow(name = weather.name, data = weather.data)
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(scrollState = listState)
                    )
                }
            }

            ScraperChoice.AIR_QUALITY -> {
                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            qualityResults.value = WebScraper.scrapeQualityData()
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(state = listState) {
                        items(qualityResults.value.qualityTableRows) { quality ->
                            ScrapedDataRow(name = quality.name, data = quality.data)
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(scrollState = listState)
                    )
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Select an option to scrape data")
                }
            }
        }
    }
}

@Composable
fun ScrapedDataRow(name: String?, data: Map<String, String?>) {
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
            Text(text = name ?: "Unknown location", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
            data.forEach { (key, value) ->
                Text("$key: $value", modifier = Modifier.padding(bottom = 4.dp))
            }
        }
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
