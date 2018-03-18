package com.kabryxis.thevoid.api.schematic;

import com.kabryxis.kabutils.spigot.data.Config;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public class BaseSchematic extends Schematic {
	
	private final Config data;
	
	public BaseSchematic(File file) {
		super(file);
		this.data = Config.get(new File(PATH + getName() + "-data.yml"));
		data.load();
	}
	
	public BaseSchematic(String name, BlockSelection selection, double centerX, double centerY, double centerZ, int radius) {
		super(name, selection, false);
		int lowestX = selection.getLowestX(), lowestY = selection.getLowestY(), lowestZ = selection.getLowestZ();
		Config config = Config.get(new File(PATH + name + "-data.yml"));
		config.set("radius", radius);
		ConfigurationSection cent = config.createSection("center");
		cent.set("x", centerX - lowestX);
		cent.set("y", centerY - lowestY);
		cent.set("z", centerZ - lowestZ);
		config.save();
		this.data = config;
	}
	
	public int getRadius() {
		return data.getInt("radius");
	}
	
	public Config getData() {
		return data;
	}
	
	public double getCenterX() {
		return data.getDouble("center.x");
	}
	
	public double getCenterY() {
		return data.getDouble("center.y");
	}
	
	public double getCenterZ() {
		return data.getDouble("center.z");
	}
	
}
