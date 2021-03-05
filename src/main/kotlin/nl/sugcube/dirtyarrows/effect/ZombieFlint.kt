package nl.sugcube.dirtyarrows.effect

import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.Material
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * Let zombies drop flint on death.
 *
 * @author SugarCaney
 */
open class ZombieFlint(private val plugin: DirtyArrows) : Listener {

    @EventHandler
    fun flintDropper(event: EntityDeathEvent) {
        if (event.entity !is Zombie) return
        if (plugin.config.getBoolean("zombie-flint").not()) return
        if (Random.nextDouble() > DROP_CHANCE) return

        val amount = Random.nextInt(1, MAXIMUM_DROP_AMOUNT + 1)
        event.drops.add(ItemStack(Material.FLINT, amount))
    }

    companion object {

        /**
         * The chance for flint to drop on death of a zombie.
         */
        private const val DROP_CHANCE = 0.1

        /**
         * The maximum amount of flint a zombie can drop at once.
         */
        private const val MAXIMUM_DROP_AMOUNT = 2
    }
}