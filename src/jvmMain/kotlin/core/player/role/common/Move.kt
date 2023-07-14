package core.player.role.common

interface Move<T : Role<T>> {
    operator fun invoke(src: Pair<Float, Float>, dest: Pair<Float, Float>) {
        // Implementation details
    }
}