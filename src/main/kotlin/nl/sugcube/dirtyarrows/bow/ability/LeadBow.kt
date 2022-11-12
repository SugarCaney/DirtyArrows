package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack

/**
 * Puts a lead on the target entity.
 *
 * @author SugarCaney
 */
open class LeadBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.LEAD,
        canShootInProtectedRegions = false,
        protectionRange = 1.0,
        removeArrow = false,
        costRequirements = listOf(ItemStack(Material.LEAD, 1)),
        description = "Attaches a lead to the target entity."
) {

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        arrow.damage = 0.0
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val entity = event.hitEntity as? LivingEntity

        if (entity == null) {
            player.reimburseBowItems()
            return
        }

        entity.setLeashHolder(player)
        arrow.remove()
    }
}