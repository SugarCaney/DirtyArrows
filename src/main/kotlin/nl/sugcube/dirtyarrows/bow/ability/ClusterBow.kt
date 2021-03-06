package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.fuzz
import nl.sugcube.dirtyarrows.util.showSmokeParticle
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

/**
 * Shoots arrows that drops 3 TNT at the location of impact.
 *
 * @author SugarCaney
 */
open class ClusterBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.CLUSTER,
        canShootInProtectedRegions = false,
        protectionRange = 24.0,
        description = "Ignite clusters of TNT."
) {

    /**
     * The amount of TNT to spawn.
     */
    val tntCount = config.getInt("$node.tnt-count")

    /**
     * How long the TNT is ignited before it explodes.
     */
    val tntFuse = config.getInt("$node.tnt-fuse")

    /**
     * The maximum horizontal velocity a spawned, ignited tnt block gets.
     */
    val maximumHorizontalVelocity = config.getDouble("$node.maximum-horizontal-velocity")

    init {
        check(tntCount >= 0) { "$node.tnt-count cannot be negative, got <$tntCount>" }
        check(tntFuse >= 0) { "$node.tnt-fuse cannot be negative, got <$tntFuse> " }

        costRequirements = listOf(ItemStack(Material.TNT, tntCount))
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        repeat(tntCount) {
            arrow.world.spawnEntity(arrow.location, EntityType.PRIMED_TNT).apply {
                val tnt = this as TNTPrimed
                tnt.fuseTicks = tntFuse
                tnt.velocity = randomVelocity()
            }
        }
    }

    /**
     * Generate a random velocity vector for the primed TNT.
     */
    private fun randomVelocity() = Vector().apply {
        x = 0.0.fuzz(maximumHorizontalVelocity)
        y = 0.875.fuzz(0.175)
        z = 0.0.fuzz(maximumHorizontalVelocity)
    }

    override fun particle(tickNumber: Int) = arrows.forEach {
        it.showSmokeParticle()
    }
}