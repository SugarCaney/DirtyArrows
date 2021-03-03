package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.isCloseTo
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData

/**
 * Fires an anvil.
 *
 * @author SugarCaney
 */
open class IronBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.IRON,
        handleEveryNTicks = 5,
        canShootInProtectedRegions = false,
        protectionRange = 1.0,
        costRequirements = listOf(ItemStack(Material.ANVIL, 1)),
) {

    /**
     * All registered anvils mapped to the unix epoch of birth.
     */
    private val anvils = HashMap<FallingBlock, Long>()

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        player.world.spawnFallingBlock(
                player.location.copyOf().add(0.0, 1.0, 0.0), MaterialData(Material.ANVIL)
        ).apply {
            velocity = arrow.velocity
            dropItem = true
            anvils[this] = System.currentTimeMillis()
        }
    }

    override fun effect() {
        // Damage all entities in range of the anvil.
        anvils.keys.forEach { anvil ->
            anvil.world.entities.asSequence()
                    .mapNotNull { it as? LivingEntity }
                    .filter { it.location.isCloseTo(anvil.location, margin = 1.0) }
                    .forEach { it.damage(3.0) }
        }
        removeExpiredAnvils()
    }

    /**
     * Unregisters all anvils that lived longer than [MAX_LIFESPAN_MILLIS].
     */
    private fun removeExpiredAnvils() {
        anvils.keys.removeIf { anvil ->
            val birthDate = anvils[anvil]!!
            val age = System.currentTimeMillis() - birthDate
            age >= MAX_LIFESPAN_MILLIS
        }
    }

    @EventHandler
    fun anvilLandEvent(event: EntityChangeBlockEvent) {
        val anvil = event.entity as? FallingBlock ?: return
        if (anvils.containsKey(anvil).not()) return

        // Damage all entities in range.
        anvil.world.entities.asSequence()
                .mapNotNull { it as? LivingEntity }
                .filter { it.location.isCloseTo(anvil.location, margin = 3.0) }
                .forEach {
                    if (it is Player) {
                        it.playSound(anvil.location, Sound.BLOCK_ANVIL_LAND, 10f, 1f)
                    }
                    it.damage(10.0)p
                }
        anvils.remove(anvil)
    }

    companion object {

        /**
         * Maximum time the anvil damage can be applied. In milliseconds.
         */
        const val MAX_LIFESPAN_MILLIS = 7000
    }
}