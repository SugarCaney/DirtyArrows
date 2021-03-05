package nl.sugcube.dirtyarrows.effect

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.copyOf
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

/**
 * Displays blood particles when an entity gets damaged.
 *
 * @author SugarCaney
 */
open class Blood(private val plugin: DirtyArrows) : Listener {

    @EventHandler
    fun bloodProducer(event: EntityDamageEvent) {
        if (plugin.config.getBoolean("blood").not()) return
        val entity = event.entity as? LivingEntity ?: return
        entity.world.playEffect(entity.location.copyOf().add(0.0, 1.5, 0.0), Effect.STEP_SOUND, Material.REDSTONE_BLOCK)
    }
}