package core.player

import java.awt.Point
import java.lang.Thread.sleep
import kotlin.math.abs

data class Player(
    var position: Point,
    var state: State,
    var currentChannel: Int = 1,

) {
    companion object {
        const val JUMP_BUTTON = "alt"
        const val MOVEMENT_STATE_STANDING = 0
        const val MOVEMENT_STATE_JUMPING = 1
        const val MOVEMENT_STATE_FALLING = 2
    }

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
        return abs(this.position.x - curX) <= xRange
    }

    fun checkIsInYRange(curY: Float, yRange: Float): Boolean {
        return abs(this.position.y - curY) <= yRange
    }

    fun moveToClosestPoint(points: List<Point>) {
        val closestPoint = points.minByOrNull { this.position.distance(it) } ?: return
        this.moveTo(closestPoint)
    }
}
