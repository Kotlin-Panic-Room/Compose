package core

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import core.player.role.common.Skill
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random

object Utils {
    fun randMs(start: Int, end: Int): Int {
        val randomValue = Random.nextDouble()
        val randomBetween0And1 = randomValue.coerceIn(0.0, 1.0)
        require(start < end) { "START must be less than END" }
        return ((end - start) * randomBetween0And1 + start).toInt()
    }

    fun readSkillFiles(role: String): List<Skill<*>>{
        val mapper = jacksonObjectMapper()

        val directoryPath = Paths.get("src/jvmMain/kotlin/core/player/role/${role}/skill")
        val files = Files.list(directoryPath)
            .filter { Files.isRegularFile(it) && it.toString().endsWith(".json") }
            .toList()
        return files.map { filePath ->
            val json = String(Files.readAllBytes(filePath))
            mapper.readValue(json, Skill::class.java)
        }
    }
}