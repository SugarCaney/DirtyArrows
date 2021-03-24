package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.showHealParticle
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Ageable
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent

/**
 * Turns baby animals into adults. Turns adult animals into babies. Heals the animal.
 *
 * @author SugarCaney
 */
open class BabyBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.BABY,
        canShootInProtectedRegions = false,
        protectionRange = 1.0,
        description = "Turn babies into adults, and adults into babies.",
        removeArrow = false
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val entity = event.hitEntity as? Ageable ?: return

        if (entity.isAdult) {
            entity.setBaby()
        }
        else entity.setAdult()

        entity.health = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).value
        repeat(5) {
            entity.location.showHealParticle(1)
        }

        arrow.remove()
    }
}