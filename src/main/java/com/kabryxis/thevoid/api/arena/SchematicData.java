package com.kabryxis.thevoid.api.arena;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.kabryxis.thevoid.api.schematic.Schematic;

public class SchematicData {
	
	private Schematic schematic;
	private Location center;
	private Map<Long, List<int[]>> currentChunkData;
	private List<Location> walkableLocs;
	
	public SchematicData(Schematic schematic, Location center, Map<Long, List<int[]>> currentChunkData, List<Location> walkableLocs) {
		this.schematic = schematic;
		this.center = center;
		this.currentChunkData = currentChunkData;
		this.walkableLocs = walkableLocs;
	}
	
	public Schematic getSchematic() {
		return schematic;
	}
	
	public Location getCenter() {
		return center;
	}
	
	public Map<Long, List<int[]>> getCurrentChunkData() {
		return currentChunkData;
	}
	
	public List<Location> getWalkableLocations() {
		return walkableLocs;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof SchematicData ? ((SchematicData)o).getSchematic().getName().equals(schematic.getName()) : false;
	}
	
}
