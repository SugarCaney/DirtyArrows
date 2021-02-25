package nl.sugcube.dirtyarrows

import org.bukkit.plugin.java.JavaPlugin
import nl.sugcube.dirtyarrows.listener.ArrowListener
import nl.sugcube.dirtyarrows.listener.EnchantmentListener
import nl.sugcube.dirtyarrows.listener.PlayerJoinListener
import nl.sugcube.dirtyarrows.listener.PlayerDamageListener
import nl.sugcube.dirtyarrows.listener.EntityListener
import nl.sugcube.dirtyarrows.region.RegionManager
import nl.sugcube.dirtyarrows.ability.Iron
import nl.sugcube.dirtyarrows.ability.CurseListener
import nl.sugcube.dirtyarrows.ability.FrozenListener
import nl.sugcube.dirtyarrows.listener.AnvilListener
import org.bukkit.entity.Projectile
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.entity.Player
import org.bukkit.entity.FallingBlock
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.Material
import nl.sugcube.dirtyarrows.ability.Airstrike
import nl.sugcube.dirtyarrows.ability.Airship
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.enchantments.Enchantment
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Entity
import org.bukkit.util.Vector
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.math.ceil
import kotlin.math.floor

/**
 * @author SugarCaney
 */
class DirtyArrows : JavaPlugin() {

    private val log = Logger.getLogger("Minecraft")
    var configFile = File(dataFolder.toString() + File.separator + "config.yml")
    var al = ArrowListener(this)
    var el = EnchantmentListener(this)
    var pjl = PlayerJoinListener(this)
    var pdl = PlayerDamageListener(this)
    var enl = EntityListener(this)
    var help = Help(this)
    @JvmField var rm = RegionManager(this)
    var iron = Iron(this)
    var curse = CurseListener(this)
    var frozenListener = FrozenListener(this)
    var anvilListener = AnvilListener(this)
    @JvmField var activated: MutableList<String> = ArrayList()
    @JvmField var slow: MutableList<Projectile> = ArrayList()
    @JvmField var airstrike: MutableList<Projectile> = ArrayList()
    @JvmField var airship: MutableList<Projectile> = ArrayList()
    @JvmField var noFallDamage: MutableList<UUID> = ArrayList()
    @JvmField var slowVec: MutableList<Vector> = ArrayList()
    @JvmField var cursed = ConcurrentHashMap<Entity, Int>()
    @JvmField var frozen = ConcurrentHashMap<Entity, Int>()
    @JvmField var noInteract: MutableList<Player> = ArrayList()
    @JvmField var particleExploding: MutableList<Projectile> = ArrayList()
    @JvmField var particleFire: MutableList<Projectile> = ArrayList()
    @JvmField var particleLava: MutableList<FallingBlock> = ArrayList()
    @JvmField var particleWater: MutableList<FallingBlock> = ArrayList()
    @JvmField var ice: MutableList<Int> = ArrayList()
    @JvmField var iceParticle: MutableList<Projectile> = ArrayList()
    @JvmField var anvils = ConcurrentHashMap<FallingBlock, Int>()

    val version: String
        get() = description.version
    private var data: FileConfiguration? = null
    private var dataFile: File? = null

