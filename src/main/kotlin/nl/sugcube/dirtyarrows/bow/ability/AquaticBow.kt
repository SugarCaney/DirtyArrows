package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData

/**
 * Shoots a blob of water.
 *
 * @author SugarCaney
 */
open class AquaticBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.AQUATIC,
        canShootInProtectedRegions = false,
        protectionRange = 1.0,
        costRequirements = listOf(ItemStack(Material.WATER_BUCKET, 1)),
) {

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        val direction = arrow.velocity.copyOf().normalize()
        // Spawn the lava block slightly in front of the player.
        val spawnLocation = arrow.location.add(direction.multiply(1.5))
        player.world.spawnFallingBlock(spawnLocation, MaterialData(Material.WATER)).apply {
            velocity = arrow.velocity
            dropItem = false
        }
    }

    override fun particle(tickNumber: Int) {
        if (tickNumber % 3 == 0) return

        arrows.forEach {
            it.world.playEffect(it.location.copyOf().add(0.5, 0.5, 0.5), Effect.STEP_SOUND, Material.WATER)
        }
    }

    override fun Player.consumeBowItems() {
        if (gameMode == GameMode.CREATIVE) return

        // Empty the lava bucket.
        inventory.firstOrNull { it?.type == Material.WATER }
                ?.let { it.type = Material.BUCKET }
    }
}