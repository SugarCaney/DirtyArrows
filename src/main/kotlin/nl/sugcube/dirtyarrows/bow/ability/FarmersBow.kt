package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.CropState
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.material.Crops

/**
 * Turns blocks into farmland.
 * Also turns coarse dirt back to regular dirt.
 * Plants seeds from the off-hand.
 *
 * @author SugarCaney
 */
open class FarmersBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.FARMERS,
        canShootInProtectedRegions = false,
        protectionRange = 10.0,
        removeArrow = true,
        description = "Turns soil into farmland."
) {

    /**
     * Maps each arrow to (power level of the bow, unix timestamp of spawning).
     */
    private val arrowMap = HashMap<Arrow, Pair<Int, Long>>()

    /**
     * How many blocks from the arrow soil will be turned into farmland.
     */
    val range = config.getInt("$node.range")

    /**
     * How much the range increases per power level, in blocks.
     */
    val rangeIncrement = config.getInt("$node.range-increment")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        val powerLevel = player.bowItem()?.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) ?: 0
        arrowMap[arrow] = powerLevel to System.currentTimeMillis()
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val powerLevel = arrowMap[arrow]?.first ?: 0
        val blocks = range + powerLevel * rangeIncrement
        val hitBlock = event.hitBlock ?: return

        // When a block that is hit that is soil, turn it into soil first.
        // Only create soil when the hit block is not soil! Otherwise you could mess up farm shapes.
        // Planting seeds/harvesting crops could be wanted without modifying the soil.
        if (hitBlock.type != Material.FARMLAND) {
            arrow.location.createSoil(blocks)
        }

        arrow.location.plantSeeds(blocks, player)
        arrow.location.harvestCrops(blocks)
    }

    /**
     * Turns all `blocks` blocks around this location into soil.
     *
     * @param blocks
     *          The range around the location that must be turned into soil.
     */
    private fun Location.createSoil(blocks: Int) {
        forXYZ(-blocks..blocks) { dx, dy, dz ->
            val block = world?.getBlockAt(blockX + dx, blockY + dy, blockZ + dz) ?: return@forXYZ

            if (block.restoreCoarseDirt()) return@forXYZ
            if (block.type !in APPLICABLE_SOIL_MATERIALS) return@forXYZ

            val blockAbove = block.getRelative(BlockFace.UP, 1)

            if (blockAbove.type in REMOVABLE_CROPS) {
                blockAbove.breakNaturally()
            }

            if (blockAbove.type == Material.AIR) {
                block.type = Material.FARMLAND
            }
        }
    }

    /**
     * Plants seeds at soil around this location.
     *
     * @param blocks
     *          How many blocks around this location to plant seeds.
     * @param player
     *          The player that used the farmer's bow.
     */
    private fun Location.plantSeeds(blocks: Int, player: Player) {
        forXYZ(-blocks..blocks) { dx, dy, dz ->
            val block = world?.getBlockAt(blockX + dx, blockY + dy, blockZ + dz) ?: return@forXYZ
            val blockAbove = block.getRelative(BlockFace.UP, 1)
            blockAbove.plantSeed(player)
        }
    }

    /**
     * Plants seeds in the player's offhand on this block.
     */
    private fun Block.plantSeed(player: Player) {
        if (type != Material.AIR) return
        if (getRelative(BlockFace.DOWN, 1).type != Material.FARMLAND) return

        val seeds = player.itemInOffHand
        if (seeds.type !in SEEDS || (seeds.amount <= 0 && player.gameMode != GameMode.CREATIVE)) return

        if (player.gameMode != GameMode.CREATIVE) {
            player.inventory.setItemInOffHand(ItemStack(seeds.type, seeds.amount - 1))
        }

        type = SEEDS[seeds.type] ?: return
    }

    /**
     * Harvest all crops that have finished growing.
     *
     * @param lootingLevel
     *          The looting level on the farmer's bow used.
     */
    private fun Location.harvestCrops(blocks: Int, lootingLevel: Int = 0) {
        forXYZ(-blocks..blocks) { dx, dy, dz ->
            val block = world?.getBlockAt(blockX + dx, blockY + dy, blockZ + dz) ?: return@forXYZ

            // Harvest melons/pumpkins.
            if (block.type == Material.MELON || block.type == Material.PUMPKIN) {
                val item = block.type.fortuneDrops(lootingLevel).first()
                block.type = Material.AIR
                block.world.dropItem(block.centreLocation, item)
                return@forXYZ
            }

            when (block.type) {
                Material.BEETROOT, Material.WHEAT, Material.CARROT, Material.POTATO -> {
                    val crops = block.state.data as Crops
                    if (crops.state == CropState.RIPE) {
                        block.breakNaturally()
                    }
                }
                else -> Unit
            }
        }
    }

    companion object {

        /**
         * All materials that can be turned into farmland.
         */
        private val APPLICABLE_SOIL_MATERIALS = setOf(
                Material.DIRT,
                Material.GRASS,
                Material.GRASS_PATH
        )

        /**
         * When these materials are above farmland, they will be removed.
         */
        private val REMOVABLE_CROPS = setOf(
                Material.TALL_GRASS,
                Material.LARGE_FERN,
                Material.SUNFLOWER,
                Material.PEONY,
                Material.ROSE_BUSH,
                Material.LILAC,
                Material.SNOW
        )

        /**
         * Maps all plantable seeds to their planted material.
         */
        private val SEEDS = mapOf(
                Material.WHEAT_SEEDS to Material.WHEAT,
                Material.BEETROOT_SEEDS to Material.BEETROOT,
                Material.PUMPKIN_SEEDS to Material.PUMPKIN_STEM,
                Material.MELON_SEEDS to Material.MELON_STEM,
                Material.CARROT to Material.CARROTS,
                Material.POTATO to Material.POTATOES
        )
    }
}