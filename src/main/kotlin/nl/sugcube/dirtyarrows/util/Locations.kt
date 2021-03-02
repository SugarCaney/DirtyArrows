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

/**
 * Get all block locations that are 'attached' to this block (east, west, up, down, south, north).
 */
fun Location.attachedBlockLocations() = listOf(
        copyOf(x = x + 1.0),
        copyOf(x = x - 1.0),
        copyOf(y = y + 1.0),
        copyOf(y = y - 1.0),
        copyOf(z = z + 1.0),
        copyOf(z = z - 1.0),
)

/**
 * Get the location 1 block north.
 */
fun Location.north() = copyOf(z = z - 1.0)

/**
 * Get the location 1 block south.
 */
fun Location.south() = copyOf(z = z + 1.0)

/**
 * Get the location 1 block west.
 */
fun Location.west() = copyOf(x = x - 1.0)

/**
 * Get the location 1 block east.
 */
fun Location.east() = copyOf(x = x + 1.0)
