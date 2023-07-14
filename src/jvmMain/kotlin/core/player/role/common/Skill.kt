package core.player.role.common

import core.Config
import core.Config.keyMap
import java.time.ZonedDateTime
import kotlin.random.Random

open class Skill <T: Role<T>> {

    open val name: String = ""
    open var executedTime: ZonedDateTime? = null
    open val coolDown: Long = 0
    open val key: String? = null
    open val downTime = 500
    val needGround = false

    private val driver = Config.driver
    private val randomValue = Random.nextDouble()
    private val randomBetween0And1 = randomValue.coerceIn(0.0, 1.0)

    operator fun invoke() {
        if (isReady()) {
            execute()
            executedTime = ZonedDateTime.now()
        }
    }

    open fun isReady(): Boolean {
        return executedTime?.let {
            it.plusSeconds(coolDown) < ZonedDateTime.now()
        } ?: true
    }

    fun execute() {
        keyMap[key]?.let { driver.userKeyDown(it) }
        Thread.sleep((downTime * (0.8 + 0.7 * randomBetween0And1)).toLong())
        keyMap[key]?.let { driver.userKeyUp(it) }
    }
}