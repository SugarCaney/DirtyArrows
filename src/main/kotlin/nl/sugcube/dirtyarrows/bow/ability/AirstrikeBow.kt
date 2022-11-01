package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack

/**
 * Launches an arrow that drops ~3 tnt per second, and an initial 1 TNT.
 *
 * @author SugarCaney
 */
open class AirstrikeBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.AIRSTRIKE,
        handleEveryNTicks = 7,
        canShootInProtectedRegions = false,
        protectionRange = 6.0,
        costRequirements = listOf(ItemStack(Material.TNT, 1)),
        description = "Arrow drops TNT during flight."
) {

    /**
     * Maps each arrow to the player that fired the arrow.
     */
    private val active = HashMap<Arrow, Player>()

    /**
     * The unix timestamp of when the arrow was launched.
     */
    private val birthTime = HashMap<Arrow, Long>()

    /**
     * Maximum time the airship bow can be applied. In milliseconds.
     */
    val maximumLifespanMillis = config.getInt("$node.max-lifespan")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        active[arrow] = player
        birthTime[arrow] = System.currentTimeMillis()
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        active.remove(arrow)
        birthTime.remove(arrow)
    }

    override fun effect() {
        active.forEach { (arrow, player) ->
            if (plugin.regionManager.isWithinARegionXZMargin(arrow.location, protectionRange) != null) return@forEach
            if (meetsResourceRequirements(player = player, showError = false).not()) return@forEach

            player.world.spawnEntity(arrow.location.add(0.0, 2.0, 0.0), EntityType.PRIMED_TNT)
            player.consumeBowItems()
        }

        // Remove when arrows have their effects applied for the maximum time.
        // Sometimes the hit event is not registered correctly, therefore this must be taken care of seperately.
        active.keys.removeAll {
            val mustDie = System.currentTimeMillis() - (birthTime[it] ?: System.currentTimeMillis()) >= maximumLifespanMillis
            mustDie.apply {
                if (this) {
                    birthTime.remove(it)
                    unregisterArrow(it)
                }
            }
        }
    }
}