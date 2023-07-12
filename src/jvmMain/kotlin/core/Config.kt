package core

import java.awt.Point

class Config {

    companion object {
        const val RESOURCES_DIR = "resources"

        var enabled = false

        var stageFright = false

        var mapChanging = false

        val skillCdTimer = mutableMapOf<String, Any>()
        val skillMaintainedCount = mutableMapOf<String, Any>()
        val isSkillReadyCollector = mutableMapOf<String, Any>()
        val skillBuffTimer = mutableMapOf<String, Any>()

        var path = mutableListOf<Point>()



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
}