package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.fuzz
import nl.sugcube.dirtyarrows.util.showSmokeParticle
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots arrows that drops 3 TNT at the location of impact.
 *
 * @author SugarCaney
 */
open class BombBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.BOMB,
        canShootInProtectedRegions = false,
        protectionRange = 16.0,
        description = "Let TNT fall from above."
) {

    /**
     * The amount of TNT to spawn.
     */
    val bombCount = config.getInt("$node.bomb-count")

    /**
     * The square radius around the location of impact where the bombs can spawn.
     */
    val bombRadius = config.getDouble("$node.radius")

    /**
     * How high above location of impact the bombs must spawn.
     */
    val spawnHeight = config.getDouble("$node.spawn-height")

    init {
        check(bombCount >= 0) { "$node.bomb-count cannot be negative, got <$bombCount>" }

        costRequirements = listOf(ItemStack(Material.TNT, bombCount))
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        repeat(bombCount) {
            val bombLocation = arrow.location.copyOf().add(
                    0.0.fuzz(bombRadius),
                    spawnHeight,
                    0.0.fuzz(bombRadius)
            )
            arrow.world.spawnEntity(bombLocation, EntityType.PRIMED_TNT)
        }
    }

    override fun particle(tickNumber: Int) = arrows.forEach {
        it.showSmokeParticle()
    }
}