package core.player


// Executing a skill with a delay
data class Command(val action: Action, val delay: Int = 0)