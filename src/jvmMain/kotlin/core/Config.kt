package core

import Driver
import java.awt.Point


object Config {


    const val RESOURCES_DIR = "resources"

    var enabled = false

    var stageFright = false

    var mapChanging = false

    val skillCdTimer = mutableMapOf<String, Any>()
    val skillMaintainedCount = mutableMapOf<String, Any>()
    val isSkillReadyCollector = mutableMapOf<String, Any>()
    val skillBuffTimer = mutableMapOf<String, Any>()

    var path = mutableListOf<Point>()

    var moveThreshold = 0
    var adjustThread = 0

    var driver = Driver()
    var monitor: Monitor? = null
    val keyMap =  mapOf(
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

    val ddKeyMap = mapOf(
        0x25 to 710,
        0x26 to 709,
        0x27 to 712,
        0x28 to 711,
        0x08 to 214,
        0x09 to 300,
        0x0D to 313,
        0x10 to 500,
        0x11 to 600,
        0x12 to 602,
        0x14 to 400,
        0x1B to 100,
        0x20 to 603,
//        0x21 to "pageup",
//        0x22 to "pagedown",
//        0x23 to "end",
//        0x24 to "home",
//        0x2D to "insert",
//        0x2E to "delete",
        0x30 to 210,
        0x31 to 201,
        0x32 to 202,
        0x33 to 203,
        0x34 to 204,
        0x35 to 205,
        0x36 to 206,
        0x37 to 207,
        0x38 to 208,
        0x39 to 209,
        0x41 to 401,
        0x42 to 505,
        0x43 to 503,
        0x44 to 403,
        0x45 to 303,
        0x46 to 404,
        0x47 to 405,
        0x48 to 406,
        0x49 to 308,
        0x4A to 407,
        0x4B to 408,
        0x4C to 409,
        0x4D to 507,
        0x4E to 506,
        0x4F to 309,
        0x50 to 310,
        0x51 to 301,
        0x52 to 304,
        0x53 to 402,
        0x54 to 305,
        0x55 to 307,
        0x56 to 504,
        0x57 to 302,
        0x58 to 502,
        0x59 to 306,
        0x5A to 501,
        0x70 to 101,
        0x71 to 102,
        0x72 to 103,
        0x73 to 104,
        0x74 to 105,
        0x75 to 106,
        0x76 to 107,
        0x77 to 108,
        0x78 to 109,
        0x79 to 110,
        0x7A to 111,
        0x7B to 112,
//        0x90 to "num lock",
//        0x91 to "scroll lock",
//        0xBA to ";",
//        0xBB to "=",
//        0xBC to ",",
//        0xBD to "-",
//        0xBE to ".",
//        0xBF to "/",
//        0xC0 to "`",
//        0xDB to "[",
//        0xDC to "\\",
//        0xDD to "]",
//        0xDE to "'"
    )


    var routine: Any? = null
    var layout: Any? = null
    var bot: Any? = null
    var capture: Any? = null
    var listener: Any? = null
    var gui: Any? = null

    var latestChangeChannelOrMap: Long = 0
    var latestSolvedRune: Long = 0
    var shouldChangeChannel = false
    var shouldSolveRune = false

    var myRemoteInfo = mutableListOf<Any>()
    var remoteInfos = mutableMapOf<String, Any>()

}