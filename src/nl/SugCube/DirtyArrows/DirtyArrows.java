package nl.SugCube.DirtyArrows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class DirtyArrows extends JavaPlugin {
	
	private final Logger log = Logger.getLogger("Minecraft");
	File file = new File(getDataFolder() + File.separator + "config.yml");
	
	public ArrowListener al = new ArrowListener(this);
	public EnchantmentListener el = new EnchantmentListener(this);
	public PlayerJoinListener pjl = new PlayerJoinListener(this);
	public EntityListener enl = new EntityListener(this);
	public Help help = new Help(this);
	
	public List<Player> activated = new ArrayList<Player>();
	public List<Projectile> slow = new ArrayList<Projectile>();
	public List<Projectile> airstrike = new ArrayList<Projectile>();
	public List<Vector> slowVec = new ArrayList<Vector>();
	
	public List<Projectile> particleExploding = new ArrayList<Projectile>();
	public List<Projectile> particleFire = new ArrayList<Projectile>();
	public List<FallingBlock> particleLava = new ArrayList<FallingBlock>();
	public List<FallingBlock> particleWater = new ArrayList<FallingBlock>();
	
	@Override
	public void onEnable() {
		if (!file.exists()) {
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		log.info("[DirtyArrows] DirtyArrows has been enabled!");
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(al, this);
		pm.registerEvents(el, this);
		pm.registerEvents(enl, this);
		pm.registerEvents(pjl, this);
		
		ShapedRecipe arrow = new ShapedRecipe(new ItemStack(Material.ARROW, getConfig().getInt("arrow-recipe-amount"))).shape(" * "," # "," % ").setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK).setIngredient('%', Material.FEATHER);
		ShapedRecipe arrow2 = new ShapedRecipe(new ItemStack(Material.ARROW, getConfig().getInt("arrow-recipe-amount"))).shape("*  ","#  ","%  ").setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK).setIngredient('%', Material.FEATHER);
		ShapedRecipe arrow3 = new ShapedRecipe(new ItemStack(Material.ARROW, getConfig().getInt("arrow-recipe-amount"))).shape("  *","  #","  %").setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK).setIngredient('%', Material.FEATHER);
		getServer().addRecipe(arrow);
		getServer().addRecipe(arrow2);
		getServer().addRecipe(arrow3);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Timer(this), 0, 1);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Airstrike(this), 5, 5);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Particles(this), 2, 2);
		
		log.info("[DirtyArrows] 33 Bastards have been loaded");
		log.info("[DirtyArrows] 3 recipes have been loaded");
	}
	
	@Override
	public void onDisable() {
		log.info("[DirtyArrows] DirtyArrows has been disabled!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("da") || label.equalsIgnoreCase("dirtyarrows")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.hasPermission("dirtyarrows")) {
					if (args.length == 0) {
						if (player.hasPermission("dirtyarrows")) {
							if (activated.contains(player)) {
								activated.remove(player);
								player.sendMessage(ChatColor.YELLOW + "Dirty Arrows have been" + ChatColor.RED + " disabled!");
							} else {
								activated.add(player);
								player.sendMessage(ChatColor.YELLOW + "Dirty Arrows have been " + ChatColor.GREEN + "enabled!");
							}
						} else {
							player.sendMessage(ChatColor.RED + "[!!] You don't have permission to perform this command!");
						}
					} else if (args[0].equalsIgnoreCase("give")) {
						if (player.hasPermission("dirtyarrows.admin")) {
							if (args.length >= 3) {
								String p = args[1];
								boolean spec = false;
								
								if (args.length >= 4) {
									if (args[3].equalsIgnoreCase("ench")) {
										spec = true;
									} else {
										spec = false;
									}
								} else {
									spec = false;
								}
								
								try {
									int id = Integer.parseInt(args[2]);
									switch (p) {
									case "@a":
										for (Player pl : Bukkit.getOnlinePlayers()) {
											giveBastard(pl, id, spec);
										}
										break;
									case "@r":
										Random ran = new Random();
										Player[] players = Bukkit.getOnlinePlayers();
										giveBastard(players[ran.nextInt(players.length)], id, spec);
										break;
									default:
										giveBastard(Bukkit.getPlayer(p), id, spec);
									}
								} catch (Exception e) {
									String id = args[2];
									switch (p) {
									case "@a":
										for (Player pl : Bukkit.getOnlinePlayers()) {
											giveBastard(pl, id, spec);
										}
										break;
									case "@r":
										Random ran = new Random();
										Player[] players = Bukkit.getOnlinePlayers();
										giveBastard(players[ran.nextInt(players.length)], id, spec);
										break;
									default:
										giveBastard(Bukkit.getPlayer(p), id, spec);
									}
								}
							} else {
								player.sendMessage(Methods.setColours("&c[!!] Usage: /da give <player> <id>"));
							}
						} else {
							player.sendMessage(ChatColor.RED + "[!!] You don't have permission to perform this command!");
						}
					} else if (args.length == 1) {
						if (player.hasPermission("dirtyarrows.admin")) {
							if (args[0].equalsIgnoreCase("reload")) {
								try {
									reloadConfiguration();
									player.sendMessage(ChatColor.GREEN + "[DirtyArows] Reloaded config.yml");
								} catch (Exception e) {
									player.sendMessage(ChatColor.RED + "[DirtyArows] Reload failed");
								}
							} else {
								Help.showMainHelpPage1(player);
							}
						} else {
							player.sendMessage(ChatColor.RED + "[!!] You don't have permission to perform this command!");
						}
					} else if (args.length > 1) {
						if (args[1].equalsIgnoreCase("2")) {
							Help.showMainHelpPage2(player);
						} else if (args[1].equalsIgnoreCase("3")){
							Help.showMainHelpPage3(player);
						} else if (args[1].equalsIgnoreCase("4")) {
							Help.showMainHelpPage4(player);
						} else if (args[1].equalsIgnoreCase("5")) {
							Help.showMainHelpPage5(player);
						} else if (args[1].equalsIgnoreCase("6")) {
							Help.showMainHelpPage6(player);
						} else {
							Help.showMainHelpPage1(player);
						}
					}
				} else {
					player.sendMessage(ChatColor.RED + "[!!] You don't have permission to perform this command");
				}
			} else {
				System.out.println("Only players can perform this command!");
			}
		}
		
		return false;
	}
	
	public void reloadConfiguration() {
		reloadConfig();
		getServer().resetRecipes();
		ShapedRecipe arrow = new ShapedRecipe(new ItemStack(Material.ARROW, getConfig().getInt("arrow-recipe-amount"))).shape(" * "," # "," % ").setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK).setIngredient('%', Material.FEATHER);
		ShapedRecipe arrow2 = new ShapedRecipe(new ItemStack(Material.ARROW, getConfig().getInt("arrow-recipe-amount"))).shape("*  ","#  ","%  ").setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK).setIngredient('%', Material.FEATHER);
		ShapedRecipe arrow3 = new ShapedRecipe(new ItemStack(Material.ARROW, getConfig().getInt("arrow-recipe-amount"))).shape("  *","  #","  %").setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK).setIngredient('%', Material.FEATHER);
		getServer().addRecipe(arrow);
		getServer().addRecipe(arrow2);
		getServer().addRecipe(arrow3);
	}
	
	public void giveBastard(Player p, String name, boolean spec) {
		ItemStack is = new ItemStack(Material.BOW, 1);
		ItemMeta im = is.getItemMeta();
		
		switch (name) {
		case "exploding":
			givePlayerBastard("exploding.name", im, is, p, spec); break;
		case "lightning":
			givePlayerBastard("lightning.name", im, is, p, spec); break;
		case "clucky":
			givePlayerBastard("clucky.name", im, is, p, spec); break;
		case "ender":
			givePlayerBastard("ender.name", im, is, p, spec); break;
		case "oak":
			givePlayerBastard("oak.name", im, is, p, spec); break;
		case "spruce":
			givePlayerBastard("spruce.name", im, is, p, spec); break;
		case "birch":
			givePlayerBastard("birch.name", im, is, p, spec); break;
		case "jungle":
			givePlayerBastard("jungle.name", im, is, p, spec); break;
		case "batty":
			givePlayerBastard("batty.name", im, is, p, spec); break;
		case "nuclear":
			givePlayerBastard("nuclear.name", im, is, p, spec); break;
		case "enlightened":
			givePlayerBastard("enlightened.name", im, is, p, spec); break;
		case "ranged":
			givePlayerBastard("ranged.name", im, is, p, spec); break;
		case "machine":
			givePlayerBastard("machine.name", im, is, p, spec); break;
		case "poisonous":
			givePlayerBastard("poisonous.name", im, is, p, spec); break;
		case "disorienting":
			givePlayerBastard("disorienting.name", im, is, p, spec); break;
		case "swap":
			givePlayerBastard("swap.name", im, is, p, spec); break;
		case "draining":
			givePlayerBastard("draining.name", im, is, p, spec); break;
		case "flintand":
			givePlayerBastard("flintand.name", im, is, p, spec); break;
		case "disarming":
			givePlayerBastard("disarming.name", im, is, p, spec); break;
		case "wither":
			givePlayerBastard("wither.name", im, is, p, spec); break;
		case "firey":
			givePlayerBastard("firey.name", im, is, p, spec); break;
		case "slow":
			givePlayerBastard("slow.name", im, is, p, spec); break;
		case "level":
			givePlayerBastard("level.name", im, is, p, spec); break;
		case "undead":
			givePlayerBastard("undead.name", im, is, p, spec); break;
		case "woodman":
			givePlayerBastard("woodman.name", im, is, p, spec); break;
		case "starvation":
			givePlayerBastard("starvation.name", im, is, p, spec); break;
		case "multi":
			givePlayerBastard("multi.name", im, is, p, spec); break;
		case "bomb":
			givePlayerBastard("bomb.name", im, is, p, spec); break;
		case "drop":
			givePlayerBastard("drop.name", im, is, p, spec); break;
		case "airstrike":
			givePlayerBastard("airstrike.name", im, is, p, spec); break;
		case "magmatic":
			givePlayerBastard("magmatic.name", im, is, p, spec); break;
		case "aquatic":
			givePlayerBastard("aquatic.name", im, is, p, spec); break;
		case "pull":
			givePlayerBastard("pull.name", im, is, p, spec); break;
		case "paralyze":
			givePlayerBastard("paralyze.name", im, is, p, spec); break;
		}
	}
	
	public void giveBastard(Player p, int id, boolean spec) {
		ItemStack is = new ItemStack(Material.BOW, 1);
		ItemMeta im = is.getItemMeta();
		
		switch (id) {
		case 1:
			givePlayerBastard("exploding.name", im, is, p, spec); break;
		case 2:
			givePlayerBastard("lightning.name", im, is, p, spec); break;
		case 3:
			givePlayerBastard("clucky.name", im, is, p, spec); break;
		case 4:
			givePlayerBastard("ender.name", im, is, p, spec); break;
		case 5:
			givePlayerBastard("oak.name", im, is, p, spec); break;
		case 6:
			givePlayerBastard("spruce.name", im, is, p, spec); break;
		case 7:
			givePlayerBastard("birch.name", im, is, p, spec); break;
		case 8:
			givePlayerBastard("jungle.name", im, is, p, spec); break;
		case 9:
			givePlayerBastard("batty.name", im, is, p, spec); break;
		case 10:
			givePlayerBastard("nuclear.name", im, is, p, spec); break;
		case 11:
			givePlayerBastard("enlightened.name", im, is, p, spec); break;
		case 12:
			givePlayerBastard("ranged.name", im, is, p, spec); break;
		case 13:
			givePlayerBastard("machine.name", im, is, p, spec); break;
		case 14:
			givePlayerBastard("poisonous.name", im, is, p, spec); break;
		case 15:
			givePlayerBastard("disorienting.name", im, is, p, spec); break;
		case 16:
			givePlayerBastard("swap.name", im, is, p, spec); break;
		case 17:
			givePlayerBastard("draining.name", im, is, p, spec); break;
		case 18:
			givePlayerBastard("flintand.name", im, is, p, spec); break;
		case 19:
			givePlayerBastard("disarming.name", im, is, p, spec); break;
		case 20:
			givePlayerBastard("wither.name", im, is, p, spec); break;
		case 21:
			givePlayerBastard("firey.name", im, is, p, spec); break;
		case 22:
			givePlayerBastard("slow.name", im, is, p, spec); break;
		case 23:
			givePlayerBastard("level.name", im, is, p, spec); break;
		case 24:
			givePlayerBastard("undead.name", im, is, p, spec); break;
		case 25:
			givePlayerBastard("woodman.name", im, is, p, spec); break;
		case 26:
			givePlayerBastard("starvation.name", im, is, p, spec); break;
		case 27:
			givePlayerBastard("multi.name", im, is, p, spec); break;
		case 28:
			givePlayerBastard("bomb.name", im, is, p, spec); break;
		case 29:
			givePlayerBastard("drop.name", im, is, p, spec); break;
		case 30:
			givePlayerBastard("airstrike.name", im, is, p, spec); break;
		case 31:
			givePlayerBastard("magmatic.name", im, is, p, spec); break;
		case 32:
			givePlayerBastard("aquatic.name", im, is, p, spec); break;
		case 33:
			givePlayerBastard("pull.name", im, is, p, spec); break;
		case 34:
			givePlayerBastard("paralyze.name", im, is, p, spec); break;
		}
	}
	
	public void givePlayerBastard(String node, ItemMeta im, ItemStack is, Player p, boolean spec) {
		im.setDisplayName(Methods.setColours(getConfig().getString(node)));
		is.setItemMeta(im);
		
		if (spec) {
			is.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
			is.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		}
		
		p.getInventory().addItem(is);
	}
	
}
