package nl.sugcube.dirtyarrows

import nl.sugcube.dirtyarrows.util.applyColours
import org.bukkit.configuration.file.FileConfiguration


/**
 * Handles the messages that will be sent to the user(s).
 * Needs to be re-initialised when the localisation file updates.
 *
 * @author SugarCaney
 */
class Broadcast(val plugin: DirtyArrows) {

    private val lang: FileConfiguration = plugin.lang

    /**
     * Primary colour for regular messages.
     */
    val colourPrimary: String = lang.getString("colour.primary")?.applyColours() ?: ""

    /**
     * Secondary colour for regular messages.
     */
    val colourSecondary: String = lang.getString("colour.secondary")?.applyColours() ?: ""

    /**
     * Tertiary colour for regular messages.
     */
    val colourTertiary: String = lang.getString("colour.tertiary")?.applyColours() ?: ""

    /**
     * Primary colour for error messages.
     */
    val colourErrorPrimary: String = lang.getString("colour.error-primary")?.applyColours() ?: ""

    /**
     * Secondary colour for error messages.
     */
    val colourErrorSecondary: String = lang.getString("colour.error-secondary")?.applyColours() ?: ""

    /**
     * The message tag for regular messages when not in minigame mode.
     */
    val tagRegular = lang.getString("tags.regular")?.coloured() ?: ""

    /**
     * The message tag for help related messages.
     */
    val tagHelp = lang.getString("tags.help")?.coloured() ?: ""

    /**
     * The message tag for error messages.
     */
    val tagError = lang.getString("tags.error")?.coloured() ?: ""

    /**
     * The message tag used when the plugin is in minigame mode.
     */
    val tagMinigame = lang.getString("tags.minigame")?.coloured() ?: ""

    /**
     * The tag to used for regular messages.
     */
    val tag = if (plugin.isMinigameVersion()) tagMinigame else tagRegular

    /**
     * Applies all formatting templates: tag/colour templates/colour codes.
     */
    private fun String.applyTemplates() = this
        .replace("${'$'}Tag${'$'}", tag)
        .replace("${'$'}TagH${'$'}", tagHelp)
        .replace("${'$'}TagE${'$'}", tagError)
        .replace("${'$'}TagM${'$'}", tagMinigame)
        .coloured()

    /**
     * Applies all colour templates, both the primary/secondary/tertiary colours and the colour codes (&).
     */
    private fun String.coloured() = applyColourTemplates().applyColours()

    /**
     * Applies the primary/secondary/tertiary colour templates to this string.
     * Templates get replaced with the colour codes.
     */
    private fun String.applyColourTemplates() = this
        .replace("${'$'}C1${'$'}", colourPrimary)
        .replace("${'$'}C2${'$'}", colourSecondary)
        .replace("${'$'}C3${'$'}", colourTertiary)
        .replace("${'$'}E1${'$'}", colourErrorPrimary)
        .replace("${'$'}E2${'$'}", colourErrorSecondary)

    /**
     * New version notification message.
     *
     * @param versionNumber
     *          New version number.
     */
    fun newVersionAvailable(versionNumber: String) = lang.getString("plugin.new-version-available")
        ?.applyTemplates()
        ?.format(versionNumber)
        ?: error("No localisation node <plugin.new-version-available> found")

    /**
     * Shows that the configuration has been reloaded.
     */
    fun reloadedConfig() = lang.getString("plugin.reloaded-config")
        ?.applyTemplates()
        ?: error("No localisation node <plugin.reloaded-config> found")

    /**
     * Confirmation message for giving a bow to someone.
     *
     * @param bowName
     *          New version number.
     * @param receiverName
     *          Name of the receiver of the bow.
     */
    fun gaveBow(bowName: String, receiverName: String) = lang.getString("plugin.gave-bow")
        ?.applyTemplates()
        ?.format(bowName, receiverName)
        ?: error("No localisation node <plugin.gave-bow> found")

    /**
     * No permission to use the bow.
     *
     * @param bowName
     *          Name of the bow.
     */
    fun noBowPermission(bowName: String) = lang.getString("plugin.no-bow-permission")
        ?.applyTemplates()
        ?.format(bowName)
        ?: error("No localisation node <plugin.no-bow-permission> found")

    /**
     * The bow does not require ammonution.
     *
     * @param bowName
     *          Name of the bow.
     */
    fun noAmmoRequired(bowName: String) = lang.getString("plugin.no-ammo-required")
        ?.applyTemplates()
        ?.format(bowName)
        ?: error("No localisation node <plugin.no-ammo-required> found")

    /**
     * Give ammo confirmation.
     *
     * @param itemList
     *          The items that were given.
     * @param receiverName
     *          Name of the receiver of the ammo.
     */
    fun gaveAmmo(itemList: String, receiverName: String) = lang.getString("plugin.gave-ammo")
        ?.applyTemplates()
        ?.format(itemList, receiverName)
        ?: error("No localisation node <plugin.gave-ammo> found")

