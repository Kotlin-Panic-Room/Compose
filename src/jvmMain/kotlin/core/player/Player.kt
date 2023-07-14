package core.player

import core.kit.Point
import core.player.role.common.Role
import java.lang.Thread.sleep
import kotlin.math.abs

object Player {

    var position: Pair<Float, Float> = Pair(0f, 0f)
    var role: Role<*>? = null
    var state: State = State.STANDING
    var currentChannel: Int = 1
    var routine: Routine = Routine(listOf())



    fun waitForWhileStanding(ms: Int = 2000): Boolean {
        for (i in 0 until ms / 10) {
            if (this.state == State.STANDING) {
                return true
            }
            Thread.sleep(10)
        }
        return false
    }

    fun waitForWhileJumping(ms: Int = 2000): Boolean {
        for (i in 0 until ms / 10) {
            if (this.state == State.JUMPING) {
                return true
            }
            Thread.sleep(10)
        }
        return false
    }

    fun waitForWhileFalling(ms: Int = 2000): Boolean {
        for (i in 0 until ms / 10) {
            if (this.state == State.FALLING) {
                return true
            }
            Thread.sleep(10)
        }
        return false
    }

    fun checkIsJumping(): Boolean {
        return this.state == State.JUMPING
    }

    fun recordPosition() {
        sleep(600)
    }

    fun checkIsInXRange(curX: Float, xRange: Float): Boolean {
        return abs(this.position.first - curX) <= xRange
    }

    fun checkIsInYRange(curY: Float, yRange: Float): Boolean {
        return abs(this.position.second - curY) <= yRange
    }

    fun moveToPoint(point: Point) {
        if (!point.isWithInActivationRange(this)) return

        point.execute()
    }
}
