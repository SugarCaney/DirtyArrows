package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.centreLocation
import nl.sugcube.dirtyarrows.util.showSmokeParticle
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * Spawns temporary blocks on the trajectory.
 *
 * @author SugarCaney
 */
open class BridgeBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.BRIDGE,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        removeArrow = true,
        description = "Creates a temporary bridge.",
        costRequirements = listOf(ItemStack(
                Material.matchMaterial(
                        plugin.config.getString("bridge.material") ?: error("Invalid bridge.material value")
                ) ?: error("No material with node ${plugin.config.getString("bridge.material")}"),
            1)
        )
) {

    /**
     * Maps each placed bridge block to the one who placed the block, compared to the unix timestamp it was placed.
     */
    private val bridgeBlocks = HashMap<Block, Pair<Player, Long>>()

    /**
     * The block material used for creating the bridges.
     */
    val material = Material.matchMaterial(config.getString("$node.material") ?: error("No configuration node $node.material found"))
            ?: error("Illegal $node.material: <${config.getString("$node.material")}>")

    /**
     * After how many milliseconds the blocks must be destroyed.
     */
    val duration = config.getLong("$node.duration")

    /**
     * How long before dissapearance particles must be shown, in milliseconds.
     */
    val notifyRange = config.getLong("$node.notify-range")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        // Give back the initial block material - as it should only be removed for each placed block.
        player.reimburseBowItems()
    }

    override fun effect() {
        placeBlocks()
        removeBlocks()
    }

    /**
     * Places bridge blocks for all arrows.
     */
    private fun placeBlocks() = arrows.forEach {
        if (it.location.isInProtectedRegion(it.shooter as? LivingEntity, showError = false)) return@forEach

        val shooter = it.shooter as? Player ?: return@forEach

        // Downward arrows: place above, Upward arrows: place below.
        val deltaY = if (it.velocity.y < -1) 1.2 else -1.2

        // Because arrows can travel quite quickly, not all blocks can be placed.
        // So look a bit behind. Also prevent the arrow from landing prematurely.
        it.location.add(it.velocity.normalize().multiply(-1.5)).add(0.0, deltaY, 0.0).block.place(shooter)
        it.location.add(it.velocity.normalize().multiply(-1.0)).add(0.0, deltaY, 0.0).block.place(shooter)
        it.location.add(it.velocity.normalize().multiply(-0.5)).add(0.0, deltaY, 0.0).block.place(shooter)
    }

    /**
     * Places this temporary bridge block.
     */
    private fun Block.place(shooter: Player) {
        if (type != Material.AIR) return
        if (location.distance(shooter.location) < 2.0) return
        if (shooter.gameMode != GameMode.CREATIVE && shooter.inventory.contains(material).not()) return

        type = material
        bridgeBlocks[this] = shooter to System.currentTimeMillis()
        shooter.consumeBowItems()
        location.showSmokeParticle()
    }

    /**
     * Removes the expired bridge blocks.
     */
    private fun removeBlocks() = bridgeBlocks.entries.removeIf { (block, playerBirthTimePair) ->
        val (player, birthTime) = playerBirthTimePair

        if (System.currentTimeMillis() - birthTime >= duration) {
            block.world.playEffect(block.centreLocation, Effect.STEP_SOUND, block.type)
            block.type = Material.AIR
            player.reimburseBowItems()
            return@removeIf true
        }

        // Notify when nearly there.
        if (System.currentTimeMillis() - birthTime >= duration - notifyRange && Random.nextInt(10) == 0) {
            block.centreLocation.showSmokeParticle()
        }

        false
    }
}