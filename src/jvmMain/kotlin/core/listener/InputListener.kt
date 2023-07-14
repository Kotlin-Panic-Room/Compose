package core.listener

import core.Config
import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import org.jnativehook.mouse.NativeMouseEvent
import org.jnativehook.mouse.NativeMouseListener
import java.awt.Toolkit
import kotlin.system.exitProcess

class InputListener {
    private val onOffSwitchKey = "F6"
    private val reloadRoutineKey = "F7"
    private val recordPositionKey = "F8"

    init {
        try {
            GlobalScreen.registerNativeHook()
        } catch (e: Exception) {
            System.err.println("There was a problem registering the native hook.")
            System.err.println(e.message)
            exitProcess(1)
        }
        GlobalScreen.addNativeMouseListener(MouseHooker())
        GlobalScreen.addNativeKeyListener(KeyHooker())
    }

    inner class KeyHooker : NativeKeyListener {
        override fun nativeKeyPressed(e: NativeKeyEvent) {
            when (NativeKeyEvent.getKeyText(e.keyCode)) {
                onOffSwitchKey -> {
                    // turn on Config.enabled flag
                    Config.enabled = !Config.enabled
                    Toolkit.getDefaultToolkit().beep()
                }

                reloadRoutineKey -> {
                    //
                }

                recordPositionKey -> {

                }
            }
        }

        override fun nativeKeyReleased(e: NativeKeyEvent) {
            println("Key Released: " + NativeKeyEvent.getKeyText(e.keyCode))
        }

        override fun nativeKeyTyped(e: NativeKeyEvent) {
            println("Key Typed: " + NativeKeyEvent.getKeyText(e.keyCode))
        }
    }

    inner class MouseHooker : NativeMouseListener {
        override fun nativeMouseClicked(e: NativeMouseEvent) {
//            println("Mouse Clicked: " + e.point)
        }

        override fun nativeMousePressed(e: NativeMouseEvent) {
//            println("Mouse Pressed: " + e.point)
        }

        override fun nativeMouseReleased(e: NativeMouseEvent) {
//            println("Mouse Released: " + e.point)
        }
    }
}
