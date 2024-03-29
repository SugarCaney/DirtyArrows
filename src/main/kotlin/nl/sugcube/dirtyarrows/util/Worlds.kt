package nl.sugcube.dirtyarrows.util

import org.bukkit.Location
import kotlin.math.floor

/**
 * @author SugarCaney
 */
object Worlds {

    /**
     * The maximum Y location where blocks can be placed (inclusive).
     */
    const val MAX_WORLD_Y = 319

    /**
     * Gravity in m/s^2.
     */
    const val GRAVITY = 1.0
}

/**
 * Get the lowest Y level greater than this Y level where entities can spawn.
 *
 * @return `null` when there is no eligible Y level.
 */
fun Location.firstSpawnEligibleY(): Int? {
    val thisY = floor(y).toInt()
    for (i in thisY..Worlds.MAX_WORLD_Y) {
        val block = world?.getBlockAt(x.toInt(), i, z.toInt())
        if (block?.type?.isOccluding == true) {
            return i
        }
    }

    return null
}