package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.fuzz
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import kotlin.random.Random

/**
 * Shoots arrows that disorient the target.
 *
 * @author SugarCaney
 */
open class DisorientingBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.DISORIENTING,
        canShootInProtectedRegions = true,
        description = "Disorients the target."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return

        val newLocation = target.location.copyOf(
                yaw = target.location.yaw + 180.0.fuzz(YAW_FUZZING).toFloat(),
                pitch = Random.nextFloat() * 180f - 90f
        )
        target.teleport(newLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
    }

    companion object {

        /**
         * (Random) varience in yaw change (degrees).
         */
        private const val YAW_FUZZING = 30.0
    }
}