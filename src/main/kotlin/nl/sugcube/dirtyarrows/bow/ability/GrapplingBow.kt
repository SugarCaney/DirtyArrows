package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.dropItem
import nl.sugcube.dirtyarrows.util.lineIteration
import nl.sugcube.dirtyarrows.util.showColoredDust
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

/**
 * Grappling hook.
 *
 * @author SugarCaney
 */
open class GrapplingBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.GRAPPLING,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Grappling hook.",
        costRequirements = listOf(ItemStack(Material.TRIPWIRE_HOOK, 1))
) {

    /**
     * All arrows that landed mapped to their birth time.
     */
    private val landedArrows = HashMap<Arrow, Long>()

    /**
     * Contains all arrows where the shooter must have their fall damage cancelled next tick.
     * Otherwise, shooting to the ground will result in fall damage.
     */
    private val cancelFallDamage = ConcurrentHashMap.newKeySet<Arrow>()

    /**
     * How quickly to travel to the grappling hook location (constant speed).
     */
    val travelSpeed = config.getDouble("$node.travel-speed")

    /**
     * The maximum amount of milliseconds the grappling hook can last.
     * Is mostly here as fail safe in case the person gets stuck.
     */
    val maximumDuration = config.getLong("$node.maximum-duration")

    init {
        check(travelSpeed > 0) { "$node.travel-speed must be greater than 0, got <$travelSpeed>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        landedArrows[arrow] = System.currentTimeMillis()
    }

    override fun effect() {
        cancelFallDamage.clear()
        landedArrows.entries.removeIf { (arrow, birthTime) ->
            val shooter = arrow.shooter as Player
            val direction = arrow.location.subtract(shooter.location).toVector().normalize()

            shooter.velocity = direction.multiply(travelSpeed)

            // Hook is done.
            val timeExpired = System.currentTimeMillis() - birthTime >= maximumDuration
            if (timeExpired || shooter.velocity.length() < 0.5 || shooter.location.distance(arrow.location) < 2.5) {
                cancelFallDamage.add(arrow)
                shooter.velocity = Vector(0, 0, 0)
                shooter.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 10, -1, true, false))

                if (shooter.gameMode != GameMode.CREATIVE) {
                    arrow.location.dropItem(ItemStack(Material.TRIPWIRE_HOOK, 1))
                }
                true
            }
            else false
        }
    }

    override fun particle(tickNumber: Int) {
        if (tickNumber % 4 == 0) return

        landedArrows.entries.forEach { (arrow, _) ->
            val player = arrow.shooter as Player
            player.location.add(0.0, 0.75, 0.0).toVector().lineIteration(arrow.location.toVector(), 0.5).forEach {
                it.toLocation(arrow.world).showColoredDust(200, 200, 200, 1)
            }
        }
    }

    @EventHandler
    fun disableFallDamage(event: EntityDamageEvent) {
        // Make sure to disable fall damage during the flight.
        if (event.cause != EntityDamageEvent.DamageCause.FALL) return
        event.isCancelled = cancelFallDamage.any { it.shooter == event.entity }
    }
}