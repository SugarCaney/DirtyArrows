package nl.SugCube.DirtyArrows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DirtyArrows extends JavaPlugin {
	
	private final Logger log = Logger.getLogger("Minecraft");
	
	public ArrowListener al = new ArrowListener(this);
	
	public List<Player> activated = new ArrayList<Player>();
	
	@Override
	public void onEnable() {
		File file = new File(getDataFolder() + File.separator + "config.yml");
		if (!file.exists()) {
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		log.info("[DirtyArrows] DirtyArrows has been enabled!");
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(al, this);
		
		ShapedRecipe arrow = new ShapedRecipe(new ItemStack(Material.ARROW, getConfig().getInt("arrow-recipe-amount"))).shape(" * "," # "," % ").setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK).setIngredient('%', Material.FEATHER);
		getServer().addRecipe(arrow);
		
		log.info("[DirtyArrows] 18 Bastards have been loaded");
		log.info("[DirtyArrows] 1 recipe has been loaded");
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
					} else if (args.length == 1) {
						Help.showMainHelpPage1(player);
					} else if (args.length > 1) {
						if (args[1].equalsIgnoreCase("2")) {
							Help.showMainHelpPage2(player);
						} else if (args[1].equalsIgnoreCase("3")){
							Help.showMainHelpPage3(player);
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
	
}
