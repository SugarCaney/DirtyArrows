package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.ToolLevel
import nl.sugcube.dirtyarrows.util.forXYZ
import nl.sugcube.dirtyarrows.util.hitBlock
import nl.sugcube.dirtyarrows.util.toolLevel
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.abs

/**
 * Breaks a slice of blocks.
 *
 * @author SugarCaney
 */
open class DrillBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.DRILL,
        canShootInProtectedRegions = false,
        protectionRange = 5.0,
        removeArrow = false,
        description = "Create beautiful tunnels. Requires a pickaxe."
) {

    /**
     * The (square) diameter of the block slices to break.
     */
    val tunnelDiameter = config.getInt("$node.diameter")

    /**
     * How many blocks the tunnel must deviate from the center block.
     */
    private val deviationFromCenter = tunnelDiameter / 2

    init {
        check(tunnelDiameter >= 0) { "$node.diameter cannot be negative, got <$tunnelDiameter>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val hitBlock = arrow.hitBlock()
        val toolLevel = player.maxToolLevel()
        val mineTool = createMineTool(toolLevel)
        val layers = player.layers()

        val dx = abs(arrow.velocity.x)
        val dy = abs(arrow.velocity.y)
        val dz = abs(arrow.velocity.z)

        if (dx > dy && dx > dz) {
            hitBlock.location.breakSlicePerpendicularToXAxis(mineTool, arrow.velocity.x, layers)
        }
        else if (dy > dz) {
            hitBlock.location.breakSlicePerpendicularToYAxis(mineTool, arrow.velocity.y, layers)
        }
        else hitBlock.location.breakSlicePerpendicularToZAxis(mineTool, arrow.velocity.z, layers)

        arrow.remove()
    }

    /**
     * Breaks a slice of blocks perpendicular to the x axis with this location at the center.
     *
     * @param withTool
     *          The tool to use to break the blocks.
     * @param xDirection
     *          Negative for -x direction, +x otherwise.
     * @param layers
     *          How many layers to dig.
     */
    private fun Location.breakSlicePerpendicularToXAxis(withTool: ItemStack, xDirection: Double, layers: Int = 1) {
        val xRange = if (xDirection >= 0) blockX until (blockX + layers) else blockX downTo (blockX - layers - 1)
        forXYZ(xRange, yRange(), zRange()) { x, y, z ->
            world.getBlockAt(x, y, z).breakBlock(withTool)
        }
    }

    /**
     * Breaks a slice of blocks perpendicular to the y axis with this location at the center.
     *
     * @param withTool
     *          The tool to use to break the blocks.
     * @param yDirection
     *          Negative for -y direction, +y otherwise.
     * @param layers
     *          How many layers to dig.
     */
    private fun Location.breakSlicePerpendicularToYAxis(withTool: ItemStack, yDirection: Double, layers: Int = 1) {
        val yRange = if (yDirection >= 0) blockY until (blockY + layers) else (blockY - layers + 1)..blockY
        forXYZ(xRange(), yRange, zRange()) { x, y, z ->
            world.getBlockAt(x, y, z).breakBlock(withTool)
        }
    }

    /**
     * Breaks a slice of blocks perpendicular to the z axis with this location at the center.
     *
     * @param withTool
     *          The tool to use to break the blocks.
     * @param zDirection
     *          Negative for -z direction, +z otherwise.
     * @param layers
     *          How many layers to dig.
     */
    private fun Location.breakSlicePerpendicularToZAxis(withTool: ItemStack, zDirection: Double, layers: Int = 1) {
        val zRange = if (zDirection >= 0) blockZ until (blockZ + layers) else (blockZ - layers + 1)..blockZ
        forXYZ(xRange(), yRange(), zRange) { x, y, z ->
            world.getBlockAt(x, y, z).breakBlock(withTool)
        }
    }

    /**
     * X coordinates to iterate over.
     */
    private fun Location.xRange() = blockX - deviationFromCenter..blockX + deviationFromCenter

    /**
     * Y coordinates to iterate over.
     */
    private fun Location.yRange() = blockY - deviationFromCenter..blockY + deviationFromCenter

    /**
     * Z coordinates to iterate over.
     */
    private fun Location.zRange() = blockZ - deviationFromCenter..blockZ + deviationFromCenter

    /**
     * Breaks this block if allowed.
     */
    private fun Block.breakBlock(tool: ItemStack) {
        if (type !in BLACKLISTED_BLOCKS) {
            breakNaturally(tool)
        }
    }

    /**
     * Creates the tool used to break the blocks.
     */
    private fun createMineTool(toolLevel: ToolLevel?): ItemStack {
        return ItemStack(toolLevel?.pickaxe ?: Material.AIR, 1)
    }

    /**
     * Get the maximum tool level available.
     */
    private fun Player.maxToolLevel() = inventory.asSequence()
            .filter { it?.type in BLOCK_BREAK_TOOLS }
            .mapNotNull { it?.type?.toolLevel }
            .maxByOrNull { it }

    /**
     * The amount of layers to drill.
     *
     * @return The amount of layers the player can drill per shot.
     */
    private fun Player.layers(): Int {
        return bowItem()?.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) ?: return 0
    }

    companion object {

        /**
         * The materials that are tools that can mine blocks.
         */
        private val BLOCK_BREAK_TOOLS = setOf(
                Material.DIAMOND_PICKAXE,
                Material.GOLD_PICKAXE,
                Material.IRON_PICKAXE,
                Material.STONE_PICKAXE,
                Material.WOOD_PICKAXE,
        )

        /**
         * Blocks that cannot be mined.
         */
        private val BLACKLISTED_BLOCKS = setOf(
                Material.BEDROCK,
                Material.OBSIDIAN
        )
    }
}