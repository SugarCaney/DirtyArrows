package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Cooks items on the ground.
 *
 * @author SugarCaney
 */
open class ChefsBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.CHEFS,
        canShootInProtectedRegions = true,
        removeArrow = true,
        costRequirements = listOf(ItemStack(Material.COAL, 1)),
        description = "Arrows cook items on land."
) {

    /**
     * How close to the landing site items can be cooked.
     */
    val cookingRange = config.getDouble("$node.cooking-range")

    /**
     * How long it takes for an item to cook in seconds.
     */
    val cookingTime = config.getInt("$node.cooking-time")

    /**
     * The maximum amount of item stacks that can be cooked at once.
     */
    val maxItems = config.getInt("$node.max-items")

    private val cookingMap = HashMap<Item, Int>()

    init {
        check(cookingRange > 0.0) { "$node.cooking-range must be greater than 0.0, got <$cookingRange>" }
        check(cookingTime >= 0.0) { "$node.cooking-time must be non negative, got <$cookingTime>" }
        check(maxItems >= 0.0) { "$node.max-items must be non negative, got <$maxItems>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.location.nearbyEntities(cookingRange).asSequence()
            .mapNotNull { it as? Item }
            .filter { it !in cookingMap }
            .filter { it.itemStack.type in SMELT_RESULTS }
            .take(maxItems)
            .forEach { cookingMap[it] = cookingTime }

        arrow.location.showFlameParticle()
    }

    override fun effect() {
        cookingMap.keys.forEach { item ->
            val timeLeft = (cookingMap[item] ?: 0) - 1

            if (timeLeft < 0) {
                plugin.scheduleDelayed(0) { cookingMap.remove(item) }
                item.cook()
                return@forEach
            }

            cookingMap[item] = timeLeft
            item.showSmokeParticle()
        }

        cleanInvalidItems()
    }

    private fun cleanInvalidItems() = cookingMap.entries.removeIf { (item, _) ->
        item.isDead
    }

    private fun Item.cook() {
        val dropSpot = location.clone()
        val itemStack = itemStack
        val resultItemStack = SMELT_RESULTS[itemStack.type] ?: return
        remove()

        var amount = itemStack.amount * resultItemStack.amount
        while (amount > 64) {
            dropSpot.dropItem(ItemStack(resultItemStack.type, 64))
            amount -= 64
        }
        dropSpot.dropItem(ItemStack(resultItemStack.type, amount))

        dropSpot.showFlameParticle()
    }

    override fun particle(tickNumber: Int) {
        if (tickNumber % 7 != 0) return

        cookingMap.keys.forEach {
            it.showSmokeParticle()
            it.world.playSound(it.location, Sound.BLOCK_FURNACE_FIRE_CRACKLE, 1.0f, 0.0f)
        }
    }
}