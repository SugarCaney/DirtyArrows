@file:JvmName("Locations")

package nl.sugcube.dirtyarrows.util

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

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

/**
 * Gets the northern, eastern, southern, and western block, in order.
 */
fun Location.northEastSouthWest() = listOf(north(), east(), south(), west())

/**
 * Gets all 5 blocks in a cross form in the horizontal plane.
 */
fun Location.crossBlocks() = listOf(this) + northEastSouthWest()

/**
 * Changes the location to a random location with at most `maxFuzz`.
 */
fun Location.fuzz(maxFuzz: Double) = copyOf(
        x = x + Random.nextDouble() * 2 * maxFuzz - maxFuzz,
        y = y + Random.nextDouble() * 2 * maxFuzz - maxFuzz,
        z = z + Random.nextDouble() * 2 * maxFuzz - maxFuzz,
)

/**
 * Gets all entities that are within a certain range of this location.
 */
fun Location.nearbyEntities(range: Double): Collection<Entity> = world.getNearbyEntities(this, range, range, range)

/**
 * Get all living entities within a certain range of this location.
 */
fun Location.nearbyLivingEntities(range: Double) = nearbyEntities(range).mapNotNull { it as? LivingEntity }

/**
 * Calculates the distance from this location (x,y,z) to the vectors (x,y,z).
 */
fun Location.distanceToVector(vector: Vector) = sqrt(
        (x - vector.x) * (x - vector.x) + (y - vector.y) * (y - vector.y) + (z - vector.z) * (z - vector.z)
)