package nl.sugcube.dirtyarrows.util

import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

/**
 * Rotate this vector along the Y axis.
 */
fun Vector.rotateAlongYAxis(angleRadians: Double) = copyOf(
        x = x * cos(angleRadians) + z * sin(angleRadians),
        y = y,
        z = -x * sin(angleRadians) + z * cos(angleRadians)
)