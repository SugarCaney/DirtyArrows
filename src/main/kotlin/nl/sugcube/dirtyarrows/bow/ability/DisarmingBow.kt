package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import kotlin.random.Random

/**
 * Can remove items from the hit target (33% chance).
 *
 * @author SugarCaney
 */
open class DisarmingBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.DISARMING,
        canShootInProtectedRegions = true,
        description = "Targets have a chance to drop their items."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        if (Random.nextDouble() < DISARM_CHANCE) return
        val target = event.hitEntity as? LivingEntity ?: return

        // Always drop hand item.
        target.dropHandItem()

        // Chance to drop armour.
        val newArmour = target.equipment.armorContents.map { item ->
            if (Random.nextDouble() < ARMOUR_CHANCE) {
                target.world.dropItem(target.location, item)
                item.type = Material.AIR
            }
            item
        }
        target.equipment.armorContents = newArmour.toTypedArray()
    }

    /**
     * Removes items from the entity's hands.
     */
    private fun LivingEntity.dropHandItem() = with(equipment) {
        // Prioritize dropping offhand over main hand.
        val offhandItem = itemInOffHand
        if (offhandItem.type != Material.AIR) {
            world.dropItem(location, offhandItem)
            offhandItem.type = Material.AIR
            itemInOffHand = offhandItem
            return
        }

        val mainHandItem = itemInMainHand
        if (mainHandItem.type != Material.AIR) {
            world.dropItem(location, mainHandItem)
            mainHandItem.type = Material.AIR
            itemInMainHand = mainHandItem
        }
    }

    companion object {

        /**
         * The chance of dropping each armor piece.
         */
        private const val ARMOUR_CHANCE = 0.15

        /**
         * The chance of disarming anything.
         */
        private const val DISARM_CHANCE = 0.3
    }
}