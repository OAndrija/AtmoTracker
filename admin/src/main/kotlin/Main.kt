import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
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
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.Document

val barColor = Color(0xFFb0c985)
val secondColor = Color(0xFFd1dfb8)
val selectedColor = Color(0xFF9ebd68)
val selectedSecondColor = Color(0xFFc6d8a7)
val offWhiteColor = Color(0xFFF5F5F5)

enum class MenuState { DATA, ABOUT_APP, SCRAPER, GENERATOR }
enum class ScraperChoice { NONE, WEATHER, AIR_QUALITY, SEND_DATA }
enum class GeneratorChoice { NONE, WEATHER, AIR_QUALITY, GENERATE_DATA }


suspend fun deleteDocumentById(collection: MongoCollection<Document>, id: String) {
    withContext(Dispatchers.IO) {
        collection.deleteOne(eq("_id", id))
    }
}

suspend fun updateDocumentById(collection: MongoCollection<Document>, id: String, updatedData: Map<String, String?>) {
    withContext(Dispatchers.IO) {
        val updateDoc = Document("\$set", Document(updatedData))
        collection.updateOne(eq("_id", id), updateDoc)
    }
}
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
fun ScraperMenu(
    scraperChoice: ScraperChoice,
    onScraperChoiceChange: (ScraperChoice) -> Unit,
    weatherScraped: Boolean,
    airQualityScraped: Boolean
) {
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
fun DataRow(name: String?, data: Map<String, String?>, onDelete: () -> Unit, onEdit: () -> Unit) {
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
            Text(
                text = name ?: "Unknown location",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            data.forEach { (key, value) ->
                Text("$key: ${value?.ifEmpty { "N/A" } ?: "N/A"}", modifier = Modifier.padding(bottom = 4.dp))
            }
        }
        Row(
            modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50)) // Green color to match the theme
            ) {
                Text("Edit", color = Color.White) // White text color for better contrast
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF44336)) // Red color for the delete button, keeping it as a warning color
            ) {
                Text("Delete", color = Color.White) // White text color for better contrast
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
fun DataTab(
    modifier: Modifier = Modifier
) {
    val tabs = listOf("Datas", "Data Series")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex, backgroundColor = secondColor, contentColor = Color.Black) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        val collectionName = if (selectedTabIndex == 0) "datas" else "dataseries"
        when (selectedTabIndex) {
            0 -> DatasTabContent(collectionName = collectionName)
            1 -> DataSeriesTabContent(collectionName = collectionName)
        }
    }
}

