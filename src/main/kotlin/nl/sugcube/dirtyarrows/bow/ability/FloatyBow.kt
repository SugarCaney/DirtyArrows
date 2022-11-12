package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.Worlds
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

/**
 * Gives slow falling to the wielder when falling to fast.
 * Arrows have reduced gravity.
 *
 * @author SugarCaney
 */
open class FloatyBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.FLOATY,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Arrows float, prevents you from crashing down."
) {

    /**
     * Vertical player velocity When the slow falling effects kicks in.
     */
    val verticalVelocityThreshold: Double = config.getDouble("$node.vertical-velocity-threshold")

    /**
     * How many percent of the gravity should be reversed.
     * 1.0 => no gravity, 0.0 => normal gravity.
     */
    val gravityCompensation: Double = config.getDouble("$node.gravity-compensation")

    override fun effect() {
        arrows.forEach {
            if (it.velocity.y < 0) {
                it.velocity = it.velocity.add(Vector(0.0, Worlds.GRAVITY / 20 * gravityCompensation, 0.0))
            }
        }
    }

    @EventHandler
    fun fallProtection(event: PlayerMoveEvent) {
        val player = event.player
        if (player.hasBowInHand().not()) return

        if (player.velocity.y < verticalVelocityThreshold) {
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 60, 0, false, false, false))
        }
    }
}