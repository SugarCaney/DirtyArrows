@file:JvmName("Locations")

package nl.sugcube.dirtyarrows.util

import org.bukkit.Location
import kotlin.math.abs

/**
 * Checks if the given location is within euclidean distance `margin` of this location.
 */
fun Location.isCloseTo(other: Location, margin: Double = 1.0): Boolean {
    if (abs(x - other.x) > margin) return false
    if (abs(y - other.y) > margin) return false
    return abs(z - other.z) <= margin
}