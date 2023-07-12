package core.player

import java.awt.Point

data class Player(
    var position: Point,
    var state: State,
    var currentChannel: Int = 1,
    var commands: MutableList<Command> = mutableListOf(),
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

}