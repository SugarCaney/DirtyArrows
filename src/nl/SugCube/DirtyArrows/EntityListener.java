package nl.SugCube.DirtyArrows;

import java.util.List;
import java.util.Random;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityListener implements Listener {
	
	Random ran = new Random();
	boolean extra = false;
	Player player;
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if (arrow.getShooter() instanceof Player) {
				player = (Player) arrow.getShooter();
			}
		}
		if (event.getDamager().getType().equals(EntityType.WITHER_SKULL)) {
			if (event.getEntity() instanceof LivingEntity) {
				LivingEntity lentity = (LivingEntity) event.getEntity();
				lentity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 180, 1));
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		try {
			if (player.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS) && event.getDrops() != null) {
				ItemStack i = player.getItemInHand();
				if (i.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) == 1) {
					if (ran.nextInt(9) == 0) {
						extra = true;
					}
				} else if (i.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) == 2) {
					if (ran.nextInt(8) == 0) {
						extra = true;
					}
				} else {
					if (ran.nextInt(7) == 0) {
						extra = true;
					}
				}
				
				List<ItemStack> drops = event.getDrops();
				if (ran.nextInt(3) == 0) {
					for (ItemStack drop : drops) {
						int amount;
						if (ran.nextInt(3) != 0) {
							amount = i.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) - 1;
						} else {
							amount = i.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
						}
						drop.setAmount(drop.getAmount() + amount);
					}
				}
				if (extra) {
					for (ItemStack drop : drops) {
						drop.setAmount(drop.getAmount() + 1);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong with the Looting enchantment (Bow)");
		}
	}

}
