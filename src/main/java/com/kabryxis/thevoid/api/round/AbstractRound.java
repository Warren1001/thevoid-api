package com.kabryxis.thevoid.api.round;

import com.kabryxis.kabutils.data.file.yaml.Config;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractRound implements Round {
	
	public final static int DEFAULT_roundLength = 30;
	public final static List<String> DEFAULT_worldNames = Collections.unmodifiableList(Collections.singletonList("world"));
	public final static List<String> DEFAULT_schematics = Collections.unmodifiableList(Arrays.asList("rainbow", "halfsphere"));
	
	private final static String directory = "plugins" + File.separator + "TheVoid" + File.separator + "rounds" + File.separator;
	
	protected final String name;
	protected final Config config;
	protected final ItemStack[] inventory = new ItemStack[36], armor = new ItemStack[4];
	
	protected List<String> worldNames;
	protected List<String> schematics;
	protected int roundLength, startingPoints;
	
	public AbstractRound(String name, int startingPoints) {
		this.name = name;
		this.startingPoints = startingPoints;
		this.config = new Config(directory + name + ".yml");
		if(!config.exists()) {
			generateDefaults();
			this.roundLength = config.get("round-length", Integer.class);
			this.worldNames = config.getList("world-names", String.class);
			this.schematics = config.getList("schematics", String.class);
			config.save();
		}
		else {
			config.load(config -> {
				this.roundLength = config.get("round-length", Integer.class);
				this.worldNames = config.getList("world-names", String.class);
				this.schematics = config.getList("schematics", String.class);
			});
		}
	}
	
	public abstract void generateDefaults();
	
	protected void useDefaults() {
		config.set("round-length", AbstractRound.DEFAULT_roundLength);
		config.set("world-names", AbstractRound.DEFAULT_worldNames);
		config.set("schematics", AbstractRound.DEFAULT_schematics);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public int getRoundLength() {
		return roundLength;
	}
	
	@Override
	public List<String> getWorldNames() {
		return worldNames;
	}
	
	@Override
	public List<String> getSchematics() {
		return schematics;
	}
	
	public int getStartingPoints() {
		return startingPoints;
	}
	
	public ItemStack[] getInventory() {
		return inventory;
	}
	
	public ItemStack[] getArmor() {
		return armor;
	}
	
	@Override
	public void customTimer() {}
	
}
