package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.showSmokeParticle
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.random.Random

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
        costRequirements = listOf(ItemStack(Material.TNT, 5)),
        description = "Ignite clusters of 5 TNT."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        repeat(5) {
            arrow.world.spawnEntity(arrow.location, EntityType.PRIMED_TNT).apply {
                val tnt = this as TNTPrimed
                tnt.fuseTicks = 40
                tnt.velocity = randomVelocity()
            }
        }
    }

    /**
     * Generate a random velocity vector for the primed TNT.
     */
    private fun randomVelocity() = Vector().apply {
        x = Random.nextDouble() * 0.147 * 2
        y = 0.7 + Random.nextDouble() * 0.35
        z = Random.nextDouble() * 0.147 * 2
    }

    override fun particle(tickNumber: Int) = arrows.forEach {
        it.showSmokeParticle()
    }
}