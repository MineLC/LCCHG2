package lc.chg2.utilities;

import java.io.File;

import lc.chg2.CHG2;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BGFiles {		
	public static FileConfiguration abconf;
	public static FileConfiguration bookconf;
	public static FileConfiguration config;
	public static FileConfiguration dsign;
	public static FileConfiguration kitconf;
	public static FileConfiguration worldconf;

	public BGFiles() {		
		try{
			loadFiles();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadFiles() throws Exception {
		File configFile = new File(CHG2.instance.getDataFolder(), "config.yml");
		File kitFile = new File(CHG2.instance.getDataFolder(), "kit.yml");
		File deathSignFile = new File(CHG2.instance.getDataFolder(), "deathsign.yml");
		File abilitiesFile = new File(CHG2.instance.getDataFolder(), "abilities.yml");
		File bookFile = new File(CHG2.instance.getDataFolder(), "book.yml");
		File worldFile = new File(CHG2.instance.getDataFolder(), "world.yml");

		Integer creation = 0;
		
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			CHG2.copy(CHG2.instance.getResource("config.yml"), configFile);
			creation++;
		}
		if (!kitFile.exists()) {
			kitFile.getParentFile().mkdirs();
			CHG2.copy(CHG2.instance.getResource("kit.yml"), kitFile);
			creation++;
		}
		if (!deathSignFile.exists()) {
			deathSignFile.getParentFile().mkdirs();
			CHG2.copy(CHG2.instance.getResource("deathsign.yml"), deathSignFile);
			creation++;
		}
		if (!abilitiesFile.exists()) {
			abilitiesFile.getParentFile().mkdirs();
			CHG2.copy(CHG2.instance.getResource("abilities.yml"), abilitiesFile);
			creation++;
		}
		if (!bookFile.exists()) {
			bookFile.getParentFile().mkdirs();
			CHG2.copy(CHG2.instance.getResource("book.yml"), bookFile);
			creation++;
		}
		if(!worldFile.exists()) {
			worldFile.getParentFile().mkdirs();
			CHG2.copy(CHG2.instance.getResource("world.yml"), worldFile);
			creation++;
		}
						
		abconf = YamlConfiguration.loadConfiguration(
				new File(CHG2.instance.getDataFolder(), "abilities.yml"));
		bookconf = YamlConfiguration.loadConfiguration(
				new File(CHG2.instance.getDataFolder(), "book.yml"));
		config = YamlConfiguration.loadConfiguration(
				new File(CHG2.instance.getDataFolder(), "config.yml"));
		dsign = YamlConfiguration.loadConfiguration(
				new File(CHG2.instance.getDataFolder(), "deathsign.yml"));
		kitconf = YamlConfiguration.loadConfiguration(
				new File(CHG2.instance.getDataFolder(), "kit.yml"));
		worldconf = YamlConfiguration.loadConfiguration(
				new File(CHG2.instance.getDataFolder(), "world.yml"));
	}
}
