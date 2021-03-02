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
 * Shoots arrows that spawn a chicken on impact.
 *
 * @author SugarCaney
 */
open class CluckyBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.CLUCKY,
        canShootInProtectedRegions = false,
        protectionRange = 3.0,
        costRequirements = listOf(ItemStack(Material.EGG, 1)),
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.world.spawnEntity(arrow.location, EntityType.CHICKEN)
        player.playSound(arrow.location, Sound.ENTITY_CHICKEN_HURT, 10f, 1f)
    }
}