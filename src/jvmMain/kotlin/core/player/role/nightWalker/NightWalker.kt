package core.player.role.nightWalker

import core.player.role.common.Adjust
import core.player.role.common.Move
import core.player.role.common.Role
import core.player.role.common.Skill

class NightWalker(override var move: Move<NightWalker>, override var adjust: Adjust<NightWalker>, override var skills: List<Skill<NightWalker>>) : Role<NightWalker> {

    val test = MoveImpl<NightWalker>()
}