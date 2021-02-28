package nl.sugcube.dirtyarrows.region

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.scheduleDelayed
import nl.sugcube.dirtyarrows.util.spawn
import org.bukkit.DyeColor
import org.bukkit.Location
import org.bukkit.entity.Shulker
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Shows a marker at the given location.
 *
 * @param location
 *          The location to visualise.
 * @param seconds
 *          The amount of seconds the visualisation is visible.
 */
fun DirtyArrows.showLocationMarker(location: Location, seconds: Int) {
    location.spawn(Shulker::class).apply {
        addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, seconds * 20, 1, true, false))
        isGlowing = true
        isInvulnerable = true
        isSilent = true
        color = DyeColor.LIME
        setAI(false)

        scheduleDelayed(seconds * 20L) {
            remove()
        }
    }
}

/**
 * Visualises the corners of the region
 *
 * @param region
 *          The region to visualise all 8 corners of.
 * @param seconds
 *          The amount of seconds the visualisation is visible.
 */
fun DirtyArrows.showRegionMarkers(region: Region, seconds: Int) {
    region.allCorners().forEach {
        showLocationMarker(it, seconds)
    }
}

/**
 * Vislualises the currently selected positions.
 *
 * @param location
 *          The location to visualise.
 * @param seconds
 *          The amount of seconds the visualisation is visible.
 */
fun DirtyArrows.showPositionMarkers(seconds: Int) = with(regionManager) {
    getSelection(1)?.let { showLocationMarker(it, seconds) }
    getSelection(2)?.let { showLocationMarker(it, seconds) }
}