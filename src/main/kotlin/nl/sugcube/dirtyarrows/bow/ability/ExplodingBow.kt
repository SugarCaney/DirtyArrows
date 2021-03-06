package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.createExplosion
import nl.sugcube.dirtyarrows.util.showSmokeParticle
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
open class ExplodingBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.EXPLODING,
        canShootInProtectedRegions = false,
        protectionRange = 5.0,
        costRequirements = listOf(ItemStack(Material.TNT, 1)),
        description = "Explosive arrows."
) {

    /**
     * The power of the explosion. TNT is 4.0 for reference.
     */
    val power = config.getDouble("$node.power").toFloat()

    /**
     * Whether the explosion can set blocks on fire.
     */
    val setOnFire = config.getBoolean("$node.set-on-fire")

    /**
     * Whether to break the broken blocks.
     */
    val breakBlocks = config.getBoolean("$node.break-blocks")

    init {
        check(power >= 0.0) { "$node.power cannot be negative, got <$power>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.location.createExplosion(power = power, setFire = setOnFire, breakBlocks = breakBlocks)
    }

    override fun particle(tickNumber: Int) = arrows.forEach {
        it.showSmokeParticle()
    }
}