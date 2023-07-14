
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import core.Config
import core.Monitor
import core.listener.InputListener
import org.jnativehook.GlobalScreen

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    InputListener()
    Config.monitor = Monitor()
    val logger = java.util.logging.Logger.getLogger(GlobalScreen::class.java.getPackage().name)
    logger.level = java.util.logging.Level.OFF
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
