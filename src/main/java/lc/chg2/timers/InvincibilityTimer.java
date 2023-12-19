package lc.chg2.timers;

import lc.chg2.CHG2;
import lc.chg2.utilities.BGChat;
import lc.chg2.utilities.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class InvincibilityTimer {

private static Integer shed_id = null;

	public InvincibilityTimer() {
		
		shed_id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(CHG2.instance, new Runnable() {
			
			public void run() {
				if (CHG2.FINAL_COUNTDOWN > 0) {
					if (CHG2.FINAL_COUNTDOWN >= 10
							& CHG2.FINAL_COUNTDOWN % 10 == 0) {
						BGChat.printTimeChat("La invencibilidad termina en "
								+ CHG2.TIME(CHG2.FINAL_COUNTDOWN)
								+ ".");
					} else if (CHG2.FINAL_COUNTDOWN < 10) {
						BGChat.printTimeChat("La invencibilidad termina en "
								+ CHG2.TIME(CHG2.FINAL_COUNTDOWN)
								+ ".");
						for (Player pl : CHG2.getGamers()) {
							pl.playSound(pl.getLocation(), Sound.NOTE_PLING , 2.0F, 2);
						}
					}
					CHG2.FINAL_COUNTDOWN--;
				} else {
					BGChat.printTimeChat("");
					BGChat.printTimeChat("La invencibilidad ha terminado.");
					CHG2.log.info("Game phase: 3 - Fighting");
					for (Player pl : CHG2.getGamers()) {
						pl.playSound(pl.getLocation(), Sound.ANVIL_LAND, 1, 2);
					}
					BGChat.printTipChat();
					CHG2.spawn.getWorld().setAutoSave(true);
					CHG2.GAMESTATE = GameState.GAME;
					new GameTimer();
					cancel();
				}
			}
			
		}, 20, 20);
		
	}

	public static void cancel() {
		if(shed_id != null) {
			Bukkit.getServer().getScheduler().cancelTask(shed_id);
			shed_id = null;
		}
	}
}