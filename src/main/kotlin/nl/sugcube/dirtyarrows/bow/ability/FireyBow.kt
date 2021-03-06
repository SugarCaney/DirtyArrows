package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Fireball
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots a wither skull.
 *
 * @author SugarCaney
 */
open class FireyBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.FIREY,
        canShootInProtectedRegions = false,
        protectionRange = 5.0,
        costRequirements = listOf(ItemStack(Material.FIREBALL, 1)),
        description = "Shoot fireballs."
) {

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        player.launchProjectile(Fireball::class.java, arrow.velocity.multiply(0.8))
        unregisterArrow(arrow)
        arrow.remove()
    }
}