@Composable
fun DatasTabContent(collectionName: String, modifier: Modifier = Modifier) {
    var queryResult by remember { mutableStateOf<List<Document>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val database = MongoDBClient.database
    val collection = database.getCollection(collectionName)
    val lazyListState = rememberLazyListState()
    var editingDocument by remember { mutableStateOf<Document?>(null) }

    LaunchedEffect(Unit) {
        queryResult = database.getCollection(collectionName).find().toList()
    }
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        queryResult?.let { documents ->
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(documents) { document ->
                    DataRow(
                        name = document.getString("name") ?: "",
                        data = document.toMap().mapValues { it.value.toString() },
                        onDelete = {
                            coroutineScope.launch {
                                deleteDocumentById(collection, document.getObjectId("_id").toString())
                                queryResult = queryResult?.filter { it.getObjectId("_id") != document.getObjectId("_id") }
                            }
                        },
                        onEdit = {
                            editingDocument = document
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(lazyListState)
        )
    }
    editingDocument?.let { document ->
        EditDocumentDialog(
            document = document,
            onDismiss = { editingDocument = null },
            onSave = { updatedDocument ->
                coroutineScope.launch {
                    updateDocumentById(collection, document.getObjectId("_id").toString(), updatedDocument.toMap().mapValues { it.value.toString() })
                    queryResult = queryResult?.map {
                        if (it.getObjectId("_id") == document.getObjectId("_id")) updatedDocument else it
                    }
                    editingDocument = null
                }
            }
        )
    }
}

@Composable
fun EditDocumentDialog(
    document: Document,
    onDismiss: () -> Unit,
    onSave: (Document) -> Unit
) {
    var newData by remember { mutableStateOf(document.toMutableMap()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Document Data") },
        text = {
            Column {
                newData.forEach { (key, value) ->
                    var updatedValue by remember { mutableStateOf(value?.toString() ?: "") }
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
                val updatedDocument = Document(newData)
                onSave(updatedDocument)
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
fun DataSeriesTabContent(collectionName: String, modifier: Modifier = Modifier) {
    var queryResult by remember { mutableStateOf<List<Document>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val database = MongoDBClient.database
    val collection = database.getCollection(collectionName)
    val lazyListState = rememberLazyListState()
    var editingDocument by remember { mutableStateOf<Document?>(null) }

    LaunchedEffect(Unit) {
        queryResult = database.getCollection(collectionName).find().toList()
    }
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        queryResult?.let { documents ->
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(documents) { document ->
                    DataRow(
                        name = document.getString("name") ?: "",
                        data = document.toMap().mapValues { it.value.toString() },
                        onDelete = {  coroutineScope.launch {
                            deleteDocumentById(collection, document.getObjectId("_id").toString())
                            queryResult = queryResult?.filter { it.getObjectId("_id") != document.getObjectId("_id") }
                        } },
                        onEdit = { editingDocument = document }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(lazyListState)
        )
    }

    editingDocument?.let { document ->
        EditDocumentDialog(
            document = document,
            onDismiss = { editingDocument = null },
            onSave = { updatedDocument ->
                coroutineScope.launch {
                    updateDocumentById(collection, document.getObjectId("_id").toString(), updatedDocument.toMap().mapValues { it.value.toString() })
                    queryResult = queryResult?.map {
                        if (it.getObjectId("_id") == document.getObjectId("_id")) updatedDocument else it
                    }
                    editingDocument = null
                }
            }
        )
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
fun GeneratorTab(
    weatherTabState: WeatherTabState,
    airQualityTabState: AirQualityTabState,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val weatherIcon: Painter = painterResource("images/weather_icon.png")
    val airQualityIcon: Painter = painterResource("images/air_quality_icon.png")

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex, backgroundColor = secondColor, contentColor = Color.Black) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = weatherIcon,
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(text = "Weather Generator")
                    }
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = airQualityIcon,
                            contentDescription = "Air Quality Icon",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(text = "Air Quality Generator")
                    }
                }
            )
        }
        when (selectedTabIndex) {
            0 -> WeatherGeneratorTab(weatherTabState)
            1 -> AirQualityGeneratorTab(airQualityTabState)
        }
    }
}

@Composable
fun WeatherGeneratorTab(
    weatherTabState: WeatherTabState,
    modifier: Modifier = Modifier
) {
    val dataGenerator = remember { DataGenerator() }
    var numberOfRecords by remember { mutableStateOf("10") }
    var temperatureMin by remember { mutableStateOf("0") }
    var temperatureMax by remember { mutableStateOf("50") }
    var windSpeedMin by remember { mutableStateOf("0") }
    var windSpeedMax by remember { mutableStateOf("50") }
    var windGustsMin by remember { mutableStateOf("0") }
    var windGustsMax by remember { mutableStateOf("50") }
    var precipitationMin by remember { mutableStateOf("0") }
    var precipitationMax by remember { mutableStateOf("50") }
    var editingWeather by remember { mutableStateOf<FakeData?>(null) }

    val lazyListState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Button(
                    onClick = {
                        val numRecords = numberOfRecords.toIntOrNull() ?: 0
                        val minTemp = temperatureMin.toIntOrNull() ?: 0
                        val maxTemp = temperatureMax.toIntOrNull() ?: 0
                        val minWindSpeed = windSpeedMin.toIntOrNull() ?: 0
                        val maxWindSpeed = windSpeedMax.toIntOrNull() ?: 0
                        val minWindGusts = windGustsMin.toIntOrNull() ?: 0
                        val maxWindGusts = windGustsMax.toIntOrNull() ?: 0
                        val minPrecipitation = precipitationMin.toIntOrNull() ?: 0
                        val maxPrecipitation = precipitationMax.toIntOrNull() ?: 0
                        if (numRecords > 0 && minTemp <= maxTemp && minWindSpeed <= maxWindSpeed &&
                            minWindGusts <= maxWindGusts && minPrecipitation <= maxPrecipitation
                        ) {
                            weatherTabState.generatedData = dataGenerator.generateFakeWeatherData(
                                numRecords,
                                minTemp..maxTemp,
                                minWindSpeed..maxWindSpeed,
                                minWindGusts..maxWindGusts,
                                minPrecipitation..maxPrecipitation
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = barColor)
                ) {
                    Text("Generate Weather Data")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                TextField(
                    value = numberOfRecords,
                    onValueChange = { numberOfRecords = it },
                    label = { Text("Number of Records") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = temperatureMin,
                    onValueChange = { temperatureMin = it },
                    label = { Text("Min Temperature") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = temperatureMax,
                    onValueChange = { temperatureMax = it },
                    label = { Text("Max Temperature") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = windSpeedMin,
                    onValueChange = { windSpeedMin = it },
                    label = { Text("Min Wind Speed") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = windSpeedMax,
                    onValueChange = { windSpeedMax = it },
                    label = { Text("Max Wind Speed") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = windGustsMin,
                    onValueChange = { windGustsMin = it },
                    label = { Text("Min Wind Gusts") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = windGustsMax,
                    onValueChange = { windGustsMax = it },
                    label = { Text("Max Wind Gusts") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = precipitationMin,
                    onValueChange = { precipitationMin = it },
                    label = { Text("Min Precipitation") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = precipitationMax,
                    onValueChange = { precipitationMax = it },
                    label = { Text("Max Precipitation") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(weatherTabState.generatedData) { data ->
                DataRow(
                    name = data.name,
                    data = mapOf(
                        "Temperature" to data.temperature.toString(),
                        "Wind Speed" to data.windSpeed.toString(),
                        "Wind Gusts" to data.windGusts.toString(),
                        "Precipitation" to data.precipitation.toString()
                    ),
                    onDelete = { weatherTabState.generatedData = weatherTabState.generatedData.toMutableList().apply {
                        remove(data)
                    }},
                    onEdit = {editingWeather = data}
                )
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(lazyListState)
        )
    }
    editingWeather?.let { weather ->
        EditFakeDataDialog(
            fakeData = weather,
            onDismiss = { editingWeather = null },
            onSave = { updatedWeather ->
                weatherTabState.generatedData = weatherTabState.generatedData.toMutableList().apply {
                    val index = indexOfFirst { it.name == weather.name }
                    if (index != -1) {
                        set(index, updatedWeather)
                    }
                }
                editingWeather = null
            }
        )
    }
}
@Composable
fun EditFakeDataDialog(
    fakeData: FakeData,
    onDismiss: () -> Unit,
    onSave: (FakeData) -> Unit
) {
    var newName by remember { mutableStateOf(fakeData.name) }
    var newTemperature by remember { mutableStateOf(fakeData.temperature.toString()) }
    var newWindSpeed by remember { mutableStateOf(fakeData.windSpeed.toString()) }
    var newWindGusts by remember { mutableStateOf(fakeData.windGusts.toString()) }
    var newPrecipitation by remember { mutableStateOf(fakeData.precipitation.toString()) }

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
                TextField(
                    value = newTemperature,
                    onValueChange = { newTemperature = it },
                    label = { Text("Temperature") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newWindSpeed,
                    onValueChange = { newWindSpeed = it },
                    label = { Text("Wind Speed") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newWindGusts,
                    onValueChange = { newWindGusts = it },
                    label = { Text("Wind Gusts") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newPrecipitation,
                    onValueChange = { newPrecipitation = it },
                    label = { Text("Precipitation") }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(FakeData(newName, newTemperature.toInt(), newWindSpeed.toInt(), newWindGusts.toInt(), newPrecipitation.toInt()))
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
fun AirQualityGeneratorTab(
    airQualityTabState: AirQualityTabState,
    modifier: Modifier = Modifier
) {
    val dataGenerator = remember { DataGenerator() }
    var numberOfRecords by remember { mutableStateOf("10") }
    var pm10Min by remember { mutableStateOf("0") }
    var pm10Max by remember { mutableStateOf("100") }
    var pm25Min by remember { mutableStateOf("0") }
    var pm25Max by remember { mutableStateOf("100") }
    var so2Min by remember { mutableStateOf("0") }
    var so2Max by remember { mutableStateOf("100") }
    var coMin by remember { mutableStateOf("0") }
    var coMax by remember { mutableStateOf("100") }
    var ozonMin by remember { mutableStateOf("0") }
    var ozonMax by remember { mutableStateOf("100") }
    var no2Min by remember { mutableStateOf("0") }
    var no2Max by remember { mutableStateOf("100") }
    var benzenMin by remember { mutableStateOf("0") }
    var benzenMax by remember { mutableStateOf("100") }
    var editingAirQuality by remember { mutableStateOf<FakeData?>(null) }

    val lazyListState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Button(
                    onClick = {
                        val numRecords = numberOfRecords.toIntOrNull() ?: 0
                        val minPM10 = pm10Min.toIntOrNull() ?: 0
                        val maxPM10 = pm10Max.toIntOrNull() ?: 0
                        val minPM25 = pm25Min.toIntOrNull() ?: 0
                        val maxPM25 = pm25Max.toIntOrNull() ?: 0
                        val minSO2 = so2Min.toIntOrNull() ?: 0
                        val maxSO2 = so2Max.toIntOrNull() ?: 0
                        val minCO = coMin.toIntOrNull() ?: 0
                        val maxCO = coMax.toIntOrNull() ?: 0
                        val minOzon = ozonMin.toIntOrNull() ?: 0
                        val maxOzon = ozonMax.toIntOrNull() ?: 0
                        val minNO2 = no2Min.toIntOrNull() ?: 0
                        val maxNO2 = no2Max.toIntOrNull() ?: 0
                        val minBenzen = benzenMin.toIntOrNull() ?: 0
                        val maxBenzen = benzenMax.toIntOrNull() ?: 0
                        if (numRecords > 0 && minPM10 <= maxPM10 && minPM25 <= maxPM25 && minSO2 <= maxSO2 && minCO <= maxCO && minOzon <= maxOzon && minNO2 <= maxNO2 && minBenzen <= maxBenzen) {
                            airQualityTabState.generatedData = dataGenerator.generateFakeAirQualityData(
                                numRecords,
                                minPM10..maxPM10,
                                minPM25..maxPM25,
                                minSO2..maxSO2,
                                minCO..maxCO,
                                minOzon..maxOzon,
                                minNO2..maxNO2,
                                minBenzen..maxBenzen
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = barColor)
                ) {
                    Text("Generate Air Quality Data")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                TextField(
                    value = numberOfRecords,
                    onValueChange = { numberOfRecords = it },
                    label = { Text("Number of Records") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = pm10Min,
                    onValueChange = { pm10Min = it },
                    label = { Text("Min PM10") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = pm10Max,
                    onValueChange = { pm10Max = it },
                    label = { Text("Max PM10") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = pm25Min,
                    onValueChange = { pm25Min = it },
                    label = { Text("Min PM2.5") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = pm25Max,
                    onValueChange = { pm25Max = it },
                    label = { Text("Max PM2.5") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = so2Min,
                    onValueChange = { so2Min = it },
                    label = { Text("Min SO2") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = so2Max,
                    onValueChange = { so2Max = it },
                    label = { Text("Max SO2") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = coMin,
                    onValueChange = { coMin = it },
                    label = { Text("Min CO") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = coMax,
                    onValueChange = { coMax = it },
                    label = { Text("Max CO") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = ozonMin,
                    onValueChange = { ozonMin = it },
                    label = { Text("Min Ozon") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = ozonMax,
                    onValueChange = { ozonMax = it },
                    label = { Text("Max Ozon") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = no2Min,
                    onValueChange = { no2Min = it },
                    label = { Text("Min NO2") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = no2Max,
                    onValueChange = { no2Max = it },
                    label = { Text("Max NO2") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = benzenMin,
                    onValueChange = { benzenMin = it },
                    label = { Text("Min Benzen") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = benzenMax,
                    onValueChange = { benzenMax = it },
                    label = { Text("Max Benzen") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = offWhiteColor,
                        focusedIndicatorColor = selectedColor,
                        focusedLabelColor = selectedColor,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(airQualityTabState.generatedData) { data ->
                DataRow(
                    name = data.name,
                    data = mapOf(
                        "PM10" to data.pm10.toString(),
                        "PM2.5" to data.pm25.toString(),
                        "SO2" to data.so2.toString(),
                        "CO" to data.co.toString(),
                        "Ozon" to data.ozon.toString(),
                        "NO2" to data.no2.toString(),
                        "Benzen" to data.benzen.toString()
                    ),
                    onDelete = {
                        airQualityTabState.generatedData = airQualityTabState.generatedData.toMutableList().apply {
                            remove(data)
                        }
                    },
                    onEdit = {
                        editingAirQuality = data
                    }
                )
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(lazyListState)
        )
    }

    editingAirQuality?.let { airQuality ->
        EditAirQualityDialog(
            fakeData = airQuality,
            onDismiss = { editingAirQuality = null },
            onSave = { updatedAirQuality ->
                airQualityTabState.generatedData = airQualityTabState.generatedData.toMutableList().apply {
                    val index = indexOfFirst { it.name == airQuality.name }
                    if (index != -1) {
                        set(index, updatedAirQuality)
                    }
                }
                editingAirQuality = null
            }
        )
    }
}

@Composable
fun EditAirQualityDialog(
    fakeData: FakeData,
    onDismiss: () -> Unit,
    onSave: (FakeData) -> Unit
) {
    var newName by remember { mutableStateOf(fakeData.name) }
    var newPM10 by remember { mutableStateOf(fakeData.pm10.toString()) }
    var newPM25 by remember { mutableStateOf(fakeData.pm25.toString()) }
    var newSO2 by remember { mutableStateOf(fakeData.so2.toString()) }
    var newCO by remember { mutableStateOf(fakeData.co.toString()) }
    var newOzon by remember { mutableStateOf(fakeData.ozon.toString()) }
    var newNO2 by remember { mutableStateOf(fakeData.no2.toString()) }
    var newBenzen by remember { mutableStateOf(fakeData.benzen.toString()) }

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
                TextField(
                    value = newPM10,
                    onValueChange = { newPM10 = it },
                    label = { Text("PM10") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newPM25,
                    onValueChange = { newPM25 = it },
                    label = { Text("PM2.5") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newSO2,
                    onValueChange = { newSO2 = it },
                    label = { Text("SO2") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newCO,
                    onValueChange = { newCO = it },
                    label = { Text("CO") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newOzon,
                    onValueChange = { newOzon = it },
                    label = { Text("Ozon") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newNO2,
                    onValueChange = { newNO2 = it },
                    label = { Text("NO2") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newBenzen,
                    onValueChange = { newBenzen = it },
                    label = { Text("Benzen") }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(FakeData(newName, newPM10.toInt(), newPM25.toInt(), newSO2.toInt(), newCO.toInt(), newOzon.toInt(), newNO2.toInt(), newBenzen.toInt()))
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

class WeatherTabState {
    var generatedData by mutableStateOf<List<FakeData>>(emptyList())
}

class AirQualityTabState {
    var generatedData by mutableStateOf<List<FakeData>>(emptyList())
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
        MenuState.GENERATOR -> GeneratorTab(
            weatherTabState = WeatherTabState(),
            airQualityTabState = AirQualityTabState(),
            modifier = modifier
        )

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
