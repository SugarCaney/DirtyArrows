package nl.sugcube.dirtyarrows.util

import org.bukkit.util.Vector

/**
 * Iterates over points on the line from the start location in the direction of the given vector in certain
 * increments.
 *
 * @param this
 *          Where to start the iteration.
 * @param direction
 *          In what direction to travel (unit length).
 * @param step
 *          In what length increments to iterate.
 * @param maxDistance
 *          The maximum distance that can be traveled.
 * @return A sequence iterating over the given line.
 */
fun Vector.lineIteration(direction: Vector, step: Double, maxDistance: Double): Sequence<Vector> {
    val checkLocation = copyOf()
    return generateSequence(checkLocation) {
        val newLocation = checkLocation.add(direction.copyOf().multiply(step))
        if (newLocation.distance(this) <= maxDistance) {
            newLocation
        }
        else null
    }
}

/**
 * Iterates over a line from this location to `to` with a given step size.
 *
 * @param this
 *          The starting location.
 * @param to
 *          The ending location.
 * @param step
 *          The distance between each iteration step.
 * @return A sequence iterating over the line segment defined by the given locations.
 */
fun Vector.lineIteration(to: Vector, step: Double) = this.lineIteration(
        direction = to.copyOf().subtract(this).normalize(),
        step = step,
        maxDistance = to.distance(this)
)