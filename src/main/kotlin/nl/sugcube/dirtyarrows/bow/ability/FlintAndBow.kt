package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.crossBlocks
import nl.sugcube.dirtyarrows.util.showFlameParticle
import nl.sugcube.dirtyarrows.util.showSmokeParticle
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Sets fire to the 5 cross-tiles when it lands and sets targets on fire.
 *
 * @author SugarCaney
 */
open class FlintAndBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.FLINT,
        canShootInProtectedRegions = false,
        protectionRange = 12.0,
        costRequirements = listOf(ItemStack(Material.FLINT_AND_STEEL, 1)),
        description = "Sets blocks on fire."
) {

    /**
     * A particle will spawn from shot arrows every N ticks.
     */
    val particleEveryNTicks = config.getInt("$node.particle-every-n-ticks")

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.location.crossBlocks().forEach {
            if (it.block.type in FIRE_REPLACABLE) {
                it.block.type = Material.FIRE
                it.showSmokeParticle()
            }
        }
    }

    override fun particle(tickNumber: Int) = arrows.forEach {
        if (tickNumber % particleEveryNTicks == 0) {
            it.showFlameParticle()
        }
    }

    override fun Player.consumeBowItems() {
        if (gameMode == GameMode.CREATIVE) return

        inventory.firstOrNull { it?.type == Material.FLINT_AND_STEEL }?.let { flintAndSteel ->
            flintAndSteel.durability = (flintAndSteel.durability + 5).toShort()

            // When out of dura, remove it (flint and steel has 64 uses).
            if (flintAndSteel.durability > 64) {
                inventory.remove(flintAndSteel)
                playSound(player.location, Sound.ENTITY_ITEM_BREAK, 10f, 1f)
            }
        }
    }

    companion object {

        /**
         * Materials that can be replaced by FlintAndBow's fire.
         */
        private val FIRE_REPLACABLE = EnumSet.of(
                Material.AIR,
                Material.SNOW,
                Material.VINE,
                Material.DEAD_BUSH,
                Material.LONG_GRASS
        )
    }
}