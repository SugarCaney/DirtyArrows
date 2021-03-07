package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.applyBowEnchantments
import nl.sugcube.dirtyarrows.util.fuzz
import nl.sugcube.dirtyarrows.util.subtractDurability
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.max

/**
 * Shoots very quickly and immediately.
 *
 * @author SugarCaney
 */
open class MachineBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.MACHINE,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Shoots quickly, and slightly accurate."
) {

    /**
     * The amount of arrows to fire per click.
     */
    val bulletCount = config.getInt("$node.bullet-count")

    /**
     * The amount of ticks between each consecutive arrow.
     */
    val bulletDelay = config.getInt("$node.bullet-delay").toLong()

    /**
     * Maximum deviation for the launch direction.
     */
    val directionFuzz = config.getDouble("$node.direction-fuzz")

    init {
        check(bulletCount >= 1) { "$node.bullet-count must be greater than or equal to 1, got <$bulletCount>" }
        check(bulletDelay >= 0) { "$node.bullet-count cannot be negative, got <$bulletDelay>" }
    }

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        unregisterArrow(arrow)
        arrow.remove()
    }

    @EventHandler
    fun clickBowHandler(event: PlayerInteractEvent) {
        val player = event.player
        if (plugin.activationManager.isActivatedFor(player).not()) return
        if (player.hasBowInHand().not()) return

        val direction = player.eyeLocation.toVector().add(player.location.direction.multiply(3))
        val spawnLocation = direction.toLocation(player.world)

        shootBullet(player, spawnLocation)
        repeat(max(0, bulletCount - 1)) {
            plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                shootBullet(player, spawnLocation)
            }, it * bulletDelay)
        }

        event.isCancelled
    }

    /**
     * Checks if the player has arrows in their inventory.
     */
    private fun Player.hasArrows() = gameMode == GameMode.CREATIVE || inventory.contains(Material.ARROW)

    /**
     * Shoots a machine gun bullet from the given spawn location.
     */
    private fun shootBullet(player: Player, spawnLocation: Location) {
        if (player.hasArrows().not()) return

        val bow = player.bowItem()
        player.world.spawnEntity(spawnLocation, EntityType.ARROW).apply {
            with(this as Arrow) {
                applyBowEnchantments(bow)
                shooter = player
                velocity = player.eyeLocation.direction.multiply(3).fuzz(maxFuzz = directionFuzz)

                if (player.gameMode == GameMode.CREATIVE) {
                    pickupStatus = Arrow.PickupStatus.CREATIVE_ONLY
                }

                player.bowItem()?.subtractDurability(player)
                player.playEffect(player.location, Effect.BOW_FIRE, null)

                if (bow?.containsEnchantment(Enchantment.ARROW_INFINITE) == false) {
                    player.inventory.removeItem(ItemStack(Material.ARROW, 1))
                }
            }
        }
    }
}