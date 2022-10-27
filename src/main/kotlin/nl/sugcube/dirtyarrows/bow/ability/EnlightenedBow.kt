package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots arrows that place torches.
 *
 * @author SugarCaney
 */
open class EnlightenedBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.ENLIGHTENED,
        canShootInProtectedRegions = false,
        protectionRange = 2.0,
        costRequirements = listOf(ItemStack(Material.TORCH, 1), ItemStack(Material.SOUL_TORCH, 1)),
        description = "Places torches."
) {

    /**
     * TORCH or SOUL_TORCH for each arrow.
     */
    private val torchTypes = HashMap<Arrow, Material>()

    // Hack to get a parameter to reimburseBowItems.
    private var lastTorchType: Material = Material.TORCH

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        lastTorchType = player.firstHeldTorchType()

        arrow.location.apply {
            if (block.type != Material.AIR) {
                player.reimburseBowItems()
                return
            }

            // When the torch has something to stand on, all is fine. However, ...
            if (block.onSolid()) {
                block.type = lastTorchType
                showFlameParticle()
                return
            }

            // ... if there isn't, find a wall face (note: torches face opposite of attached face).
            // But first check if there is a face to attach to.
            if (attachedBlockLocations().none { it.block.type.isSolid }) {
                player.reimburseBowItems()
                return
            }

            // Then find a face.
            // Notice that the facing direction is opposite to the solid direction.
            val blockFace = when {
                north().block.type.isSolid -> BlockFace.SOUTH
                south().block.type.isSolid -> BlockFace.NORTH
                east().block.type.isSolid -> BlockFace.WEST
                west().block.type.isSolid -> BlockFace.EAST
                else -> BlockFace.UP
            }

            block.type = if (lastTorchType == Material.SOUL_TORCH) Material.SOUL_WALL_TORCH else Material.WALL_TORCH
            block.setBlockData(block.blockData.useAs<BlockData, Directional> {
                it.facing = blockFace
            }, true)

            showFlameParticle()
        }
    }

    /**
     * Get which type of torch to place.
     */
    private fun Player.firstHeldTorchType(): Material {
        // First look in offhand,
        // then look through the rest of the inventory.
        return when (val torch = itemInOffHand.type) {
            Material.TORCH, Material.SOUL_TORCH -> torch
            else -> inventory.contents.firstOrNull {
                it?.type == Material.TORCH || it?.type == Material.SOUL_TORCH
            }?.type ?: Material.TORCH
        }
    }

    // There is some code duplication here, the system is not made for possibly only one item
    // from the cost requirements. The enlightened bow is the only bow with such a choice (torch or soul torch)
    // and I'm too 'lazy' to refactor everything, hence why this duplication is here.
    // When there are more bows with such a requirement, then refactor.

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        lastTorchType = player.firstHeldTorchType()
    }

    override fun Player.meetsResourceRequirements(showError: Boolean): Boolean {
        // Make sure to match online one item (torch or soul torch) instead of all items from the
        // cost requirements.

        val survival = gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE
        val meetsRequirements = survival.not() || costRequirements.any {
            inventory.checkForItem(it)
        }

        if (showError && meetsRequirements.not()) {
            sendMessage(Broadcast.NOT_ENOUGH_RESOURCES.format(costRequirements.joinToString(", or ") {
                "${it.type.name.toLowerCase()} (x${it.amount})"
            }))
        }

        return meetsRequirements
    }

    override fun Player.consumeBowItems() {
        val recentlyRemoved = ArrayList<ItemStack>(costRequirements.size)

        listOf(ItemStack(lastTorchType, 1)).forEach { item ->
            // Find the item with the correct material. removeItem won't work when the item has
            // item data.
            val eligibleItem = inventory.asSequence()
                .filterNotNull()
                .firstOrNull { it.type == item.type && it.amount >= item.amount }
                ?: return@forEach

            val toRemove = eligibleItem.clone().apply {
                amount = item.amount
            }
            if (gameMode != GameMode.CREATIVE) {
                inventory.removeIncludingData(toRemove)
            }
            recentlyRemoved.add(toRemove)
        }

        mostRecentItemsConsumed[this] = recentlyRemoved
    }

    override fun Player.reimburseBowItems() {
        if (player != null && player!!.gameMode != GameMode.CREATIVE) {
            inventory.addItem(ItemStack(lastTorchType, 1))
        }
    }
}