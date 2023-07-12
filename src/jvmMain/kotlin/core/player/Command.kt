package core.player


// Executing a skill with a delay
data class Command(val skill: Skill, val delay: Int = 0)