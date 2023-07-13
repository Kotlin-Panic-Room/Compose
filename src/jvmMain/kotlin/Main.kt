import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import core.listener.KeyHooker
import core.listener.MouseHooker
import org.jnativehook.GlobalScreen
import kotlin.system.exitProcess


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
    try {
        GlobalScreen.registerNativeHook()
    } catch (e: Exception) {
        System.err.println("There was a problem registering the native hook.")
        System.err.println(e.message)
        exitProcess(1)
    }
    GlobalScreen.addNativeMouseListener(MouseHooker())
    GlobalScreen.addNativeKeyListener(KeyHooker())
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
