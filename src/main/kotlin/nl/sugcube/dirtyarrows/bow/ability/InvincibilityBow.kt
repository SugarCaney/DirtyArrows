package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.scheduleDelayed
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Shoots powerful arrows that make the target invincible for a while.
 *
 * @author SugarCaney
 */
open class InvincibilityBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.INVINCIBILITY,
        handleEveryNTicks = 60,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Powerful shots. Makes targets invincible."
) {

    /**
     * The Power enchantment level of each arrow.
     */
    private val powerLevel = HashMap<Arrow, Int>()

    /**
     * Multiplier for the damage dealt by the arrows.
     */
    val damageMultiplier = config.getDouble("$node.damage-multiplier")

    /**
     * The base amount of invincibility ticks after a player has been hit.
     */
    val baseInvincibilityTime = config.getInt("$node.base-time")

    /**
     * How many extra ticks of invincibility time to give per power level of the bow.
     */
    val extraInvincibilityTimePerPowerLevel = config.getInt("$node.extra-time")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        powerLevel[arrow] = player.bowItem()?.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) ?: 0
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrows.add(arrow)
    }

    override fun effect() {
        powerLevel.entries.removeIf { (arrow, _) ->
            arrow.ticksLived > 600
        }
    }

    @EventHandler
    fun damageEvent(event: EntityDamageByEntityEvent) {
        val hitter = event.damager as? Arrow ?: return
        if (hitter !in arrows) return

        val target = event.entity as? LivingEntity ?: return
        val powerLevel = powerLevel[hitter] ?: 0
        event.damage = event.damage * damageMultiplier

        // Schedule for next tick, otherwise no damage is dealt.
        plugin.scheduleDelayed(1L) {
            target.makeInvincible(powerLevel = powerLevel)
        }
    }

    /**
     * Makes this entity invincible according to the bow specs.
     */
    private fun LivingEntity.makeInvincible(powerLevel: Int = 0) {
        val duration = baseInvincibilityTime + powerLevel * extraInvincibilityTimePerPowerLevel
        addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, 999, false, true))
    }
}