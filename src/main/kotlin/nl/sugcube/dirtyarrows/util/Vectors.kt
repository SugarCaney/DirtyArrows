package nl.sugcube.dirtyarrows.util

import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Rotate this vector along the Y axis.
 */
fun Vector.rotateAlongYAxis(angleRadians: Double) = copyOf(
        x = x * cos(angleRadians) + z * sin(angleRadians),
        y = y,
        z = -x * sin(angleRadians) + z * cos(angleRadians)
)

/**
 * Changes the direction of the vector randomly with at most `maxFuzz`.
 */
fun Vector.fuzz(maxFuzz: Double) = copyOf(
        x = x + Random.nextDouble() * 2 * maxFuzz - maxFuzz,
        y = y + Random.nextDouble() * 2 * maxFuzz - maxFuzz,
        z = z + Random.nextDouble() * 2 * maxFuzz - maxFuzz,
)

/**
 * Rotates this vector around the given normal vector.
 *
 * @param this
 *          The vector to rotate.
 * @param normal
 *          The normal defining the plane to rotate over.
 * @param angle
 *          The angle to rotate.
 */
fun Vector.rotateAround(normal: Vector, angle: Double): Vector {
    return copyOf().multiply(cos(angle))
            .add(normal.copyOf().crossProduct(this).multiply(sin(angle)))
            .add(normal.copyOf().multiply(normal.copyOf().dot(this)).multiply(1.0 - cos(angle)))
}