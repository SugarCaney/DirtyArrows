package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.showHealParticle
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Tameable
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent

/**
 * Tames the targeted animal.
 *
 * @author SugarCaney
 */
open class TamingBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.TAMING,
        canShootInProtectedRegions = false,
        protectionRange = 1.0,
        removeArrow = false,
        description = "Tames the target mob."
) {

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        arrow.damage = 0.0
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val entity = event.hitEntity as? Tameable ?: return

        if (entity.isTamed.not()) {
            entity.isTamed = true
            entity.owner = player
            entity.health = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
            repeat(5) {
                entity.location.showHealParticle(1, 0.5)
            }
        }

        arrow.remove()
    }
}