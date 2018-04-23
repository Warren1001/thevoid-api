package com.kabryxis.thevoid.api.round.impl;

import com.kabryxis.kabutils.data.file.yaml.Config;
import com.kabryxis.thevoid.api.round.Round;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class VoidRound implements Round {
	
	public final static int DEFAULT_roundLength = 30;
	public final static List<String> DEFAULT_worldNames = Collections.unmodifiableList(Arrays.asList("void_overworld", "void_nether", "void_end"));
	public final static List<String> DEFAULT_schematics = Collections.unmodifiableList(Arrays.asList("rainbow", "halfsphere"));
	public final static int DEFAULT_weight = 100;
	
	private final static String directory = "plugins" + File.separator + "TheVoid" + File.separator + "rounds" + File.separator;
	
	protected final String name;
	protected final Config config;
	protected final ItemStack[] inventory = new ItemStack[36], armor = new ItemStack[4];
	
	protected List<String> worldNames;
	protected List<String> schematics;
	protected int roundLength;
	protected int startingPoints;
	protected int weight;
	protected GameMode gameMode;
	
	public VoidRound(String name, int startingPoints, GameMode gameMode) {
		this.name = name;
		this.startingPoints = startingPoints;
		this.gameMode = gameMode;
		this.config = new Config(directory + name + ".yml");
		if(!config.exists()) {
			generateDefaults();
			this.roundLength = config.get("round-length", Integer.class);
			this.worldNames = config.getList("world-names", String.class);
			this.schematics = config.getList("schematics", String.class);
			this.weight = config.get("weight", Integer.class);
			config.save();
		}
		else {
			config.load(config -> {
				this.roundLength = config.get("round-length", Integer.class);
				this.worldNames = config.getList("world-names", String.class);
				this.schematics = config.getList("schematics", String.class);
				this.weight = config.get("weight", Integer.class);
			});
		}
	}
	
	public VoidRound(String name, int startingPoints) {
		this(name, startingPoints, GameMode.ADVENTURE);
	}
	
	public VoidRound(String name) {
		this(name, 1);
	}
	
	public void generateDefaults() {
		config.set("round-length", VoidRound.DEFAULT_roundLength);
		config.set("world-names", VoidRound.DEFAULT_worldNames);
		config.set("schematics", VoidRound.DEFAULT_schematics);
		config.set("weight", VoidRound.DEFAULT_weight);
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
	
	@Override
	public int getWeight() {
		return weight;
	}
	
	@Override
	public int getStartingPoints() {
		return startingPoints;
	}
	
	@Override
	public GameMode getGameMode() {
		return gameMode;
	}
	
	@Override
	public ItemStack[] getInventory() {
		return inventory;
	}
	
	@Override
	public ItemStack[] getArmor() {
		return armor;
	}
	
}
