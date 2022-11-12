package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.scheduleRemoval
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import kotlin.random.Random

/**
 * Has powerful shots, but also has a chance to damage yourself.
 *
 * @author SugarCaney
 */
open class RouletteBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.ROULETTE,
        canShootInProtectedRegions = true,
        description = "Often shoots with great power, sometimes damages you."
) {

    /**
     * Multiplier for damage inflicted by arrows shot by the Roulette Bow.
     */
    val damageMultiplier = config.getDouble("$node.damage-multiplier")

    /**
     * The chance of inflicting damage to the yielder when shot.
     */
    val selfDamageChance = config.getDouble("$node.self-damage-chance")

    /**
     * How much damage to inflict to the yielder when out of luck.
     */
    val selfDamageAmount = config.getDouble("$node.self-damage-amount")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        // Self-damage
        if (Random.nextDouble() < selfDamageChance) {
            player.damage(selfDamageAmount, player)

            arrow.scheduleRemoval(plugin)
            unregisterArrow(arrow)
        }
        // Power shots.
        else {
            arrow.damage = arrow.damage * damageMultiplier
        }
    }
}