    /**
     * Set position # to coordinate.
     *
     * @param positionNumber
     *          Position number.
     * @param coordinate
     *          Coordinate string.
     */
    fun positionSet(positionNumber: Int, coordinate: String) = lang.getString("region.position-set")
        ?.applyTemplates()
        ?.format(positionNumber, coordinate)
        ?: error("No localisation node <region.position-set> found")

    /**
     * Set position # to coordinate.
     *
     * @param regionCount
     *          The amount of regions.
     * @param regionNameList
     *          A list of region names.
     */
    fun regionsList(regionCount: Int, regionNameList: String) = lang.getString("region.regions-list")
        ?.applyTemplates()
        ?.format(regionCount, regionNameList)
        ?: error("No localisation node <region.regions-list> found")

    /**
     * Show in which region the player is.
     *
     * @param regionName
     *          The name of the region.
     */
    fun inRegion(regionName: String) = lang.getString("region.in-region")
        ?.applyTemplates()
        ?.format(regionName)
        ?: error("No localisation node <region.in-region> found")

    /**
     * You are not in a region.
     */
    fun notInRegion() = lang.getString("region.not-in-region")
        ?.applyTemplates()
        ?: error("No localisation node <region.not-in-region> found")

    /**
     * Removed a region.
     *
     * @param regionName
     *          Name of the removed region.
     */
    fun regionRemoved(regionName: String) = lang.getString("region.region-removed")
        ?.applyTemplates()
        ?.format(regionName)
        ?: error("No localisation node <region.region-removed> found")

    /**
     * Created a region.
     *
     * @param regionName
     *          Name of the created region.
     */
    fun regionCreated(regionName: String) = lang.getString("region.region-created")
        ?.applyTemplates()
        ?.format(regionName)
        ?: error("No localisation node <region.region-created> found")

    /**
     * Teleported to a certain region.
     *
     * @param regionName
     *          Name of the region that has been teleported to.
     */
    fun regionTeleported(regionName: String) = lang.getString("region.region-teleported")
        ?.applyTemplates()
        ?.format(regionName)
        ?: error("No localisation node <region.region-teleported> found")

    /**
     * DirtyArrows enable message for players.
     */
    fun enabled() = lang.getString("info.enabled")
        ?.applyTemplates()
        ?: error("No localisation node <info.enabled> found")

    /**
     * DirtyArrows disable message for players.
     */
    fun disabled() = lang.getString("info.disabled")
        ?.applyTemplates()
        ?: error("No localisation node <info.disabled> found")

    /**
     * Error when not enough xp levels are available.
     *
     * @param levelsRequired
     *          Amount of levels required.
     */
    fun levelsRequired(levelsRequired: Int) = lang.getString("info.levels-required")
        ?.applyTemplates()
        ?.format(levelsRequired)
        ?: error("No localisation node <info.levels-required> found")

    /**
     * Got impacted by a headshot from a different player.
     *
     * @param shooterName
     *          Name of player who made the headshot.
     */
    fun headshotBy(shooterName: String) = lang.getString("info.headshot-by")
        ?.applyTemplates()
        ?.format(shooterName)
        ?: error("No localisation node <info.headshot-by> found")

    /**
     * Made a headshot on a different player.
     *
     * @param targetName
     *          Name of the target player.
     */
    fun headshotOn(targetName: String) = lang.getString("info.headshot-on")
        ?.applyTemplates()
        ?.format(targetName)
        ?: error("No localisation node <info.headshot-on> found")

    /**
     * Disabled in protected region.
     *
     * @param bowName
     *          Name of the bow.
     */
    fun disabledInProtectedRegion(bowName: String) = lang.getString("info.disabled-in-protected-region")
        ?.applyTemplates()
        ?.format(bowName)
        ?: error("No localisation node <info.disabled-in-protected-region> found")

    /**
     * Message to list all required resources.
     *
     * @param resourceList
     *          The list of resources that are required.
     */
    fun notEnoughResources(resourceList: String) = lang.getString("info.not-enough-resources")
        ?.applyTemplates()
        ?.format(resourceList)
        ?: error("No localisation node <info.not-enough-resources> found")

    /**
     * Message to let the player know they defrosted.
     */
    fun defrosted() = lang.getString("info.defrosted")
        ?.applyTemplates()
        ?: error("No localisation node <info.defrosted> found")

    /**
     * Message to let the player know they are frozen.
     */
    fun frozen() = lang.getString("info.frozen")
        ?.applyTemplates()
        ?: error("No localisation node <info.frozen> found")

    /**
     * Message to let the player know they were cursed.
     */
    fun cursed() = lang.getString("info.cursed")
        ?.applyTemplates()
        ?: error("No localisation node <info.cursed> found")

    /**
     * Let know that the curse was lifted.
     */
    fun curseLifted() = lang.getString("info.curse-lifted")
        ?.applyTemplates()
        ?: error("No localisation node <info.curse-lifted> found")