    override fun onEnable() {
        /*
		 * Load config.yml and data.yml
		 */
        val file = File(dataFolder.toString() + File.separator + "config.yml")
        if (!file.exists()) {
            try {
                config.options().copyDefaults(true)
                saveConfig()
                logger.info("Generated config.yml succesfully!")
            } catch (e: Exception) {
                logger.info("Failed to generate config.yml!")
            }
        }
        val df = File(dataFolder.toString() + File.separator + "data.yml")
        if (!df.exists()) {
            try {
                reloadData()
                saveData()
                logger.info("Generated data.yml succesfully!")
            } catch (e: Exception) {
                logger.info("Failed to generate data.yml!")
            }
        }
        val pm = server.pluginManager
        pm.registerEvents(al, this)
        pm.registerEvents(el, this)
        pm.registerEvents(enl, this)
        pm.registerEvents(pjl, this)
        pm.registerEvents(pdl, this)
        pm.registerEvents(iron, this)
        pm.registerEvents(curse, this)
        pm.registerEvents(frozenListener, this)
        pm.registerEvents(anvilListener, this)
        val arrow =
            ShapedRecipe(ItemStack(Material.ARROW, config.getInt("arrow-recipe-amount"))).shape(" * ", " # ", " % ")
                .setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK)
                .setIngredient('%', Material.FEATHER)
        val arrow2 =
            ShapedRecipe(ItemStack(Material.ARROW, config.getInt("arrow-recipe-amount"))).shape("*  ", "#  ", "%  ")
                .setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK)
                .setIngredient('%', Material.FEATHER)
        val arrow3 =
            ShapedRecipe(ItemStack(Material.ARROW, config.getInt("arrow-recipe-amount"))).shape("  *", "  #", "  %")
                .setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK)
                .setIngredient('%', Material.FEATHER)
        server.addRecipe(arrow)
        server.addRecipe(arrow2)
        server.addRecipe(arrow3)
        server.scheduler.scheduleSyncRepeatingTask(this, Timer(this), 0, 1)
        server.scheduler.scheduleSyncRepeatingTask(this, Airstrike(this), 5, 5)
        server.scheduler.scheduleSyncRepeatingTask(this, Particles(this), 2, 2)
        server.scheduler.scheduleSyncRepeatingTask(this, Airship(this), 2, 2)
        server.scheduler.scheduleSyncRepeatingTask(this, iron, 5, 5)
        server.scheduler.scheduleSyncRepeatingTask(this, curse, 20, 20)
        server.scheduler.scheduleSyncRepeatingTask(this, frozenListener, 20, 20)
        rm.loadRegions()
        log.info("[DirtyArrows] DirtyArrows has been enabled!")
        log.info("[DirtyArrows] 42 Bastards have been loaded")
        log.info("[DirtyArrows] 3 recipes have been loaded")

        /*
		 * Check for updatese
		 */if (this.config.getBoolean("updates.check-for-updates")) {
            val uc = Update(57131, description.version)
            if (uc.query()) {
                Bukkit.getConsoleSender().sendMessage(
                    ChatColor.GREEN.toString() + "[DirtyArrows] A new version of DirtyArrows is " +
                            "avaiable! Get it at the BukkitDev page!"
                )
            } else {
                Bukkit.getConsoleSender().sendMessage("[DirtyArrows] DirtyArrows is up-to-date!")
            }
        }

        //Plugin metrics
        if (this.config.getBoolean("metrics.enabled")) {
            try {
                val metrics = Metrics(this)
                metrics.start()
                logger.info("Started Metrics.")
            } catch (e: Exception) {
                logger.info("Failed starting Metrics.")
            }
        } else {
            logger.info("Didn't start Metrics (disabled in the configuration).")
        }
    }

    override fun onDisable() {
        rm.saveRegions()
        log.info("[DirtyArrows] DirtyArrows has been disabled!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (label.equals("da", ignoreCase = true) || label.equals("dirtyarrows", ignoreCase = true)) {
            if (sender is Player) {
                if (sender.hasPermission("dirtyarrows")) {
                    if (args.isEmpty()) {
                        if (sender.hasPermission("dirtyarrows")) {
                            if (activated.contains(sender.uniqueId.toString())) {
                                activated.remove(sender.uniqueId.toString())
                                sender.sendMessage(Message.getEnabled(false))
                            } else {
                                activated.add(sender.uniqueId.toString())
                                sender.sendMessage(Message.getEnabled(true))
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED.toString() + "[!!] You don't have permission to perform this command!")
                        }
                    } else if (args[0].equals("give", ignoreCase = true)) {
                        if (sender.hasPermission("dirtyarrows.admin")) {
                            if (args.size >= 3) {
                                val p = args[1]
                                val spec = if (args.size >= 4) {
                                    args[3].equals("ench", ignoreCase = true)
                                } else {
                                    false
                                }
                                try {
                                    val id = args[2].toInt()
                                    when (p) {
                                        "@a" -> for (pl in Bukkit.getOnlinePlayers()) {
                                            giveBastard(pl, id, spec)
                                        }
                                        "@r" -> {
                                            val ran = Random()
                                            val players = Util.getOnlinePlayers()
                                            giveBastard(players[ran.nextInt(players.size)], id, spec)
                                        }
                                        else -> giveBastard(Bukkit.getPlayer(p), id, spec)
                                    }
                                } catch (e: Exception) {
                                    val id = args[2]
                                    when (p) {
                                        "@a" -> for (pl in Bukkit.getOnlinePlayers()) {
                                            giveBastard(pl, id, spec)
                                        }
                                        "@r" -> {
                                            val ran = Random()
                                            val players = Util.getOnlinePlayers()
                                            giveBastard(players[ran.nextInt(players.size)], id, spec)
                                        }
                                        else -> giveBastard(Bukkit.getPlayer(p), id, spec)
                                    }
                                }
                            } else {
                                sender.sendMessage(Methods.setColours("&c[!!] Usage: /da give <player> <id>"))
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED.toString() + "[!!] You don't have permission to perform this command!")
                        }
                    } else if (args[0].equals("register", ignoreCase = true)) {
                        if (sender.hasPermission("dirtyarrows.admin")) {
                            if (args.size >= 2) {
                                val region = rm.createRegion(args[1])
                                if (region != null) {
                                    sender.sendMessage(
                                        ChatColor.YELLOW.toString() + "Region " + ChatColor.GREEN + args[1] +
                                                ChatColor.YELLOW + " has been created!"
                                    )
                                } else {
                                    sender.sendMessage(ChatColor.RED.toString() + "[!!] Could not create the region!")
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED.toString() + "[!!] Usage: /da register <regionName>")
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED.toString() + "[!!] You don't have permission to perform this command!")
                        }
                    } else if (args[0].equals("remove", ignoreCase = true)) {
                        if (sender.hasPermission("dirtyarrows.admin")) {
                            if (args.size >= 2) {
                                val region = rm.getRegionByName(args[1])
                                if (region != null) {
                                    rm.removeRegion(region.name)
                                    sender.sendMessage(
                                        ChatColor.YELLOW.toString() + "Region " + ChatColor.GREEN + region.name +
                                                ChatColor.YELLOW + " has been removed!"
                                    )
                                } else {
                                    sender.sendMessage(ChatColor.RED.toString() + "[!!] Could not remove the region!")
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED.toString() + "[!!] Usage: /da remove <regionName>")
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED.toString() + "[!!] You don't have permission to perform this command!")
                        }
                    } else if (args.size == 1) {
                        if (sender.hasPermission("dirtyarrows.admin")) {
                            /*
							 * RELOAD
							 */
                            if (args[0].equals("reload", ignoreCase = true)) {
                                try {
                                    reloadConfiguration()
                                    sender.sendMessage(
                                        ChatColor.GREEN.toString() + "[DirtyArows]" + ChatColor.YELLOW +
                                                " Reloaded config.yml"
                                    )
                                } catch (e: Exception) {
                                    sender.sendMessage(ChatColor.RED.toString() + "[DirtyArows] " + ChatColor.YELLOW + "Reload failed")
                                }
                            } else if (args[0].equals("pos1", ignoreCase = true)) {
                                val loc = sender.location
                                rm.setSelection(1, loc)
                                sender.sendMessage(
                                    ChatColor.GREEN.toString() + "Position 1" + ChatColor.YELLOW + " has been set" +
                                            " to " + ChatColor.GREEN + "[x:" + floor(loc.x) + ",y:" +
                                            ceil(loc.y) + ",z:" + ceil(loc.z) + "]"
                                )
                            } else if (args[0].equals("pos2", ignoreCase = true)) {
                                val loc = sender.location
                                rm.setSelection(2, loc)
                                sender.sendMessage(
                                    ChatColor.GREEN.toString() + "Position 2" + ChatColor.YELLOW + " has been set" +
                                            " to " + ChatColor.GREEN + "[x:" + floor(loc.x) + ",y:" +
                                            ceil(loc.y) + ",z:" + ceil(loc.z) + "]"
                                )
                            } else if (args[0].equals("list", ignoreCase = true)) {
                                val regions = rm.allNames
                                var chat = ChatColor.YELLOW.toString() + "Regions (" + regions.size + "): "
                                for (i in regions.indices) {
                                    val name = regions[i]
                                    chat += if (i == regions.size - 1) {
                                        ChatColor.GREEN.toString() + name
                                    } else {
                                        ChatColor.GREEN.toString() + name + ChatColor.YELLOW + ", "
                                    }
                                }
                                sender.sendMessage(chat)
                            } else if (args[0].equals("check", ignoreCase = true)) {
                                val region = rm.isWithinARegionMargin(sender.location, 1)
                                if (region == null) {
                                    sender.sendMessage(
                                        ChatColor.YELLOW.toString() + "You are currently " + ChatColor.RED + "not" +
                                                ChatColor.YELLOW + " in a region."
                                    )
                                } else {
                                    sender.sendMessage(
                                        ChatColor.YELLOW.toString() + "You are currently in region " + ChatColor.GREEN +
                                                region.name
                                    )
                                }
                            } else {
                                Help.showMainHelpPage1(sender)
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED.toString() + "[!!] You don't have permission to perform this command!")
                        }
                    } else if (args.size > 1) {
                        if (args[1].equals("2", ignoreCase = true)) {
                            Help.showMainHelpPage2(sender)
                        } else if (args[1].equals("3", ignoreCase = true)) {
                            Help.showMainHelpPage3(sender)
                        } else if (args[1].equals("4", ignoreCase = true)) {
                            Help.showMainHelpPage4(sender)
                        } else if (args[1].equals("5", ignoreCase = true)) {
                            Help.showMainHelpPage5(sender)
                        } else if (args[1].equals("6", ignoreCase = true)) {
                            Help.showMainHelpPage6(sender)
                        } else if (args[1].equals("7", ignoreCase = true)) {
                            Help.showMainHelpPage7(sender)
                        } else if (args[1].equals("admin", ignoreCase = true)) {
                            if (sender.hasPermission("dirtyarrows.admin")) {
                                sender.sendMessage(
                                    ">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v2.7" +
                                            ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<"
                                )
                                sender.sendMessage(
                                    ChatColor.GOLD.toString() + "/da give <player|@a|@r> <ID|baseName> [ench] " + ChatColor.WHITE +
                                            "Gives someone a bow."
                                )
                                sender.sendMessage(
                                    ChatColor.GOLD.toString() + "/da list " + ChatColor.WHITE + "Shows a list of all " +
                                            "registered regions."
                                )
                                sender.sendMessage(
                                    ChatColor.GOLD.toString() + "/da check " + ChatColor.WHITE + "Check in which " +
                                            "region you are."
                                )
                                sender.sendMessage(ChatColor.GOLD.toString() + "/da pos1 " + ChatColor.WHITE + "Set position 1.")
                                sender.sendMessage(ChatColor.GOLD.toString() + "/da pos2 " + ChatColor.WHITE + "Set position 2.")
                                sender.sendMessage(
                                    ChatColor.GOLD.toString() + "/da register <name> " + ChatColor.WHITE +
                                            "Register a region."
                                )
                                sender.sendMessage(
                                    ChatColor.GOLD.toString() + "/da remove <name> " + ChatColor.WHITE +
                                            "Remove a region."
                                )
                                sender.sendMessage(
                                    ChatColor.GOLD.toString() + "/da reload " + ChatColor.WHITE +
                                            "Reloads the config.yml"
                                )
                                sender.sendMessage(
                                    ">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page ADMIN" +
                                            ChatColor.RED + " /da help <#|admin>" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<"
                                )
                            } else {
                                Help.showMainHelpPage1(sender)
                            }
                        } else {
                            Help.showMainHelpPage1(sender)
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + "[!!] You don't have permission to perform this command")
                }
            } else {
                println("Only players can perform this command!")
            }
        }
        return false
    }

    fun isActivated(p: Player): Boolean {
        return if (MINIGAME_VERSION) {
            true
        } else {
            activated.contains(p.uniqueId.toString())
        }
    }

    fun reloadConfiguration() {
        reloadConfig()
        server.resetRecipes()
        val arrow =
            ShapedRecipe(ItemStack(Material.ARROW, config.getInt("arrow-recipe-amount"))).shape(" * ", " # ", " % ")
                .setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK)
                .setIngredient('%', Material.FEATHER)
        val arrow2 =
            ShapedRecipe(ItemStack(Material.ARROW, config.getInt("arrow-recipe-amount"))).shape("*  ", "#  ", "%  ")
                .setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK)
                .setIngredient('%', Material.FEATHER)
        val arrow3 =
            ShapedRecipe(ItemStack(Material.ARROW, config.getInt("arrow-recipe-amount"))).shape("  *", "  #", "  %")
                .setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK)
                .setIngredient('%', Material.FEATHER)
        server.addRecipe(arrow)
        server.addRecipe(arrow2)
        server.addRecipe(arrow3)
    }

    fun giveBastard(p: Player, name: String?, spec: Boolean) {
        val `is` = ItemStack(Material.BOW, 1)
        val im = `is`.itemMeta
        when (name) {
            "exploding" -> givePlayerBastard("exploding.name", im, `is`, p, spec)
            "lightning" -> givePlayerBastard("lightning.name", im, `is`, p, spec)
            "clucky" -> givePlayerBastard("clucky.name", im, `is`, p, spec)
            "ender" -> givePlayerBastard("ender.name", im, `is`, p, spec)
            "oak" -> givePlayerBastard("oak.name", im, `is`, p, spec)
            "spruce" -> givePlayerBastard("spruce.name", im, `is`, p, spec)
            "birch" -> givePlayerBastard("birch.name", im, `is`, p, spec)
            "jungle" -> givePlayerBastard("jungle.name", im, `is`, p, spec)
            "batty" -> givePlayerBastard("batty.name", im, `is`, p, spec)
            "nuclear" -> givePlayerBastard("nuclear.name", im, `is`, p, spec)
            "enlightened" -> givePlayerBastard("enlightened.name", im, `is`, p, spec)
            "ranged" -> givePlayerBastard("ranged.name", im, `is`, p, spec)
            "machine" -> givePlayerBastard("machine.name", im, `is`, p, spec)
            "poisonous" -> givePlayerBastard("poisonous.name", im, `is`, p, spec)
            "disorienting" -> givePlayerBastard("disorienting.name", im, `is`, p, spec)
            "swap" -> givePlayerBastard("swap.name", im, `is`, p, spec)
            "draining" -> givePlayerBastard("draining.name", im, `is`, p, spec)
            "flintand" -> givePlayerBastard("flintand.name", im, `is`, p, spec)
            "disarming" -> givePlayerBastard("disarming.name", im, `is`, p, spec)
            "wither" -> givePlayerBastard("wither.name", im, `is`, p, spec)
            "firey" -> givePlayerBastard("firey.name", im, `is`, p, spec)
            "slow" -> givePlayerBastard("slow.name", im, `is`, p, spec)
            "level" -> givePlayerBastard("level.name", im, `is`, p, spec)
            "undead" -> givePlayerBastard("undead.name", im, `is`, p, spec)
            "woodman" -> givePlayerBastard("woodman.name", im, `is`, p, spec)
            "starvation" -> givePlayerBastard("starvation.name", im, `is`, p, spec)
            "multi" -> givePlayerBastard("multi.name", im, `is`, p, spec)
            "bomb" -> givePlayerBastard("bomb.name", im, `is`, p, spec)
            "drop" -> givePlayerBastard("drop.name", im, `is`, p, spec)
            "airstrike" -> givePlayerBastard("airstrike.name", im, `is`, p, spec)
            "magmatic" -> givePlayerBastard("magmatic.name", im, `is`, p, spec)
            "aquatic" -> givePlayerBastard("aquatic.name", im, `is`, p, spec)
            "pull" -> givePlayerBastard("pull.name", im, `is`, p, spec)
            "paralyze" -> givePlayerBastard("paralyze.name", im, `is`, p, spec)
            "acacia" -> givePlayerBastard("acacia.name", im, `is`, p, spec)
            "darkoak" -> givePlayerBastard("darkoak.name", im, `is`, p, spec)
            "cluster" -> givePlayerBastard("cluster.name", im, `is`, p, spec)
            "airship" -> givePlayerBastard("airship.name", im, `is`, p, spec)
            "iron" -> givePlayerBastard("iron.name", im, `is`, p, spec)
            "curse" -> givePlayerBastard("curse.name", im, `is`, p, spec)
            "round" -> givePlayerBastard("round.name", im, `is`, p, spec)
            "frozen" -> givePlayerBastard("frozen.name", im, `is`, p, spec)
        }
    }

    fun giveBastard(p: Player, id: Int, spec: Boolean) {
        val `is` = ItemStack(Material.BOW, 1)
        val im = `is`.itemMeta
        when (id) {
            1 -> givePlayerBastard("exploding.name", im, `is`, p, spec)
            2 -> givePlayerBastard("lightning.name", im, `is`, p, spec)
            3 -> givePlayerBastard("clucky.name", im, `is`, p, spec)
            4 -> givePlayerBastard("ender.name", im, `is`, p, spec)
            5 -> givePlayerBastard("oak.name", im, `is`, p, spec)
            6 -> givePlayerBastard("spruce.name", im, `is`, p, spec)
            7 -> givePlayerBastard("birch.name", im, `is`, p, spec)
            8 -> givePlayerBastard("jungle.name", im, `is`, p, spec)
            9 -> givePlayerBastard("batty.name", im, `is`, p, spec)
            10 -> givePlayerBastard("nuclear.name", im, `is`, p, spec)
            11 -> givePlayerBastard("enlightened.name", im, `is`, p, spec)
            12 -> givePlayerBastard("ranged.name", im, `is`, p, spec)
            13 -> givePlayerBastard("machine.name", im, `is`, p, spec)
            14 -> givePlayerBastard("poisonous.name", im, `is`, p, spec)
            15 -> givePlayerBastard("disorienting.name", im, `is`, p, spec)
            16 -> givePlayerBastard("swap.name", im, `is`, p, spec)
            17 -> givePlayerBastard("draining.name", im, `is`, p, spec)
            18 -> givePlayerBastard("flintand.name", im, `is`, p, spec)
            19 -> givePlayerBastard("disarming.name", im, `is`, p, spec)
            20 -> givePlayerBastard("wither.name", im, `is`, p, spec)
            21 -> givePlayerBastard("firey.name", im, `is`, p, spec)
            22 -> givePlayerBastard("slow.name", im, `is`, p, spec)
            23 -> givePlayerBastard("level.name", im, `is`, p, spec)
            24 -> givePlayerBastard("undead.name", im, `is`, p, spec)
            25 -> givePlayerBastard("woodman.name", im, `is`, p, spec)
            26 -> givePlayerBastard("starvation.name", im, `is`, p, spec)
            27 -> givePlayerBastard("multi.name", im, `is`, p, spec)
            28 -> givePlayerBastard("bomb.name", im, `is`, p, spec)
            29 -> givePlayerBastard("drop.name", im, `is`, p, spec)
            30 -> givePlayerBastard("airstrike.name", im, `is`, p, spec)
            31 -> givePlayerBastard("magmatic.name", im, `is`, p, spec)
            32 -> givePlayerBastard("aquatic.name", im, `is`, p, spec)
            33 -> givePlayerBastard("pull.name", im, `is`, p, spec)
            34 -> givePlayerBastard("paralyze.name", im, `is`, p, spec)
            35 -> givePlayerBastard("acacia.name", im, `is`, p, spec)
            36 -> givePlayerBastard("darkoak.name", im, `is`, p, spec)
            37 -> givePlayerBastard("cluster.name", im, `is`, p, spec)
            38 -> givePlayerBastard("airship.name", im, `is`, p, spec)
            39 -> givePlayerBastard("iron.name", im, `is`, p, spec)
            40 -> givePlayerBastard("curse.name", im, `is`, p, spec)
            41 -> givePlayerBastard("round.name", im, `is`, p, spec)
            42 -> givePlayerBastard("frozen.name", im, `is`, p, spec)
        }
    }

    fun givePlayerBastard(node: String?, im: ItemMeta, `is`: ItemStack, p: Player, spec: Boolean) {
        im.displayName = Methods.setColours(config.getString(node))
        `is`.itemMeta = im
        if (spec) {
            `is`.addUnsafeEnchantment(Enchantment.DURABILITY, 10)
            `is`.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1)
        }
        p.inventory.addItem(`is`)
    }

    fun reloadData() {
        if (dataFile == null) {
            dataFile = File(dataFolder, "data.yml")
        }
        data = YamlConfiguration.loadConfiguration(dataFile)
        val defStream = getResource("data.yml")
        if (defStream != null) {
            val defConfig = YamlConfiguration.loadConfiguration(defStream)
            data!!.defaults = defConfig
        }
    }

    fun getData(): FileConfiguration? {
        if (data == null) {
            reloadData()
        }
        return data
    }

    fun saveData() {
        if (data == null || dataFile == null) {
            return
        }
        try {
            getData()!!.save(dataFile)
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Could not save config to $dataFile", ex)
        }
    }

    companion object {
        const val MINIGAME_VERSION = false
    }
}