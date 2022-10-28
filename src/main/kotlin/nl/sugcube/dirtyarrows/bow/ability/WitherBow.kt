package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.scheduleRemoval
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.WitherSkull
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots a wither skull.
 *
 * @author SugarCaney
 */
open class WitherBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.WITHER,
        canShootInProtectedRegions = false,
        protectionRange = 5.0,
        costRequirements = listOf(ItemStack(Material.SOUL_SAND, 3)),
        description = "Shoot wither skulls."
) {

    /**
     * Multiplier of the initial arrow velocity.
     */
    val skullSpeedModifier = config.getDouble("$node.head-speed-multiplier")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        player.launchProjectile(WitherSkull::class.java, arrow.velocity.clone().multiply(skullSpeedModifier))
        unregisterArrow(arrow)
        arrow.scheduleRemoval(plugin)
    }
}