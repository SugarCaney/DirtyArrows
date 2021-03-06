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
        description = "Shoots deadly anvils."
) {

    /**
     * All registered anvils mapped to the (unix epoch of birth, player who shot).
     */
    private val anvils = HashMap<FallingBlock, Pair<Long, Player>>()

    /**
     * The maximum amount of milliseconds anvil will damage surrounding entities.
     */
    val maximumLifespanMillis = config.getInt("$node.max-lifespan")

    /**
     * The damage entities receive that are within this distance from the flying anvil.
     */
    val flyingDamageAmount = config.getDouble("$node.flying.damage")

    /**
     * Entities within this distance from the flying anvil will get damaged.
     */
    val flyingDamageRange = config.getDouble("$node.flying.range")

    /**
     * The damage entities receive that are within this distance from the landing place of the anvil.
     */
    val landingDamageAmount = config.getDouble("$node.landing.damage")

    /**
     * Entities within this distance from where the anvil lands get damaged.
     */
    val landingDamageRange = config.getDouble("$node.landing.range")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        player.world.spawnFallingBlock(
                player.location.copyOf().add(0.0, 1.0, 0.0), MaterialData(Material.ANVIL)
        ).apply {
            velocity = arrow.velocity
            dropItem = true
            anvils[this] = System.currentTimeMillis() to player
        }

        unregisterArrow(arrow)
        arrow.remove()
    }

    override fun effect() {
        // Damage all entities in range of the anvil.
        anvils.keys.forEach { anvil ->
            anvil.world.entities.asSequence()
                    .mapNotNull { it as? LivingEntity }
                    .filter { it.location.isCloseTo(anvil.location, margin = flyingDamageRange) }
                    .forEach { it.damage(flyingDamageAmount) }
        }
        removeExpiredAnvils()
    }

    /**
     * Unregisters all anvils that lived longer than [maximumLifespanMillis].
     */
    private fun removeExpiredAnvils() {
        anvils.keys.removeIf { anvil ->
            val birthDate = anvils[anvil]!!.first
            val age = System.currentTimeMillis() - birthDate
            age >= maximumLifespanMillis
        }
    }

    @EventHandler
    fun anvilLandEvent(event: EntityChangeBlockEvent) {
        val anvil = event.entity as? FallingBlock ?: return
        if (anvils.containsKey(anvil).not()) return
        val player = anvils[anvil]?.second ?: return

        /// Do not land in protected areas.
        if (anvil.location.isInProtectedRegion(player)) {
            player.reimburseBowItems()
            event.isCancelled = true
            anvils.remove(anvil)
            return
        }

        // Damage all entities in range.
        anvil.world.entities.asSequence()
                .mapNotNull { it as? LivingEntity }
                .filter { it.location.isCloseTo(anvil.location, margin = landingDamageRange) }
                .forEach {
                    if (it is Player) {
                        it.playSound(anvil.location, Sound.BLOCK_ANVIL_LAND, 10f, 1f)
                    }
                    it.damage(landingDamageAmount)
                }
        anvils.remove(anvil)
    }
}