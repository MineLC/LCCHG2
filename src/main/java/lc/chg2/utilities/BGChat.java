package lc.chg2.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lc.chg2.CHG2;
import lc.core2.entities.Jugador;
import lc.core2.utils.IconMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class BGChat {
	static Integer TIP_COUNT = 0;
	static List<String> TIPS = new ArrayList<String>();
	
	public BGChat() {		
		List<String> tiplist = BGFiles.config.getStringList("TIPS");
		for(String tip : tiplist)
			TIPS.add(tip);
	}

	public static void printInfoChat(String text) {
		broadcast(ChatColor.DARK_GREEN + text);
	}

	public static void printDeathChat(String text) {
		broadcast(ChatColor.RED + text);
	}

	public static void printTimeChat(String text) {
		broadcast(ChatColor.GREEN + text);
	}

	public static void printPlayerChat(Player player, String text) {
			player.sendMessage(ChatColor.GRAY + text);
	}
	
	private static void broadcast(String msg) {
		for(Player Online : Bukkit.getOnlinePlayers()) {
			Online.sendMessage(msg);
		}
	}

	public static void printHelpChat(Player player) {
			BGChat.printPlayerChat(player, CHG2.SERVER_TITLE);
			String are = "Hay";
			String players = "jugadores";
			if (CHG2.getGamers().size() == 1) {
				are = "Hay";
				players = "jugador";
			}

			Integer timeleft = CHG2.MAX_GAME_RUNNING_TIME
					- CHG2.GAME_RUNNING_TIME;
			String is = "Faltan";
			String minute = "minutos";
			if (timeleft <= 1) {
				is = "Falta";
				minute = "minutos";
			}
			player.sendMessage(ChatColor.GRAY + " - " + are + " "
					+ CHG2.getGamers().size() + " " + players + " conectados.");
			player.sendMessage(ChatColor.GRAY + " - " + is + " " + timeleft + " "
					+ minute + " para terminar el juego.");
			if (CHG2.HELP_MESSAGE != null && CHG2.HELP_MESSAGE != "")
				player.sendMessage(ChatColor.GRAY + " - " + CHG2.HELP_MESSAGE);
	}

	@SuppressWarnings({ "deprecation", "unlikely-arg-type" })
	public static void printKitChat(Player player) {
		 Set<String> kits = BGFiles.kitconf.getKeys(false);
		 
		 Integer invsize = 9;
		 for(int i=0; i<=10; i++) {
			 if((i*9) >= kits.size()) {
				 invsize = invsize + i*9;
				 break;
			 }
		 }
		 final Player pl = player;
		 IconMenu menu = new IconMenu("Selecciona un KIT", invsize, new IconMenu.OptionClickEventHandler() {
	            public void onOptionClick(IconMenu.OptionClickEvent event) {
	            	BGKit.setKit(pl, ChatColor.stripColor(event.getName()));
	                event.setWillClose(true);
	                event.setWillDestroy(false);
	            }
	        }, CHG2.instance, true);
		 
		 Integer mypos = 0;
		 Integer othpos = 1;
		 for(String kitname : kits) {	
			 try {
				 if(kitname.equalsIgnoreCase("default"))
					 continue;
				 
					char[] stringArray = kitname.toCharArray();
					stringArray[0] = Character.toUpperCase(stringArray[0]);
					kitname = new String(stringArray);
				 
					ArrayList<String> container = new ArrayList<String>();
					ConfigurationSection kit = BGFiles.kitconf.getConfigurationSection(kitname.toLowerCase());
					List<String> kititems = kit.getStringList("ITEMS");
					for (String item : kititems) {
						String[] oneitem = item.split(",");

						String itemstring = null;
						Integer id = null;
						Integer amount = null;
						String enchantment = null;
						String ench_numb = null;

						if (oneitem[0].contains(":")) {
							String[] ITEM_ID = oneitem[0].split(":");
							id = Integer.valueOf(Integer.parseInt(ITEM_ID[0]));
							amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
						} else {
							id = Integer.valueOf(Integer.parseInt(oneitem[0]));
							amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
						}

						itemstring = " - "
								+ amount
								+ "x "
								+ Material.getMaterial(id.intValue()).toString()
										.replace("_", " ").toLowerCase();

						if (oneitem.length == 4) {
							enchantment = Enchantment
									.getById(Integer.parseInt(oneitem[2])).getName()
									.toLowerCase();
							ench_numb = oneitem[3];

							itemstring = itemstring + " with " + enchantment + " "
									+ ench_numb;
						}

						container.add(ChatColor.WHITE + itemstring);
					}
					
					List<String> pots = kit.getStringList("POTION");
					for(String pot : pots) {	
						if (pot != null & pot != "") {
							if (!pot.equals(0)) {
								String[] potion = pot.split(",");
								if (Integer.parseInt(potion[0]) != 0) {
									PotionEffectType pt = PotionEffectType.getById(Integer.parseInt(potion[0]));
									String name = pt.getName();
									if (Integer.parseInt(potion[1]) == 0) {
										name += " (Duracion: Infinita)";
									} else {
										name += " (Duracion: "+potion[1]+" seg)";
									}
									container.add(ChatColor.WHITE + " * " + name);
								}
							}
						}
					}
					
					List<Integer> abils = kit.getIntegerList("ABILITY");
					for(Integer abil : abils) {
						String desc = BGKit.getAbilityDesc(abil.intValue());
						if (desc != null)
							container.add(ChatColor.WHITE + " + " + desc);
					}
					if(!kit.getString("PERMS").equalsIgnoreCase("default")) {
						container.add(" ");
						container.add(ChatColor.DARK_AQUA+"Exclusivo para "+kit.getString("PERMS"));
					}
					Integer itemid = kit.getInt("ITEMMENU");
					Material kitem = Material.getMaterial(itemid);
				    
					String perms = kit.getString("PERMS");
					Jugador jug = Jugador.getJugador(player);
					boolean hasperms = jug.isCHG_Winner();
					
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
					if (hasperms) {
				    
						String[] info = new String[container.size()];
					    info = container.toArray(info);
						
						menu.setOption(mypos, new ItemStack(kitem, 1), ChatColor.GREEN + kitname, info);
						mypos++;
					} else {							
						String[] info = new String[container.size()];
					    info = container.toArray(info);
						
						menu.setOption(invsize - othpos, new ItemStack(kitem, 1), ChatColor.RED + kitname, info);
						othpos++;
					}
				container.clear();
			} catch (Exception e) {
				e.printStackTrace();

			}	
		 }
		 menu.open(player);
	}

	@SuppressWarnings({ "deprecation", "unlikely-arg-type" })
	public static void printKitInfo(Player player, String kitname) {
		String kitinfoname = kitname;
		kitname = kitname.toLowerCase();
		ConfigurationSection kit = BGFiles.kitconf.getConfigurationSection(kitname);
		if (kit == null || !BGKit.isKit(kitname)) {
			printPlayerChat(player,
					"Ese kit no existe, para ver los kits usa: /kit");
			return;
		}
			char[] stringArray = kitinfoname.toCharArray();
			stringArray[0] = Character.toUpperCase(stringArray[0]);
			kitinfoname = new String(stringArray);

			player.sendMessage(ChatColor.GREEN  + kitinfoname + " Kit incluye:");

			List<String> kititems = kit.getStringList("ITEMS");
			for (String item : kititems) {
				String[] oneitem = item.split(",");

				String itemstring = null;
				Integer id = null;
				Integer amount = null;
				String enchantment = null;
				String ench_numb = null;

				if (oneitem[0].contains(":")) {
					String[] ITEM_ID = oneitem[0].split(":");
					id = Integer.valueOf(Integer.parseInt(ITEM_ID[0]));
					amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
				} else {
					id = Integer.valueOf(Integer.parseInt(oneitem[0]));
					amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
				}

				itemstring = " - "
						+ amount
						+ "x "
						+ Material.getMaterial(id.intValue()).toString()
								.replace("_", " ").toLowerCase();

				if (oneitem.length == 4) {
					enchantment = Enchantment
							.getById(Integer.parseInt(oneitem[2])).getName()
							.toLowerCase();
					ench_numb = oneitem[3];

					itemstring = itemstring + " with " + enchantment + " "
							+ ench_numb;
				}

				player.sendMessage(ChatColor.WHITE + itemstring);
			}

			List<String> pots = kit.getStringList("POTION");
			for(String pot : pots) {	
				if (pot != null & pot != "") {
					if (!pot.equals(0)) {
						String[] potion = pot.split(",");
						if (Integer.parseInt(potion[0]) != 0) {
							PotionEffectType pt = PotionEffectType.getById(Integer.parseInt(potion[0]));
							String name = pt.getName();
							if (Integer.parseInt(potion[1]) == 0) {
								name += " (Duracion: Infinita)";
							} else {
								name += " (Duracion: "+potion[1]+" seg)";
							}
							player.sendMessage(ChatColor.WHITE + " * " + name);
						}
					}
				}
			}
			
			List<Integer> abils = kit.getIntegerList("ABILITY");
			for(Integer abil : abils) {
				String desc = BGKit.getAbilityDesc(abil);

				if (desc != null) {
					player.sendMessage(ChatColor.WHITE + " + " + desc);

				}
			}
			}

	public static void printTipChat() {
		if(TIPS.size() - 1 < TIP_COUNT)
			TIP_COUNT = 0;
		
		String tip = TIPS.get(TIP_COUNT);
		TIP_COUNT++;
		if (tip != "" || tip != null)
			broadcast(ChatColor.GRAY + "[" + ChatColor.RED + "MineLC" + ChatColor.GRAY + "] " + ChatColor.GREEN + tip);
	}

}