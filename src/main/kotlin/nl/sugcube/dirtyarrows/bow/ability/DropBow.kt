package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent

/**
 * Shoots the target entity up so it will drop down.
 *
 * @author SugarCaney
 */
open class DropBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.DROP,
        canShootInProtectedRegions = true,
        description = "Launches the target in the air."
) {

    /**
     * How many blocks the targets get teleported upward.
     */
    val dropHeight = config.getInt("$node.drop-height")

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return
        if (target == player) return

        val dropLocation = target.location
        for (i in 1..dropHeight) {
            val block = target.world.getBlockAt(dropLocation.blockX, dropLocation.blockY + i, dropLocation.blockZ)
            if (block?.type?.isSolid == true) {
                target.teleport(dropLocation.copyOf(y = (dropLocation.blockY + i).toDouble()))
                return
            }
        }

        target.teleport(dropLocation.copyOf(y = (dropLocation.blockY + dropHeight).toDouble()))
    }
}