package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots a blob of lava.
 *
 * @author SugarCaney
 */
open class MagmaticBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.MAGMATIC,
        canShootInProtectedRegions = false,
        protectionRange = 1.0,
        costRequirements = listOf(ItemStack(Material.LAVA_BUCKET, 1)),
        description = "Shoots lava."
) {

    /**
     * A particle will spawn from shot arrows every N ticks.
     */
    val particleEveryNTicks = config.getInt("$node.particle-every-n-ticks")

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val block = arrow.location.block

        if (block.type == Material.AIR && block.location.isInProtectedRegion(player).not()) {
            arrow.location.block.type = Material.LAVA
            player.world.playSound(arrow.location, Sound.ITEM_BUCKET_EMPTY_LAVA, 1f, 1f)
        }
    }

    override fun particle(tickNumber: Int) {
        if (tickNumber % particleEveryNTicks == 0) return

        arrows.forEach {
            it.world.playEffect(it.location.copyOf().add(0.5, 0.5, 0.5), Effect.STEP_SOUND, Material.MAGMA_BLOCK)
        }
    }

    override fun Player.consumeBowItems() {
        if (gameMode == GameMode.CREATIVE) return

        // Empty the lava bucket.
        inventory.firstOrNull { it?.type == Material.LAVA_BUCKET }
                ?.let { it.type = Material.BUCKET }
    }
}