package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots arrows that generate lightning on impact.
 *
 * @author SugarCaney
 */
open class LightningBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.LIGHTNING,
        canShootInProtectedRegions = false,
        protectionRange = 3.0,
        costRequirements = listOf(ItemStack(Material.GLOWSTONE_DUST, 1)),
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.location.world.strikeLightning(arrow.location)
    }
}