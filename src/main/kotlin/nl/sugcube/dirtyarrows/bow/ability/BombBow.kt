package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.showSmokeParticle
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

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
        costRequirements = listOf(ItemStack(Material.TNT, 3)),
        description = "Let 3 TNT fall from above."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        repeat(3) {
            val bombLocation = arrow.location.copyOf().add(
                    (Random.nextInt(9) - 4).toDouble(),
                    32.0,
                    (Random.nextInt(9) - 4).toDouble()
            )
            arrow.world.spawnEntity(bombLocation, EntityType.PRIMED_TNT)
        }
    }

    override fun particle(tickNumber: Int) = arrows.forEach {
        it.showSmokeParticle()
    }
}