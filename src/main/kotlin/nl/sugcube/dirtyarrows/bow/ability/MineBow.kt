package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.createExplosion
import nl.sugcube.dirtyarrows.util.nearbyLivingEntities
import nl.sugcube.dirtyarrows.util.showColoredDust
import nl.sugcube.dirtyarrows.util.showSmokeParticle
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots arrows that explode on impact.
 *
 * @author SugarCaney
 */
open class MineBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.MINE,
        canShootInProtectedRegions = false,
        protectionRange = 7.0,
        handleEveryNTicks = 1,
        removeArrow = false,
        costRequirements = listOf(ItemStack(Material.SULPHUR, 3), ItemStack(Material.REDSTONE, 1)),
        description = "Arrows that land turn into mines."
) {

    /**
     * All arrows that act as proximity mines mapped to their birth unix timestamp.
     */
    private val mines = HashMap<Arrow, Long>()

    /**
     * The power of the explosion. TNT is 4.0 for reference.
     */
    val power = config.getDouble("$node.power").toFloat()

    /**
     * Whether the explosion can set blocks on fire.
     */
    val setOnFire = config.getBoolean("$node.set-on-fire")

    /**
     * Whether the explosion can break blocks.
     */
    val breakBlocks = config.getBoolean("$node.break-blocks")

    /**
     * Whether the mines should display a particle (indicating that the arrow is a mine).
     */
    val showLight = config.getBoolean("$node.show-light")

    /**
     * The light is shown every N ticks, if [showLight] is true.
     */
    val lightFrequency = config.getInt("$node.light-frequency")

    /**
     * How close a living entity has to be to the mine for it to explode, in blocks.
     */
    val proximity = config.getDouble("$node.proximity")

    /**
     * How long the mine is active in milliseconds.
     */
    val maximumLifespan = config.getLong("$node.maximum-lifespan")

    init {
        check(power >= 0.0) { "$node.power cannot be negative, got <$power>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        // Only create a mine when a block is hit.
        if (event.hitBlock == null) return

        mines[arrow] = System.currentTimeMillis()
        arrow.pickupStatus = Arrow.PickupStatus.DISALLOWED
    }

    override fun particle(tickNumber: Int) {
        showMineLight(tickNumber)

        arrows.forEach {
            it.showSmokeParticle()
        }
    }

    /**
     * Show a little light indicating that the arrow is a mine.
     */
    private fun showMineLight(tickNumber: Int) {
        if (showLight.not()) return
        if (tickNumber % lightFrequency != 0) return

        mines.keys.forEach {
            it.location.showColoredDust(Color.RED, count = 1)
        }
    }

    override fun effect() {
        mines.entries.removeIf { (mine, birthTime) ->
            mine.tryExploding() || System.currentTimeMillis() - birthTime > maximumLifespan
        }
    }

    /**
     * Explodes when a living entity is in range.
     *
     * @return `true` when the arrow exploded, `false` otherwise.
     */
    private fun Arrow.tryExploding(): Boolean {
        location.nearbyLivingEntities(proximity).firstOrNull() ?: return false
        location.createExplosion(power = power, setFire = setOnFire, breakBlocks = breakBlocks)
        remove()
        return true
    }
}