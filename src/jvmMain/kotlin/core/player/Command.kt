package core.player

import core.Config
import core.player.role.common.Skill
import java.lang.Thread.sleep


// Executing a skill with a delay
data class Command(val skill: Skill<*>, val delay: Long = 0, val rep: Int = 1, val direction: String? = null) {

    fun execute() {

        for (i in 1..rep) {
            Config.keyMap[direction]?.let { Config.driver.userKeyDown(it) }
            sleep(delay)
            skill.execute()
            Config.keyMap[direction]?.let { Config.driver.userKeyUp(it) }
        }
    }
}