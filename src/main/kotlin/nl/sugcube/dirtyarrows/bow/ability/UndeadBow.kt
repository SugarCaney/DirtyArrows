package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.effect.spawnZombieSwarm
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots arrows that spawns a circle of zombies.
 *
 * @author SugarCaney
 */
open class UndeadBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.UNDEAD,
        canShootInProtectedRegions = false,
        protectionRange = 8.0,
        costRequirements = listOf(ItemStack(Material.ROTTEN_FLESH, 64)),
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.location.spawnZombieSwarm(zombieCount = 16, distance = 5.0)
    }
}