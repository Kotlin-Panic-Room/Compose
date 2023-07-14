
import com.sun.jna.Library
import com.sun.jna.Native
import core.Config.ddKeyMap
import java.lang.Thread.sleep
import java.nio.file.Paths
import kotlin.concurrent.thread

class Driver {
    val FLIPPED_KEY_MAP = mapOf(
        0x25 to 710,
        0x26 to 709,
        0x27 to 712,
        0x28 to 711,
        // Add rest of your key mappings here ...
    )

    private val keyUpList = mutableListOf<Int>()
    private val keyDownList = mutableListOf<Int>()
    private val keyDownObjs = mutableMapOf<Int, Pair<Long, Int>>()
    private val keyUpObjs = mutableMapOf<Int, Long>()



    init {
        DD.INSTANCE.DD_btn(0);//to add a line
        thread(start = true) {
            while (true) {
                if (keyUpList.isNotEmpty()) {
                    val newKey = keyUpList.removeAt(keyUpList.size - 1)
                    keyUpObjs[newKey] = System.currentTimeMillis()
                }

                if (keyDownList.isNotEmpty()) {
                    val newKey = keyDownList.removeAt(keyDownList.size - 1)
                    keyDownObjs[newKey] = Pair(System.currentTimeMillis(), 0)
                }

                keyUpObjs.keys.forEach { key ->
                    keyUp(key)
                    keyDownObjs.remove(key)
                }
                keyUpObjs.clear()

                keyDownObjs.keys.forEach { key ->
                    val (lastT, count) = keyDownObjs[key]!!
                    when (count) {
                        0 -> {
                            keyDown(key)
                            keyDownObjs[key] = Pair(System.currentTimeMillis(), count + 1)
                        }

                        1 -> {
                            if (System.currentTimeMillis() - lastT >= 250) {
                                keyDown(key)
                                keyDownObjs[key] = Pair(System.currentTimeMillis(), count + 1)
                            }
                        }

                        else -> {
                            if (System.currentTimeMillis() - lastT >= 30) {
                                keyDown(key)
                                keyDownObjs[key] = Pair(System.currentTimeMillis(), count + 1)
                            }
                        }
                    }
                }
                sleep(200)
            }
        }
    }

    fun userKeyDown(key: Int) {
        keyDownList.add(ddKeyMap[key]!!)
    }

    fun userKeyUp(key: Int) {
        keyUpList.add(ddKeyMap[key]!!)
    }

    private fun keyUp(key: Int) {
        DD.INSTANCE.DD_key(key, 2)
    }

    private fun keyDown(key: Int) {
        DD.INSTANCE.DD_key(key, 1)
    }

    interface DD : Library {
        companion object {
            val absolutePath = Paths.get("src/jvmMain/resources/drivers/dd2023.x64.dll").toAbsolutePath().toString()
            val INSTANCE: DD = Native.load(absolutePath, DD::class.java) as DD
        }

        fun DD_mov(x: Int, y: Int): Int // mouse move abs.
        fun DD_movR(dx: Int, dy: Int): Int // mouse move rel.
        fun DD_btn(btn: Int): Int // 1==L.down, 2==L.up, 4==R.down, 8==R.up, 16==M.down, 32==M.up
        fun DD_whl(whl: Int): Int // Mouse Wheel. 1==down, 2==up
        fun DD_key(ddcode: Int, flag: Int): Int // keyboard
        fun DD_str(s: String): Int // Input visible char
    }

    // Implement rest of your functions here ...
}
