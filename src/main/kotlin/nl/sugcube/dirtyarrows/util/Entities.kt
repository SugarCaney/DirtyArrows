package nl.sugcube.dirtyarrows.util

import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.Location
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import kotlin.math.sqrt

/**
 * The diameter of the entity based on width and height.
 */
val Entity.diameter: Double
    get() = sqrt(width * width + height * height)

/**
 * The centre location of the entity.
 */
val Entity.centre: Location
    get() = location.clone().add(Vector(0.0, height / 2.0, 0.0))

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

/**
 * Schedules a task to delete the arrow entity from existence.
 */
fun Arrow.scheduleRemoval(plugin: DirtyArrows) = plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
    // For some reason this needs to be scheduled when you want to delete an array immediately after a player fired it.
    // If you don't, you return the arrow to the player's inventory.
    remove()
}, 1L)