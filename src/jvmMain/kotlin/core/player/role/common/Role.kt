package core.player.role.common

interface Role<T: Role<T>> {
    var move: Move<T>
    var adjust: Adjust<T>
    var skills: List<Skill<T>>
}