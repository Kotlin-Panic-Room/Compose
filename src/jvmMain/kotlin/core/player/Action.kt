package core.player

import Driver
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.random.Random

sealed class Action(val name: String, var executedTime: ZonedDateTime?, val coolDown: Duration, val key: String) {
    val KEY_MAP = mapOf(
        "left" to 0x25, // Arrow keys
        "up" to 0x26,
        "right" to 0x27,
        "down" to 0x28,

        "backspace" to 0x08, // Special keys
        "tab" to 0x09,
        "enter" to 0x0D,
        "shift" to 0x10,
        "ctrl" to 0x11,
        "alt" to 0x12,
        "caps lock" to 0x14,
        "esc" to 0x1B,
        "space" to 0x20,
        "pageup" to 0x21,
        "pagedown" to 0x22,
        "end" to 0x23,
        "home" to 0x24,
        "insert" to 0x2D,
        "delete" to 0x2E,

        "0" to 0x30, // Numbers
        "1" to 0x31,
        "2" to 0x32,
        "3" to 0x33,
        "4" to 0x34,
        "5" to 0x35,
        "6" to 0x36,
        "7" to 0x37,
        "8" to 0x38,
        "9" to 0x39,

        "a" to 0x41, // Letters
        "b" to 0x42,
        "c" to 0x43,
        "d" to 0x44,
        "e" to 0x45,
        "f" to 0x46,
        "g" to 0x47,
        "h" to 0x48,
        "i" to 0x49,
        "j" to 0x4A,
        "k" to 0x4B,
        "l" to 0x4C,
        "m" to 0x4D,
        "n" to 0x4E,
        "o" to 0x4F,
        "p" to 0x50,
        "q" to 0x51,
        "r" to 0x52,
        "s" to 0x53,
        "t" to 0x54,
        "u" to 0x55,
        "v" to 0x56,
        "w" to 0x57,
        "x" to 0x58,
        "y" to 0x59,
        "z" to 0x5A,

        "f1" to 0x70, // Functional keys
        "f2" to 0x71,
        "f3" to 0x72,
        "f4" to 0x73,
        "f5" to 0x74,
        "f6" to 0x75,
        "f7" to 0x76,
        "f8" to 0x77,
        "f9" to 0x78,
        "f10" to 0x79,
        "f11" to 0x7A,
        "f12" to 0x7B,
        "num lock" to 0x90,
        "scroll lock" to 0x91,

        ";" to 0xBA, // Special characters
        "=" to 0xBB,
        "," to 0xBC,
        "-" to 0xBD,
        "." to 0xBE,
        "/" to 0xBF,
        "`" to 0xC0,
        "[" to 0xDB,
        "\\" to 0xDC,
        "]" to 0xDD,
        "'" to 0xDE,
    )

    private val driver = Driver()
    private val downTime = 500
    private val randomValue = Random.nextDouble()
    private val randomBetween0And1 = randomValue.coerceIn(0.0, 1.0)

    open fun isReady(): Boolean {
        return executedTime?.let {
            it.plus(coolDown) < ZonedDateTime.now()
        } ?: true
    }

    fun execute() {
        KEY_MAP[key]?.let { driver.userKeyDown(it) }
        Thread.sleep((downTime * (0.8 + 0.7 * randomBetween0And1)).toLong())
    }

    class BuffAction(
        key: String,
        name: String,
        executedTime: ZonedDateTime,
        coolDown: Duration,
        private var buffDuration: Duration,
    ) :
        Action(name, executedTime, coolDown, key) {
        override fun isReady(): Boolean {
            executedTime?.let {
                return it.plus(coolDown) < ZonedDateTime.now() && it.plus(buffDuration) < ZonedDateTime.now()
            } ?: run {
                return true
            }
        }
    }

    class AttackAction(
        key: String,
        name: String,
        executedTime: ZonedDateTime?,
        coolDown: Duration,
        var delay: Duration,
        var needGrounded: Boolean,
        var direct: String,
        var rep: Long,
        var repInterval: Float,
    ) :
        Action(name, executedTime, coolDown, key)

    class MoveAction(key: String, name: String, executedTime: ZonedDateTime?, coolDown: Duration = Duration.ZERO) :
        Action(name, executedTime, coolDown, key)
}
