import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState

val barColor = Color(0xFF08DCC4)
enum class MenuState { INVOICES, ABOUT_APP }

@Composable
fun Menu(menuState: MutableState<MenuState>, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(barColor)
    ) {
        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .clickable { menuState.value = MenuState.INVOICES },
            contentAlignment = Alignment.Center
        ) {
            InvoicesMenuItem(menuState, modifier)
        }

        Box(
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .clickable { menuState.value = MenuState.ABOUT_APP },
            contentAlignment = Alignment.Center
        ) {
            AboutAppMenuItem(menuState, modifier)
        }
    }
}

@Composable
fun InvoicesMenuItem(menuState: MutableState<MenuState>, modifier: Modifier) {
    val invoiceIcon: Painter = painterResource("images/menu_icon.png")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = invoiceIcon,
            contentDescription = "Invoices",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Invoices")
    }
}

@Composable
fun AboutAppMenuItem(menuState: MutableState<MenuState>, modifier: Modifier) {
    val aboutAppIcon: Painter = painterResource("images/about_app_icon.png")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = aboutAppIcon,
            contentDescription = "About App",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "About App")
    }
}
@Composable
fun InvoicesTab(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("INVOICES CONTENT")
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
        Text("Author: Matej Moravec")
    }
}

@Composable
fun Content(menuState: MutableState<MenuState>, modifier: Modifier = Modifier) {
    when (menuState.value) {
        MenuState.INVOICES -> InvoicesTab(modifier = modifier)
        MenuState.ABOUT_APP -> AboutAppTab(modifier = modifier)
    }
}

@Composable
fun Footer(menuState: MutableState<MenuState>, modifier: Modifier = Modifier) {
    val text = when (menuState.value) {
        MenuState.INVOICES -> "You're viewing the \"Invoices\" tab."
        MenuState.ABOUT_APP -> "You're viewing the \"About App\" tab."
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(25.dp)
            .background(barColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = text)
    }
}

@Composable
@Preview
fun App() {
    val menuState = remember { mutableStateOf(MenuState.ABOUT_APP) }

    Column {
        Menu(menuState)
        Content(menuState = menuState, modifier = Modifier.weight(1f))
        Footer(menuState)
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Vodjenje racunov",
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(900.dp, 800.dp)
        ),
        undecorated = false,
        resizable = true
    ) {
        App()
    }
}
