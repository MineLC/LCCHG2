package lc.chg2.events;

import java.util.Collection;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class encantos implements Listener {
	/*
	@EventHandler(priority=EventPriority.HIGH)
	  public void InventoryClickEvent(EntityDamageByEntityEvent event) {
	    if ((event.getDamager() instanceof Player)) {
	      Player player = (Player)event.getDamager();
	      detectiontest(player, player.getItemInHand());
	      detectPotion(player);
	    }
	  }
	*/
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	  public void onclick1(InventoryClickEvent e) {
		if(e.getClickedInventory() != e.getWhoClicked().getInventory()) {
			if(e.getCurrentItem() != null) {
				e.setCurrentItem(detectiontest(e.getCurrentItem()));
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	  public void onClick2(PlayerPickupItemEvent e) {
		e.getItem().setItemStack(detectiontest(e.getItem().getItemStack()));
	}
	
	  public ItemStack detectiontest(ItemStack item) {
		  if(item == null || item.getType() == Material.AIR || item.getEnchantments().isEmpty()) {
			return item;  
		  }
		  
	        Map<Enchantment, Integer> Enchants = item.getEnchantments();
	        if (Enchants.containsKey(Enchantment.DAMAGE_ALL)) {
	            Integer level = Enchants.get(Enchantment.DAMAGE_ALL);
	            if (level >= 4) {
	                item.removeEnchantment(Enchantment.DAMAGE_ALL);
	                item.addEnchantment(Enchantment.DAMAGE_ALL, 3);
	            }
	        }
      if (Enchants.containsKey(Enchantment.PROTECTION_ENVIRONMENTAL)) {
          Integer level = Enchants.get(Enchantment.PROTECTION_ENVIRONMENTAL);
          if (level >= 3) {
              item.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
              item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
          }
      }
      if (Enchants.containsKey(Enchantment.KNOCKBACK)) {
    	  Integer level = Enchants.get(Enchantment.KNOCKBACK);
    	  if (level >= 3) {
        item.removeEnchantment(Enchantment.KNOCKBACK);
        item.addEnchantment(Enchantment.KNOCKBACK, 2);
        }
    	  }
      if (Enchants.containsKey(Enchantment.ARROW_DAMAGE)) {
    	  Integer level = Enchants.get(Enchantment.ARROW_DAMAGE);
    	  if (level >= 5) {
        item.removeEnchantment(Enchantment.ARROW_DAMAGE);
        item.addEnchantment(Enchantment.ARROW_DAMAGE, 4);
    }
}
      if (Enchants.containsKey(Enchantment.ARROW_KNOCKBACK)) {
    	  Integer level = Enchants.get(Enchantment.ARROW_KNOCKBACK);
    	  if (level >= 3) {
        item.removeEnchantment(Enchantment.ARROW_KNOCKBACK);
        item.addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
        }
    	  }
      return item;
	  }
	  
	  public void detectPotion(Player p) {
		  Collection<PotionEffect> pot = p.getActivePotionEffects();
		  if(pot.isEmpty())
			  return;
		  
		  for(PotionEffect pe : pot) {
			  if(pe.getAmplifier() > 3)
			  p.removePotionEffect(pe.getType());
			  else if(pe.getType() == PotionEffectType.INCREASE_DAMAGE)
				  if(pe.getAmplifier() > 1)
				  p.removePotionEffect(pe.getType());
		  }
	  }
}