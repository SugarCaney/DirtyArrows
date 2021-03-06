package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots arrows that spawns a swarm of bats at the location of impact.
 *
 * @author SugarCaney
 */
open class BattyBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.BATTY,
        canShootInProtectedRegions = false,
        protectionRange = 5.0,
        costRequirements = listOf(ItemStack(Material.ROTTEN_FLESH, 6)),
        description = "Spawn a swarm of bats on impact."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        repeat(10) {
            arrow.world.spawnEntity(arrow.location, EntityType.BAT)
        }
        player.playSound(player.location, Sound.ENTITY_BAT_AMBIENT, 10f, 1f)
    }
}