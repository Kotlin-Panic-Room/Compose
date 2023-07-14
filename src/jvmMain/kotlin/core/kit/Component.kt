package core.kit

import core.player.role.common.Skill

interface Component {
    val activationOnSkillReady: String?
    val activationOnSkillCd: String?
    val activationOnSkillBuff: String?
    val activationOnNotInSkillBuff: String?
    val waitUntilReady: Boolean?

    fun execute(obj: Any? = null)
    fun update(vararg args: Any)

    fun isActivated(skill: Skill<*>): Boolean {
        return skill.isReady()
    }
}
