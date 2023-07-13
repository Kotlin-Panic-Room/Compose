package core.kit

import core.player.Action

interface Component {
    val activationOnSkillReady: String?
    val activationOnSkillCd: String?
    val activationOnSkillBuff: String?
    val activationOnNotInSkillBuff: String?
    val waitUntilReady: Boolean?

    fun update(vararg args: Any)

    fun isActivated(action: Action): Boolean {
        return action.isReady()
    }
}
