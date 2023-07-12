import java.lang.Thread.sleep
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

    private val driver = System.loadLibrary("drivers/dd2023.x64.dll")

    init {
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
                sleep(2000)
            }
        }
    }

    fun userKeyDown(key: Int) {
        keyDownList.add(key)
    }

    fun userKeyUp(key: Int) {
        keyUpList.add(key)
    }

    private fun keyUp(key: Int) {
        DD_Key(key, 2)
    }

    private fun keyDown(key: Int) {
       DD_Key(key, 1)
    }

    external fun DD_Key(key: Int, flag: Int): Int

    // Implement rest of your functions here ...
}