package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.firstSpawnEligibleY
import nl.sugcube.dirtyarrows.util.spawn
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Shoots arrows that spawns a circle of zombies.
 *
 * @author SugarCaney
 */
open class UndeadBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.UNDEAD,
        canShootInProtectedRegions = false,
        protectionRange = 8.0,
        costRequirements = listOf(ItemStack(Material.ROTTEN_FLESH, 64)),
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.location.spawnZombieSwarm(zombieCount = 16, distance = 5.0)
    }

    /**
     * Spawns a circle of zombies around the given location.
     *
     * @param zombieCount
     *          The amount of zombies to spawn (strictly greater than 0).
     * @param distance
     *          How far from the location the zombies must spawn (radius, >= 0).
     */
    fun Location.spawnZombieSwarm(zombieCount: Int = 16, distance: Double = 5.0) {
        require(zombieCount > 0) { "Zombie count must be positive, was $zombieCount" }
        require(distance >= 0.0) { "Distance must be nonnegative, was $distance" }

        for (i in 0 until zombieCount) {
            val angle = 2 * PI * i / zombieCount.toDouble()
            val circleX = x + distance * cos(angle)
            val circleZ = z + distance * sin(angle)

            Location(world, circleX, y, circleZ).let {
                it.y = it.firstSpawnEligibleY()?.toDouble() ?: y
                it.spawn(Zombie::class)
                world.playEffect(it, Effect.MOBSPAWNER_FLAMES, 0)
            }
        }
    }
}