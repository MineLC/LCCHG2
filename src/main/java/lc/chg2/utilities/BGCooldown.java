package lc.chg2.utilities;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import lc.chg2.CHG2;
import lc.chg2.events.BGAbilitiesListener;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class BGCooldown {

	public BGCooldown() {
		BGFiles.abconf = YamlConfiguration.loadConfiguration(new File(CHG2.instance.getDataFolder(), "abilities.yml"));
	}
	
	public static void monkCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run() {
				
				BGAbilitiesListener.cooldown.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.12.Cooldown") * 1000);
	}
	
	public static void thiefCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run() {
				
				BGAbilitiesListener.cooldown.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.15.Cooldown") * 1000);
	}
	
	public static void ghostCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run() {
				
				BGAbilitiesListener.cooldown.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.16.Cooldown") * 1000);
	}
	
	public static void viperCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run() {
				
				BGAbilitiesListener.cooldown.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.19.Duration") * 1000);
	}
	public static void orcoCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run() {
				
				BGAbilitiesListener.cooldown.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.35.Duration") * 1000);
	}
	public static void trollCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run() {
				
				BGAbilitiesListener.cooldown.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.36.Duration") * 1000);
	}
	public static void thorCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run(){
				
				BGAbilitiesListener.cooldown.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.11.Cooldown") * 1000);
	}
	public static void flashCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run(){
				
		        BGAbilitiesListener.cooldown.remove(player);
	            player.sendMessage(ChatColor.GREEN + "Ahora puedes volver a teletransportarte!");
	          }
			
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.31.Cooldown") * 1000);
	}
	
	public static void timeCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run() {
				
				BGAbilitiesListener.cooldown.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.22.Cooldown")*1000);
	}
	
	public static void freezeCooldown(final Player player) {
		
		TimerTask action = new TimerTask(){
			
			public void run() {
				player.setWalkSpeed(0.2F);
				BGChat.printPlayerChat(player, BGFiles.abconf.getString("AB.22.unfrozen"));
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.22.Duration")*1000);
	}
}
