package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.scheduleDelayed
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack

/**
 * Flies behind the arrow.
 *
 * @author SugarCaney
 */
open class AirshipBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.AIRSHIP,
        handleEveryNTicks = 2,
        canShootInProtectedRegions = true,
        costRequirements = listOf(ItemStack(Material.FEATHER, 2)),
        description = "Fly behind your arrows."
) {

    /**
     * Maps each arrow to the player that fired the arrow.
     */
    private val active = HashMap<Arrow, Player>()

    /**
     * All players that activated the airship bow.
     */
    private val players = HashSet<Player>()

    /**
     * The unix timestamp where the arrow was launched.
     */
    private val birthTime = HashMap<Arrow, Long>()

    /**
     * Maximum time the airship bow can be applied. In milliseconds.
     */
    val maximumLifespanMillis = config.getInt("$node.max-lifespan")

    /**
     * How many times the arrow velocity to fly.
     */
    val velocityMultiplier = config.getDouble("$node.velocity-multiplier")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        active[arrow] = player
        players.add(player)
        birthTime[arrow] = System.currentTimeMillis()
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        active.remove(arrow)
        birthTime.remove(arrow)

        plugin.scheduleDelayed(10L) {
            players.remove(player)
        }
    }

    override fun effect() {
        active.forEach { (arrow, player) ->
            player.velocity = arrow.velocity.copyOf().multiply(velocityMultiplier)
        }

        // Remove when arrows have their effects applied for the maximum time.
        // Sometimes the hit event is not registered correctly, therefore this must be taken care of seperately.
        active.keys.removeAll {
            val mustDie = System.currentTimeMillis() - (birthTime[it]
                    ?: System.currentTimeMillis()) >= maximumLifespanMillis
            mustDie.apply {
                if (this) {
                    players.remove(active[it])
                    birthTime.remove(it)
                    unregisterArrow(it)
                }
            }
        }
    }

    @EventHandler
    fun cancelFallDamage(event: EntityDamageEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.FALL) return
        val player = event.entity as? Player ?: return
        if (player in players) {
            event.damage = 0.0
            event.isCancelled = true
        }
    }
}