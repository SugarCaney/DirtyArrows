package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * Breaks and drops all shearable blocks.
 * Shears sheep in range on impact.
 * Shears mooshrooms in range on impact.
 * Shears snow golems in range on impact.
 *
 * @author SugarCaney
 */
open class ShearBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.SHEAR,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        removeArrow = true,
        description = "Breaks all shearable blocks."
) {

    /**
     * The range around the location of impact where special blocks/entities (sheep, pumpkins, ...), in blocks.
     */
    val impactRange = config.getDouble("$node.impact-range")

    /**
     * How much the range increases per power level on the bow, in blocks.
     */
    val rangeIncrease = config.getDouble("$node.range-increase")

    init {
        require(impactRange >= 0) { "$node.impact-range must not be negative, got <$impactRange>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        // When an entity is hit, don't bounce.
        if (event.hitEntity != null) {
            arrow.location.impactEffects(player)
            arrow.remove()
            return
        }

        val hitBlockType = event.hitBlock?.type ?: return
        if (hitBlockType != Material.AIR && hitBlockType.isShearable.not()) {
            arrow.location.impactEffects(player)
            return
        }

        event.hitBlock?.shearBlocks()
        registerArrow(arrow.respawnArrow())
    }

    override fun effect() {
        arrows.forEach {
            if (it.location.isInProtectedRegion(it.shooter as? LivingEntity, showError = false).not()) {
                it.location.block.shearBlocks()
            }
        }
    }

    /**
     * Shears all blocks around this block.
     */
    @Suppress("DEPRECATION")
    private fun Block.shearBlocks() {
        forXYZ(-1..1, -1..1, -1..1) { dx, dy, dz ->
            val block = getRelative(dx, dy, dz)
            val originalType = block.type

            if (originalType.isShearable) {
                val itemDrop = ItemStack(originalType, 1, block.itemDataValue().toShort())
                block.centreLocation.dropItem(itemDrop)
                block.type = Material.AIR
            }
        }
    }

    /**
     * Get the data value for the item that is dropped by this block.
     */
    @Suppress("DEPRECATION")
    private fun Block.itemDataValue(): Byte = when (type) {
        // TODO: Shear Bow Leave block data
//        Material.LEAVES -> (state.data as Leaves).species.data
//        Material.LEAVES_2 -> ((state.data as Leaves).species.data - 4).toByte()
//        Material.LONG_GRASS -> (state.data as LongGrass).species.data
//        Material.DOUBLE_PLANT -> state.data.data
        else -> 0
    }

    /**
     * Processes all effects that happen when the arrow lands.
     */
    private fun Location.impactEffects(player: Player) {
        val bowItem = player.bowItem() ?: return
        val lootingLevel = bowItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)
        val powerLevel = bowItem.getEnchantmentLevel(Enchantment.ARROW_DAMAGE)
        val range = impactRange + powerLevel * rangeIncrease

        shearSheep(range, lootingLevel)
        shearMooshrooms(range, lootingLevel)
        shearSnowGolems(range)
    }

    /**
     * Shears all sheep around this location.
     *
     * @param range
     *          How far from this location to check for sheep.
     * @param lootingLevel
     *          The level of the looting enchantment (0 if not applicable).
     */
    @Suppress("DEPRECATION")
    private fun Location.shearSheep(range: Double, lootingLevel: Int = 0) = nearbyLivingEntities(range).asSequence()
            .mapNotNull { it as? Sheep }
            .filter { it.isSheared.not() }
            .forEach {
//                val woolBlocks = Random.nextInt(1, 4) + lootingLevel
//                val itemDrops = ItemStack(Material.WOOL, woolBlocks, it.color.woolData.toShort())
//                it.world.dropItem(it.location, itemDrops)
//                it.isSheared = true
                // TODO: Shear Bow Sheep wool drop
            }

    /**
     * Shears all mooshrooms around this location.
     *
     * @param range
     *          How far from this location to check for mooshrooms.
     * @param lootingLevel
     *          The level of the looting enchantment (0 if not applicable).
     */
    private fun Location.shearMooshrooms(range: Double, lootingLevel: Int = 0) = nearbyLivingEntities(range).asSequence()
            .mapNotNull { it as? MushroomCow }
            .forEach {
                val mushrooms = 5 + lootingLevel
                val mushroomType = if (Random.nextBoolean()) Material.RED_MUSHROOM else Material.BROWN_MUSHROOM
                val itemDrops = ItemStack(mushroomType, mushrooms)
                it.world.dropItem(it.location, itemDrops)
                it.location.spawn(Cow::class)
                it.remove()
            }

    /**
     * Shears all mooshrooms around this location.
     *
     * @param range
     *          How far from this location to check for snow golems.
     */
    private fun Location.shearSnowGolems(range: Double) = nearbyLivingEntities(range).asSequence()
            .mapNotNull { it as? Snowman }
            .filter { it.isDerp }
            .forEach {
                val itemDrops = ItemStack(Material.PUMPKIN, 1)
                it.world.dropItem(it.location, itemDrops)
                it.isDerp = false
            }

    /**
     * Shears all pumpkins around this location.
     *
     * @param range
     *          How far from this location to check for pumpkins.
     * @param lootingLevel
     *          The level of the looting enchantment (0 if not applicable).
     */
    @Suppress("UNUSED_PARAMETER")
    private fun Location.carvePumpkins(range: Double, lootingLevel: Int = 0) {
        // TODO: carving pumpkins has been introduced in 1.13
    }

    companion object {

        /**
         * The shears item used to break blocks.
         */
        private val SHEARS = ItemStack(Material.SHEARS, 1)
    }
}