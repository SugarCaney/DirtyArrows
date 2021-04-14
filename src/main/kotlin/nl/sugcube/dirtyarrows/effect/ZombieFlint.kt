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
 * Not affected by Looting enchantment.
 *
 * @author SugarCaney
 */
open class ZombieFlint(private val plugin: DirtyArrows) : Listener {

    /**
     * Whether zombies should drop flint or not on death.
     */
    val enabled: Boolean
        get() = plugin.config.getBoolean("zombie-flint.enabled")

    /**
     * The chance for flint to drop on death of a zombie in range [0,1] inclusive.
     */
    val dropChance: Double
        get() = plugin.config.getDouble("zombie-flint.drop-chance")

    /**
     * The maximum amount of flint that can drop at once.
     * The actual amount will be a value between 1 and this value (inclusive).
     */
    val maximumDropAmount: Int
        get() = plugin.config.getInt("zombie-flint.maximum-drop-count")

    @EventHandler
    fun flintDropper(event: EntityDeathEvent) {
        if (event.entity !is Zombie) return
        if (enabled.not()) return
        if (Random.nextDouble() > dropChance) return

        val amount = Random.nextInt(1, maximumDropAmount + 1)
        event.drops.add(ItemStack(Material.FLINT, amount))
    }
}