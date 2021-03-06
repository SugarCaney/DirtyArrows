package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.showHealParticle
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import kotlin.math.min

/**
 * Regain 1 heart of health per hit on a different entity.
 *
 * @author SugarCaney
 */
open class DrainBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.DRAINING,
        canShootInProtectedRegions = true,
        description = "Gain 1 heart every hit."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return
        if (target == player) return

        val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).value
        player.health = min(maxHealth, player.health + 2)
        repeat(2) {
            player.showHealParticle(1)
        }
    }
}