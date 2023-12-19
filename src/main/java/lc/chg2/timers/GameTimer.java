package lc.chg2.timers;

import lc.chg2.CHG2;
import lc.chg2.utilities.BGChat;
import lc.chg2.utilities.BGFBattle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GameTimer {
  private static Integer shed_id = null;
  
  public GameTimer() {
    shed_id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(CHG2.instance, new Runnable() {
      public void run() {
        CHG2.GAME_RUNNING_TIME++;
        CHG2.checkwinner();
        if (((CHG2.GAME_RUNNING_TIME % 5 != 0 ? 1 : 0) & (CHG2.GAME_RUNNING_TIME % 10 != 0 ? 1 : 0)) != 0) {
        	BGChat.printTipChat();
        }
        if ((CHG2.GAME_RUNNING_TIME == CHG2.END_GAME_TIME - 1)) {
			for (Player pl : CHG2.getGamers()) {
			pl.playSound(pl.getLocation(), Sound.AMBIENCE_CAVE, 1, -1);
			}
            BGChat.printInfoChat(ChatColor.RED + "[Batalla Final] " +ChatColor.GREEN + "Todos seran enviados al spawn en 1 minuto!");
              }
        if ((CHG2.GAME_RUNNING_TIME == CHG2.END_GAME_TIME) &&  (CHG2.END_GAME)) {
          CHG2.END_GAME = false;
          
          BGFBattle.createBattle();
          new EndGameTimer();
        }
        if (CHG2.GAME_RUNNING_TIME == CHG2.MAX_GAME_RUNNING_TIME - 1) {
            BGChat.printInfoChat(ChatColor.RED + "[Batalla final] " + ChatColor.GREEN +"1 minuto restante.");
        }
        if (CHG2.GAME_RUNNING_TIME >= CHG2.MAX_GAME_RUNNING_TIME) {
          Bukkit.getServer().shutdown();
        }
      }
    }, 0L, 1200L);
  }
  
  public static void cancel() {
    if (shed_id != null) {
      Bukkit.getServer().getScheduler().cancelTask(shed_id);
      shed_id = null;
    }
  }
}
