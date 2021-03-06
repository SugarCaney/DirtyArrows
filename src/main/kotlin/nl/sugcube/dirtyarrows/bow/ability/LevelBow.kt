package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.Effect
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import kotlin.math.max

/**
 * Steals levels from the target (if they have levels).
 *
 * @author SugarCaney
 */
open class LevelBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.LEVEL,
        canShootInProtectedRegions = true,
        description = "Steals levels from the target."
) {

    /**
     * The amount of levels to steal per hit.
     */
    val levelChange = 1

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? Player ?: return
        if (target == player) return
        if (target.level < 1) return

        target.level = max(0, target.level - levelChange)
        player.level = player.level + levelChange

        target.world.playEffect(target.location, Effect.SMOKE, 0)
        player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10f, 1f)
    }
}