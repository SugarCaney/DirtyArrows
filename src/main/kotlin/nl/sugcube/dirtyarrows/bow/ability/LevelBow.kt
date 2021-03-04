package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.Effect
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent

/**
 * Steals 1 level from the target (if they have levels).
 *
 * @author SugarCaney
 */
open class LevelBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.LEVEL,
        canShootInProtectedRegions = true
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? Player ?: return
        if (target == player) return
        if (target.level < 1) return

        target.level = target.level - 1
        player.level = player.level + 1

        target.world.playEffect(target.location, Effect.SMOKE, 0)
        player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10f, 1f)
    }
}