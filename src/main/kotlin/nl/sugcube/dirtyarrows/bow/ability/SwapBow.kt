package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerTeleportEvent

/**
 * Swaps location with the target.
 *
 * @author SugarCaney
 */
open class SwapBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.SWAP,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Swap locations with the target."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return

        val targetLocation = target.location
        val playerLocation = player.location
        target.teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
        player.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
    }
}