package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.getFloat
import nl.sugcube.dirtyarrows.util.scheduleDelayed
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random
import kotlin.random.nextLong

/**
 * Does more damage, at the cost of playing annoying sounds and making you walk slowly.
 *
 * @author SugarCaney
 */
open class BaneBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.BANE,
        canShootInProtectedRegions = true,
        description = "Power, at the cost of insanity."
) {

    /**
     * The highest pitch value the played sounds can have.
     */
    val maximumPitch = config.getFloat("$node.maximum-pitch")

    /**
     * The level of slowness to apply (level I starts with value 0).
     */
    val slownessLevel = config.getInt("$node.slowness-level")

    /**
     * Multiplier for damage inflicted by arrows shot by the Bane.
     */
    val damageMultiplier = config.getDouble("$node.damage-multiplier")

    /**
     * All bane arrows that hit their targets.
     */
    private val baneHits = HashSet<Arrow>()

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        player.annoy()

        repeat(Random.nextInt(0, 3)) {
            plugin.scheduleDelayed(it * 4 * Random.nextLong(3L..12L)) {
                player.annoy()
            }
        }
    }

    fun Player.annoy() = playSound(location, Sound.ENTITY_HORSE_DEATH, 1f, Random.nextFloat() * maximumPitch)

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        if (event.hitEntity != null) {
            baneHits.add(arrow)
        }
    }

    @EventHandler
    fun slowWalking(event: PlayerMoveEvent) {
        val player = event.player
        if (player.hasBowInHand().not()) return

        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 40, slownessLevel, false, false, false))
    }

    @EventHandler
    fun increaseDamage(event: EntityDamageByEntityEvent) {
        val arrow = event.damager as? Arrow ?: return
        if (arrow !in baneHits) return

        baneHits.remove(arrow)
        event.damage = event.damage * damageMultiplier
    }
}