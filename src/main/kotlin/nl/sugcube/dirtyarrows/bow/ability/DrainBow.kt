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
import kotlin.math.abs
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
        description = "Gain health back every hit."
) {

    /**
     * The amount of health points to heal on every hit. 1 point = half a heart.
     */
    val healthPointsToHeal = config.getInt("$node.health-points")

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return
        if (target == player) return

        val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).value
        player.health = min(maxHealth, player.health + healthPointsToHeal)
        repeat(abs(healthPointsToHeal)) {
            player.location.showHealParticle(1)
        }
    }
}