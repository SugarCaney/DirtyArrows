package nl.sugcube.dirtyarrows.effect

import nl.sugcube.dirtyarrows.Broadcast

/**
 * @author SugarCaney
 */
enum class HeadshotType(
    val message: String,
    val minigameMessage: String
) {

    /**
     * Headshot by a player.
     */
    BY(Broadcast.HEADSHOT_BY, Broadcast.MINIGAME_HEADSHOT_BY),

    /**
     * Headshot on a player.
     */
    ON(Broadcast.HEADSHOT_ON, Broadcast.MINIGAME_HEADSHOT_ON)
}