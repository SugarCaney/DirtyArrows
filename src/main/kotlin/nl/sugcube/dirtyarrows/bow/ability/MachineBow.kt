package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.applyBowEnchantments
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.subtractDurability
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector
import kotlin.random.Random

/**
 * Shoots very quickly and immediately.
 *
 * @author SugarCaney
 */
open class MachineBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.MACHINE,
        canShootInProtectedRegions = true,
        removeArrow = false
) {

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        unregisterArrow(arrow)
        arrow.remove()
    }

    @EventHandler
    fun clickBowHandler(event: PlayerInteractEvent) {
        val player = event.player
        if (player.hasBowInHand().not()) return

        val direction = player.eyeLocation.toVector().add(player.location.direction.multiply(3))
        val spawnLocation = direction.toLocation(player.world)

        shootBullet(player, spawnLocation)
        repeat(2) {
            plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                shootBullet(player, spawnLocation)
            }, it * 2L)
        }

        event.isCancelled
    }

    /**
     * Shoots a machine gun bullet from the given spawn location.
     */
    private fun shootBullet(player: Player, spawnLocation: Location) {
        player.world.spawnEntity(spawnLocation, EntityType.ARROW).apply {
            with(this as Arrow) {
                applyBowEnchantments(player.bowItem())
                shooter = player
                velocity = player.eyeLocation.direction.multiply(3).fuzzDirection(maxFuzz = 0.22)

                player.bowItem()?.subtractDurability(player)
                player.playEffect(player.location, Effect.BOW_FIRE, null)
            }
        }
    }

    /**
     * Changes the direction of the vector randomly with at most `maxFuzz`.
     */
    private fun Vector.fuzzDirection(maxFuzz: Double) = copyOf(
            x = x + Random.nextDouble() * 2 * maxFuzz - maxFuzz,
            y = y + Random.nextDouble() * 2 * maxFuzz - maxFuzz,
            z = z + Random.nextDouble() * 2 * maxFuzz - maxFuzz,
    )
}