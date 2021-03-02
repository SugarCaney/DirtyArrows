package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.showEnderParticle
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots arrows that teleports the player to the arrow location.
 *
 * @author SugarCaney
 */
open class EnderBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.ENDER,
        canShootInProtectedRegions = true,
        costRequirements = listOf(ItemStack(Material.ENDER_PEARL, 1)),
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        player.showEnderParticle()
        player.teleport(arrow.location.copyOf(yaw = player.location.yaw, pitch = player.location.pitch))
        player.showEnderParticle()
        player.playSound(player.location, Sound.ENTITY_ENDERMEN_TELEPORT, 10f, 1f)
    }
}