    /**
     * You have to wait N seconds to use the bow again.
     *
     * @param amountOfSecondsLeft
     *          The amount of seconds left with 1 decimal precision.
     * @param bowName
     *          Name of the bow.
     */
    fun cooldown(amountOfSecondsLeft: Double, bowName: String) = lang.getString("info.cooldown")
        ?.applyTemplates()
        ?.format(amountOfSecondsLeft, bowName)
        ?: error("No localisation node <info.cooldown> found")

    /**
     * Let know that the curse was lifted.
     */
    fun noRecord() = lang.getString("info.no-record")
        ?.applyTemplates()
        ?: error("No localisation node <info.no-record> found")

    /**
     * The bow ability is not enabled.
     *
     * @param bowName
     *          The name of the bow that is not enabled.
     */
    fun bowNotEnabled(bowName: String) = lang.getString("error.not-enabled")
        ?.applyTemplates()
        ?.format(bowName)
        ?: error("No localisation node <error.not-enabled> found")

    /**
     * No bow has been specified.
     */
    fun noBowSpecified() = lang.getString("error.no-bow-specified")
        ?.applyTemplates()
        ?: error("No localisation node <error.no-bow-specified> found")

    /**
     * Unknown bow X.
     *
     * @param bowName
     *          The name of the bow that is not known.
     */
    fun unknownBow(bowName: String) = lang.getString("error.unknown-bow")
        ?.applyTemplates()
        ?.format(bowName)
        ?: error("No localisation node <error.unknown-bow> found")

    /**
     * No player has been specified.
     */
    fun noPlayerSpecified() = lang.getString("error.no-player-specified")
        ?.applyTemplates()
        ?: error("No localisation node <error.no-player-specified> found")

    /**
     * No online player X.
     *
     * @param playerName
     *          The name of the player that is not online.
     */
    fun noOnlinePlayer(playerName: String) = lang.getString("error.no-online-player")
        ?.applyTemplates()
        ?.format(playerName)
        ?: error("No localisation node <error.no-online-player> found")

    /**
     * Invalid region name message.
     *
     * @param regionName
     *          The name of the region.
     */
    fun regionInvalidCharacters(regionName: String) = lang.getString("error.region-invalid-characters")
        ?.applyTemplates()
        ?.format(regionName)
        ?: error("No localisation node <error.region-invalid-characters> found")

    /**
     * Region already exists.
     *
     * @param regionName
     *          The name of the region.
     */
    fun regionExists(regionName: String) = lang.getString("error.region-exists")
        ?.applyTemplates()
        ?.format(regionName)
        ?: error("No localisation node <error.region-exists> found")

    /**
     * Could not create region.
     *
     * @param regionName
     *          The name of the region.
     */
    fun couldNotCreateRegion(regionName: String) = lang.getString("error.could-not-create-region")
        ?.applyTemplates()
        ?.format(regionName)
        ?: error("No localisation node <error.could-not-create-region> found")

    /**
     * Failed to reload config and localisation file.
     *
     * @param errorMessage
     *          Error message
     */
    fun reloadFailed(errorMessage: String) = lang.getString("error.reload-failed")
        ?.applyTemplates()
        ?.format(errorMessage)
        ?: error("No localisation node <error.reload-failed> found")

    /**
     * No region has been specified.
     */
    fun noRegionSpecified() = lang.getString("error.no-region-specified")
        ?.applyTemplates()
        ?: error("No localisation node <error.no-region-specified> found")

    /**
     * No region X.
     *
     * @param regionName
     *          The name of the region that does not exist.
     */
    fun noRegion(regionName: String) = lang.getString("error.no-region")
        ?.applyTemplates()
        ?.format(regionName)
        ?: error("No localisation node <error.no-region> found")

    /**
     * You do not have permission to perform this command.
     */
    fun noCommandPermission() = lang.getString("error.no-command-permission")
        ?.applyTemplates()
        ?: error("No localisation node <error.no-command-permission> found")

    /**
     * Only in-game players can perform this command.
     */
    fun onlyInGame() = lang.getString("error.only-in-game")
        ?.applyTemplates()
        ?: error("No localisation node <error.only-in-game> found")

    /**
     * Usage: <example>
     *
     * @param usageExample
     *          Example usage string.
     */
    fun usage(usageExample: String) = lang.getString("error.usage")
        ?.applyTemplates()
        ?.format(usageExample)
        ?: error("No localisation node <error.usage> found")

    /**
     * Fireballs cannot explode near protected regions.
     */
    fun fireballs() = lang.getString("error.fireballs")
        ?.applyTemplates()
        ?: error("No localisation node <error.fireballs> found")

    /**
     * Wither skulls cannot explode near protected regions.
     */
    fun witherSkulls() = lang.getString("error.wither-skulls")
        ?.applyTemplates()
        ?: error("No localisation node <error.wither-skulls> found")

    /**
     * Get the message that shows whether DA is enabled or disabled.
     *
     * @param enabled
     *          Whether DA is enabled (`true`) or disabled (`false`).
     */
    fun enabledMessage(enabled: Boolean = true): String {
        if (plugin.isMinigameVersion()) return ""
        return if (enabled) enabled() else disabled()
    }
}