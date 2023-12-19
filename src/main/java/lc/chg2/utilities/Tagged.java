package lc.chg2.utilities;


import lc.core2.entities.Jugador;

import java.util.HashMap;

public class Tagged {
	private static HashMap<Jugador, Long> taggedtime = new HashMap<Jugador, Long>();
	private static HashMap<Jugador, Jugador> taggedplayer = new HashMap<Jugador, Jugador>();
	
	public static void addTagged(Jugador player, Jugador killer, Long time) {
		taggedplayer.put(player, killer);
		taggedtime.put(player, time);
	}
	
	public static Long getTime(Jugador j) {
		if(taggedtime.containsKey(j)) {
			return taggedtime.get(j);
		}
		
		return 0L;
	}
	
	public static Jugador getKiller(Jugador j) {
		Jugador ret = null;
		if(taggedplayer.containsKey(j)) {
			ret = taggedplayer.get(j);
			taggedplayer.remove(j);
			taggedtime.remove(j);
		}
		
		return ret;
	}
	
	public static void removeTagged(Jugador jug) {
		if(taggedplayer.containsKey(jug)) {
			taggedplayer.remove(jug);
		}
		
		if(taggedtime.containsKey(jug)) {
			taggedtime.remove(jug);
		}
		
	}
}
