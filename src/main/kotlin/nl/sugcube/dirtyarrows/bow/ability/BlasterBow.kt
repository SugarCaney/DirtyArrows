package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.createExplosion
import nl.sugcube.dirtyarrows.util.getFloat
import nl.sugcube.dirtyarrows.util.showSmokeParticle
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

/**
 * Shoots arrows that keep exploding until impact.
 *
 * @author SugarCaney
 */
open class BlasterBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.BLASTER,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        removeArrow = true,
        costRequirements = listOf(ItemStack(Material.GUNPOWDER, 3)),
        description = "Arrows keep exploding."
) {

    /**
     * All arrows that are affected by this bow effect, mapped to their birth time as a unix timestamp.
     */
    private val blasterArrows = ConcurrentHashMap<Arrow, Long>()

    /**
     * Arrows shoot an arrow every, this amount of, ticks.
     */
    val handleFrequency = config.getInt("$node.handle-every-n-ticks")

    /**
     * Maximum amount of milliseconds the arrows can shoot child arrows.
     */
    val maximumLifespan = config.getLong("$node.maximum-lifespan")

    /**
     * The power of the explosions. TNT is 4.0 for reference.
     */
    val power = config.getFloat("$node.power")

    /**
     * Whether the explosion can set blocks on fire.
     */
    val setOnFire = config.getBoolean("$node.set-on-fire")

    /**
     * Whether the explosion can break blocks.
     */
    val breakBlocks = config.getBoolean("$node.break-blocks")

    init {
        check(handleFrequency >= 1) { "$node.handle-every-n-ticks must be greater than or equal to 1, got <$handleFrequency>" }
    }

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        blasterArrows[arrow] = System.currentTimeMillis()
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        blasterArrows.remove(arrow)
    }

    override fun effect() {
        if (tickCounter % handleFrequency != 0) return

        blasterArrows.entries.removeIf { (arrow, birthTime) ->
            val age = System.currentTimeMillis() - birthTime
            if (age > 100) {
                arrow.explode()
            }
            age > maximumLifespan
        }
    }

    /**
     * Shoots a child projectile from this arrow when a target is in range.
     */
    private fun Arrow.explode() {
        if (location.isInProtectedRegion(shooter as? LivingEntity, showError = false)) return

        location.createExplosion(power = power, setFire = setOnFire, breakBlocks = breakBlocks)
        showSmokeParticle()
    }
}