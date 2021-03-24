package nl.sugcube.dirtyarrows.util

import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import kotlin.math.sqrt

/**
 * The diameter of the entity based on width and height.
 */
val Entity.diameter: Double
    get() = sqrt(width * width + height * height)

/**
 * Respawns an arrow in the same direction as this arrow.
 *
 * @return The spawned arrow.
 */
fun Arrow.respawnArrow(): Arrow {
    val landedArrow = this
    landedArrow.remove()
    return landedArrow.world.spawnEntity(landedArrow.location, EntityType.ARROW).apply {
        val createdArrow = this as Arrow
        createdArrow.velocity = landedArrow.velocity
        createdArrow.isCritical = landedArrow.isCritical
        createdArrow.pickupStatus = landedArrow.pickupStatus
        createdArrow.knockbackStrength = landedArrow.knockbackStrength
        createdArrow.fireTicks = landedArrow.fireTicks
        createdArrow.shooter = this@respawnArrow.shooter
    } as Arrow
}