package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.createExplosion
import nl.sugcube.dirtyarrows.util.showFlameParticle
import nl.sugcube.dirtyarrows.util.showSmokeParticle
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots arrows that create a huge explode on impact.
 *
 * @author SugarCaney
 */
open class NuclearBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.NUCLEAR,
        canShootInProtectedRegions = false,
        protectionRange = 64.0,
        costRequirements = listOf(ItemStack(Material.TNT, 64)),
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.location.createExplosion(power = 55f)
    }

    override fun particle(tickNumber: Int) = arrows.forEach {
        it.showSmokeParticle()

        if (tickNumber % 5 == 0) {
            it.showFlameParticle()
        }
    }
}