package core.player.role.common

import core.Config
import core.Config.keyMap
import core.Utils
import core.player.Player
import kotlin.math.abs

interface Adjust<T: Role<T>> {

    operator fun invoke(dest: Pair<Float, Float>) {
        var counter = 15
        var toggle = true
        val driver = Config.driver
        val threshold = Config.adjustThread
        var dX = dest.first - Player.position.first
        var dY = dest.second - Player.position.second
        while (counter > 0 && (abs(dX) > threshold || abs(dY) > threshold)) {
            if (toggle) {
                dX = dest.first - Player.position.first
                if (abs(dX) > threshold) {
                    var walkCounter = 0
                    if (dX < 0) {
                        driver.userKeyDown(keyMap["left"]!!)
                        while ( dX < -1 * threshold && walkCounter < 60) {
                            Thread.sleep(Utils.randMs(100,200).toLong())
                            walkCounter += 1
                            dX = dest.first - Player.position.first
                        }
                        driver.userKeyUp(keyMap["left"]!!)
                    } else {
                        driver.userKeyDown(keyMap["right"]!!)
                        while ( dX > threshold && walkCounter < 60) {
                            Thread.sleep((Utils.randMs(100,200).toLong()))
                            walkCounter += 1
                            dX = dest.first - Player.position.first
                        }
                        driver.userKeyUp(keyMap["right"]!!)
                    }
                    counter -= 1
                }
            } else {
                dY = dest.second - Player.position.second
                if (abs(dY) > threshold) {
                    if (dY < 0) {
                        Player.waitForWhileStanding(1000)
                        // up jump
                    } else {
                        Player.waitForWhileStanding(1000)
                        driver.userKeyDown(keyMap["down"]!!)
                        Thread.sleep((Utils.randMs(50,70).toLong()))
                        driver.userKeyDown(keyMap["alt"]!!)
                        driver.userKeyUp(keyMap["down"]!!)
                        Thread.sleep((Utils.randMs(179,250).toLong()))
                    }
                    counter -= 1
                }
            }
            dX = dest.first - Player.position.first
            dY = dest.second - Player.position.second
            toggle = !toggle
        }
    }
}