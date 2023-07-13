package core.kit

import core.player.Player

class Point(
    private var x: Float,
    private var y: Float,
    frequency: Int = 1,
    skip: Boolean = false,
    adjust: Boolean = false,
    private var activationWithinXRange: Float? = null,
    private var activationWithinYRange: Float? = null,
    private var activationNotWithinXRange: Float? = null,
    private var activationNotWithinYRange: Float? = null,
    override val activationOnSkillReady: String?,
    override val activationOnSkillCd: String?,
    override val activationOnSkillBuff: String?,
    override val activationOnNotInSkillBuff: String?,
    override val waitUntilReady: Boolean?
) : Component {

    init {
        val location = Pair(x, y)
        val frequency = if (frequency <= 0) throw Exception("frequency must be positive") else frequency
        val counter = if (skip) 1 else 0
        val adjust = adjust
        var isConditionalPoint = false
        if (activationWithinXRange != null) {
            isConditionalPoint = true
        }
        if (activationWithinYRange != null) {
            isConditionalPoint = true
        }
        if (activationNotWithinXRange != null) {
            isConditionalPoint = true
        }
        if (activationNotWithinYRange != null) {
            isConditionalPoint = true
        }
        val commands = ArrayList<String>()
    }

    override fun update(vararg args: Any) {
        TODO("Not yet implemented")
    }

    fun isWithInActivationRange(player: Player): Boolean {
        this.activationWithinXRange?.let {
            if (!player.checkIsInXRange(this.x, it)) return false
        }
        this.activationNotWithinXRange?.let {
            if (player.checkIsInXRange(this.x, it)) return false
        }
        this.activationWithinYRange?.let {
            if (!player.checkIsInYRange(this.y, it)) return false
        }
        this.activationNotWithinYRange?.let {
            if (player.checkIsInYRange(this.y, it)) return false
        }

        return true
    }
}
