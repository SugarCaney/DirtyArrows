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

/**
 * Shoots arrows that disorient the target.
 *
 * @author SugarCaney
 */
open class DisorientingBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.DISORIENTING,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Disorients the target."
) {

    /**
     * Maximum change in yaw in degrees.
     */
    val yawFuzzing = config.getDouble("$node.yaw-fuzzing").toFloat()

    /**
     * Maximum change in pitch in degrees.
     */
    val pitchFuzzing = config.getDouble("$node.pitch-fuzzing").toFloat()

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return

        val newLocation = target.location.copyOf(
                yaw = target.location.yaw + 180.0f.fuzz(yawFuzzing),
                pitch = 0f.fuzz(pitchFuzzing)
        )
        target.teleport(newLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
    }
}