package lc.chg2.utilities;

import java.util.*;

import lc.chg2.CHG2;
import lc.core2.entities.Database;
import lc.core2.entities.Jugador;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BGKit {
	public static List<String> kits = new ArrayList<String>();
	private static final Map<Integer, String> ABILITY_DESC = new HashMap<Integer, String>();

	public BGKit() {
		Set<String> kitList = BGFiles.kitconf.getKeys(false);
		for(String kit : kitList) {
			if(kit.equalsIgnoreCase("default"))
				continue;
			
			kits.add(kit.toLowerCase());
		}
	}

	private static String getAbilityDescription(int abilityId) {
		if (!ABILITY_DESC.containsKey(abilityId)) {
			String description = BGFiles.abconf.getString("AB." + abilityId + ".Desc");
			ABILITY_DESC.put(abilityId, description);
		}
		return ABILITY_DESC.get(abilityId);
	}

	@SuppressWarnings({ "deprecation", "unlikely-arg-type", "unused" })
	public static void giveKit(Player p) {
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.setExp(0);
		p.setLevel(0);
		p.setFoodLevel(20);
		p.setFlying(false);
		p.setAllowFlight(false);


		Jugador jug = Jugador.getJugador(p);
		String perms = BGFiles.kitconf.getConfigurationSection(jug.getCHG_Kit().toLowerCase()).getString("PERMS");
		boolean hasperms = jug.isCHG_Winner();
		
		if(hasperms) {
			jug.setCHG_Winner(false);
			Database.savePlayerSV_CHG(jug);
		}
		
		if(perms.equalsIgnoreCase("VIP") && jug.is_VIP()) {
			hasperms = true;
		} else if(perms.equalsIgnoreCase("SVIP") && jug.is_SVIP()) {
			hasperms = true;
		} else if(perms.equalsIgnoreCase("ELITE") && jug.is_ELITE()) {
			hasperms = true;
		} else if(perms.equalsIgnoreCase("RUBY") && jug.is_RUBY()) {
			hasperms = true;
		} else if(perms.equalsIgnoreCase("default")) {
			hasperms = true;
		}
		
		if(!hasperms) {
			jug.setCHG_Kit("default");
		}
		if (!kits.contains(jug.getCHG_Kit().toLowerCase())) {
			p.getInventory().addItem(BGMenus.getCompass());
			
			if (CHG2.DEFAULT_KIT) {
				try {
				ConfigurationSection def = BGFiles.kitconf.getConfigurationSection("default");
				
				List<String> kititems = def.getStringList("ITEMS");
				for (String item : kititems) {
					String[] oneitem = item.split(",");
					ItemStack i = null;
					Integer id = null;
					Integer amount = null;
					Short durability = null;
					if (oneitem[0].contains(":")) {
						String[] ITEM_ID = oneitem[0].split(":");
						id = Integer.valueOf(Integer.parseInt(ITEM_ID[0]));
						amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
						durability = Short.valueOf(Short.parseShort(ITEM_ID[1]));
						i = new ItemStack(id.intValue(), amount.intValue(),
								durability.shortValue());
					} else {
						id = Integer.valueOf(Integer.parseInt(oneitem[0]));
						amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
						i = new ItemStack(id.intValue(), amount.intValue());
					}

					if (oneitem.length == 4) {
						i.addUnsafeEnchantment(
								Enchantment.getById(Integer.parseInt(oneitem[2])),
								Integer.parseInt(oneitem[3]));
					}

					if ((id.intValue() < 298) || (317 < id.intValue())) {
						p.getInventory().addItem(new ItemStack[] { i });
					} else if ((id.intValue() == 298) || (id.intValue() == 302)
							|| (id.intValue() == 306) || (id.intValue() == 310)
							|| (id.intValue() == 314)) {
						i.setAmount(1);
						p.getInventory().setHelmet(i);
					} else if ((id.intValue() == 299) || (id.intValue() == 303)
							|| (id.intValue() == 307) || (id.intValue() == 311)
							|| (id.intValue() == 315)) {
						i.setAmount(1);
						p.getInventory().setChestplate(i);
					} else if ((id.intValue() == 300) || (id.intValue() == 304)
							|| (id.intValue() == 308) || (id.intValue() == 312)
							|| (id.intValue() == 316)) {
						i.setAmount(1);
						p.getInventory().setLeggings(i);
					} else if ((id.intValue() == 301) || (id.intValue() == 305)
							|| (id.intValue() == 309) || (id.intValue() == 313)
							|| (id.intValue() == 317)) {
						i.setAmount(1);
						p.getInventory().setBoots(i);
					}
				}

				List<String> pots = def.getStringList("POTION");
				for(String pot : pots) {	
					if (pot != null & pot != "") {
						if (!pot.equals(0)) {
							String[] potion = pot.split(",");
							if (Integer.parseInt(potion[0]) != 0) {
								if (Integer.parseInt(potion[1]) == 0) {
									p.addPotionEffect(new PotionEffect(PotionEffectType
											.getById(Integer.parseInt(potion[0])),
											CHG2.MAX_GAME_RUNNING_TIME * 1200, Integer
											.parseInt(potion[2])));
								} else {
									p.addPotionEffect(new PotionEffect(PotionEffectType
											.getById(Integer.parseInt(potion[0])), Integer
											.parseInt(potion[1]) * 20, Integer
											.parseInt(potion[2])));
								}
							}
						}
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		} 
			
			return;
		}

		String kitname = jug.getCHG_Kit().toLowerCase();
		try {
		ConfigurationSection kit = BGFiles.kitconf.getConfigurationSection(kitname.toLowerCase());

		List<String> kititems = kit.getStringList("ITEMS");
		for (String item : kititems) {
			String[] oneitem = item.split(",");
			ItemStack i = null;
			Integer id = null;
			Integer amount = null;
			Short durability = null;
			int blue = 0;
			int green = 0;
			int red = 0;
			if (item.toLowerCase().contains(">")){
			String[] color = item.split(">");
			if (color[1].toLowerCase().contains("blue"))
			{
			blue = 255;
			green = 0;
			red = 0;
			}
			else if (color[1].toLowerCase().contains("green"))
			{
			blue = 0;
			green = 255;
			red = 0;
			}
			else if (color[1].toLowerCase().contains("red"))
			{
			blue = 0;
			green = 0;
			red = 255;
			}
			else if (color[1].toLowerCase().contains("black"))
			{
			blue = 0;
			green = 0;
			red = 0;
			}
			else if (color[1].toLowerCase().contains("white"))
			{
			blue = 255;
			green = 255;
			red = 255;
			}
			}
			if (oneitem[0].contains(":")) {
				String[] ITEM_ID = oneitem[0].split(":");
				id = Integer.valueOf(Integer.parseInt(ITEM_ID[0]));
				amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
				durability = Short.valueOf(Short.parseShort(ITEM_ID[1]));
				i = new ItemStack(id.intValue(), amount.intValue(),
						durability.shortValue());
			} else {
				id = Integer.valueOf(Integer.parseInt(oneitem[0]));
				amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
				i = new ItemStack(id.intValue(), amount.intValue());
			}

			if (oneitem.length == 4) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
			}
			// For-Loops did not seem to work, so its written out manually.
			else if (oneitem.length == 6) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[4])),
						Integer.parseInt(oneitem[5]));
			}
			else if (oneitem.length == 8) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[4])),
						Integer.parseInt(oneitem[5]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[6])),
						Integer.parseInt(oneitem[7]));
			}
			else if (oneitem.length == 10) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[4])),
						Integer.parseInt(oneitem[5]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[6])),
						Integer.parseInt(oneitem[7]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[8])),
						Integer.parseInt(oneitem[9]));

			}
			else if (oneitem.length == 12) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[4])),
						Integer.parseInt(oneitem[5]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[6])),
						Integer.parseInt(oneitem[7]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[8])),
						Integer.parseInt(oneitem[9]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[10])),
						Integer.parseInt(oneitem[11]));
			}
			else if (oneitem.length == 14) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[4])),
						Integer.parseInt(oneitem[5]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[6])),
						Integer.parseInt(oneitem[7]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[8])),
						Integer.parseInt(oneitem[9]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[10])),
						Integer.parseInt(oneitem[11]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[12])),
						Integer.parseInt(oneitem[13]));
			}
			else if (oneitem.length == 16) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[4])),
						Integer.parseInt(oneitem[5]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[6])),
						Integer.parseInt(oneitem[7]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[8])),
						Integer.parseInt(oneitem[9]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[10])),
						Integer.parseInt(oneitem[11]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[12])),
						Integer.parseInt(oneitem[13]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[14])),
						Integer.parseInt(oneitem[15]));
			}

			if ((id.intValue() < 298) || (317 < id.intValue())) 
			{
				p.getInventory().addItem(new ItemStack[] { i });
			} 
			else if ((id.intValue() == 298) || (id.intValue() == 302)
					|| (id.intValue() == 306) || (id.intValue() == 310)
					|| (id.intValue() == 314)) 
			{
				if ((id.intValue() == 298) && (kitname.equalsIgnoreCase("spiderman")))// Checks if the Item line contains a > to give the armor color.
				{
				    LeatherArmorMeta h = (LeatherArmorMeta)i.getItemMeta();
				    h.setColor(Color.RED);//Gave it variables so they set-up above.
				    i.setItemMeta(h);// Give the armor the actual color we made
				}
				i.setAmount(1);
				p.getInventory().setHelmet(i);
				}
			else if ((id.intValue() == 299) || (id.intValue() == 303)
					|| (id.intValue() == 307) || (id.intValue() == 311)
					|| (id.intValue() == 315)) 
			{
				if ((id.intValue() == 299) && (kitname.equalsIgnoreCase("spiderman")))
				{
				    LeatherArmorMeta c = (LeatherArmorMeta)i.getItemMeta();
				    c.setColor(Color.BLUE);
				    i.setItemMeta(c);
				}
				i.setAmount(1);
				p.getInventory().setChestplate(i);
			} 
			else if ((id.intValue() == 300) || (id.intValue() == 304)
					|| (id.intValue() == 308) || (id.intValue() == 312)
					|| (id.intValue() == 316)) 
			{
				if ((id.intValue() == 300) && (kitname.equalsIgnoreCase("spiderman")))
				{
				    LeatherArmorMeta l = (LeatherArmorMeta)i.getItemMeta();
				    l.setColor(Color.RED);
				    i.setItemMeta(l);
				}
				i.setAmount(1);
				p.getInventory().setLeggings(i);
				}
			else if ((id.intValue() == 301) || (id.intValue() == 305)
					|| (id.intValue() == 309) || (id.intValue() == 313)
					|| (id.intValue() == 317)) 
			{
				if ((id.intValue() == 301) && (kitname.equalsIgnoreCase("spiderman")))
				{
				    LeatherArmorMeta b = (LeatherArmorMeta)i.getItemMeta();
				    b.setColor(Color.BLUE);
				    i.setItemMeta(b);
				}
				i.setAmount(1);
				p.getInventory().setBoots(i);
			}
		}

		List<String> pots = kit.getStringList("POTION");
		for(String pot : pots) {	
			if (pot != null & pot != "") {
				if (!pot.equals(0)) {
					String[] potion = pot.split(",");
					if (Integer.parseInt(potion[0]) != 0) {
						if (Integer.parseInt(potion[1]) == 0) {
							p.addPotionEffect(new PotionEffect(PotionEffectType
									.getById(Integer.parseInt(potion[0])),
									CHG2.MAX_GAME_RUNNING_TIME * 1200, Integer
									.parseInt(potion[2])));
						} else {
							p.addPotionEffect(new PotionEffect(PotionEffectType
									.getById(Integer.parseInt(potion[0])), Integer
									.parseInt(potion[1]) * 20, Integer
									.parseInt(potion[2])));
						}
					}
				}
			}
		}

			p.getInventory().addItem(BGMenus.getCompass());
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void setKit(Player player, String kitname) {
		kitname = kitname.toLowerCase();
		kitname = kitname.replace(".", "");
		ConfigurationSection kit = BGFiles.kitconf.getConfigurationSection(kitname);

		if (kit == null  && !kits.contains(kitname)) {
			BGChat.printPlayerChat(player, ChatColor.RED + "El kit no existe!");
			return;
		}
		Jugador jug = Jugador.getJugador(player);
		if(jug.getCHG_Kit().equalsIgnoreCase(kitname)) {
			player.sendMessage(ChatColor.RED+"Ya tienes seleccionado este kit!");
			return;
		}
		String perms = BGFiles.kitconf.getConfigurationSection(kitname).getString("PERMS");
		boolean hasperms = jug.isCHG_Winner();
		
		if(perms.equalsIgnoreCase("VIP") && jug.is_VIP()) {
			hasperms = true;
		} else if(perms.equalsIgnoreCase("SVIP") && jug.is_SVIP()) {
			hasperms = true;
		} else if(perms.equalsIgnoreCase("ELITE") && jug.is_ELITE()) {
			hasperms = true;
		}else if(perms.equalsIgnoreCase("RUBY") && jug.is_RUBY()) {
			hasperms = true;
		} else if(perms.equalsIgnoreCase("default")) {
			hasperms = true;
		}
		if (hasperms) {
			jug.setCHG_Kit(kitname);
			char[] stringArray = kitname.toCharArray();
			stringArray[0] = Character.toUpperCase(stringArray[0]);
			kitname = new String(stringArray);
			
			BGChat.printPlayerChat(player, ChatColor.GREEN + "Seleccionaste " + ChatColor.DARK_GREEN  + ChatColor.ITALIC + kitname + ChatColor.RESET + ChatColor.GREEN + " como tu kit.");
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1F, 1.5F);
			Database.savePlayerSV_CHG(jug);
			Database.BuyKit_SV_CHG(jug, kitname);
		} else {
			BGChat.printPlayerChat(player, ChatColor.RED + CHG2.NO_KIT_MSG);
			return;
		}
	}
	
	public static Boolean hasAbility(Player player, Integer ability) {
		if(CHG2.isSpectator(player)) {
			return false;
		}
		Jugador jug = Jugador.getJugador(player);
		if (!kits.contains(jug.getCHG_Kit().toLowerCase())) {
			if (CHG2.DEFAULT_KIT) {
				ConfigurationSection def = BGFiles.kitconf.getConfigurationSection("default");
				List<Integer> s = def.getIntegerList("ABILITY");
				for(Integer i : s) {
					if (i.equals(ability)) {
					return true;
					}
				}
				return false;
			}else {
				return false;
			}
		}

		String kitname = jug.getCHG_Kit().toLowerCase();
		ConfigurationSection kit = BGFiles.kitconf.getConfigurationSection(kitname);

		List<Integer> s = kit.getIntegerList("ABILITY");
		for(Integer i : s) {
			if (i.equals(ability)) {
				return true;
			}
		}
		return false;
	}
	public static boolean isKit(String kitName) {
		return kits.contains(kitName);
	}
	
	public static String getAbilityDesc(Integer ability) {
		if (ability == 0)
			return null;

        return getAbilityDescription(ability);
	}

	public static void setAbilityDesc(Integer ability, String description) throws Error {
		if (ABILITY_DESC.containsKey(ability))
			throw new Error("No hay descripcion.");
		else
			ABILITY_DESC.put(ability, description);
	}
}
