package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.effect.cutDownTree
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent

/**
 * Shoots arrows that cut down trees.
 *
 * @author SugarCaney
 */
open class WoodmanBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.WOODMAN,
        canShootInProtectedRegions = false,
        protectionRange = 1.0,
        removeArrow = false,
        description = "Tear down trees quickly."
) {

    /**
     * The chance of logs turning into sticks in [0,1].
     */
    val stickChance = config.getDouble("$node.stick-chance")

    /**
     * The chance of logs turning into planks in [0,1].
     */
    val plankChance = config.getDouble("$node.plank-chance")

    init {
        check(stickChance in 0.0..1.0) { "$node.stick-chance must lie between 0 and 1, got <$stickChance>" }
        check(plankChance in 0.0..1.0) { "$node.plank-chance must lie between 0 and 1, got <$plankChance>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val hitBlock = event.hitBlock ?: return
        if (hitBlock.location.cutDownTree(plankChance, stickChance)) {
            arrow.remove()
        }
    }
}