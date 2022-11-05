package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.createExplosion
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent

/**
 * Kills the target and the player.
 * Both explode as well.
 *
 * @author SugarCaney
 */
open class KamikazeBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.KAMIKAZE,
        canShootInProtectedRegions = false,
        removeArrow = false,
        description = "Target dead, you dead."
) {

    /**
     * Whether the target must explode when they die.
     */
    val targetExplodes = config.getBoolean("$node.target-explodes")

    /**
     * Whether the shooter must explode when they die.
     */
    val shooterExplodes = config.getBoolean("$node.shooter-explodes")

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
        val target = event.hitEntity as? LivingEntity ?: return

        val targetLocation = target.location.clone()
        val playerLocation = player.location.clone()

        target.damage(999999.0, player)
        player.damage(999999.0, player)

        if (targetExplodes) {
            targetLocation.createExplosion(power, setOnFire, breakBlocks)
        }
        if (shooterExplodes) {
            playerLocation.createExplosion(power, setOnFire, breakBlocks)
        }
    }
}