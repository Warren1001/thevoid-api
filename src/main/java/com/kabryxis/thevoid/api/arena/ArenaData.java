package com.kabryxis.thevoid.api.arena;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Location;

import com.kabryxis.kabutils.spigot.world.ChunkEntry;
import com.kabryxis.thevoid.api.arena.object.ArenaDataObject;
import com.kabryxis.thevoid.api.schematic.Schematic;

public class ArenaData {
	
	private final Schematic schematic;
	private final Arena arena;
	
	private Map<String, ArenaDataObject> dataObjects;
	private Map<Long, List<ChunkEntry>> chunkData;
	private Location center;
	
	private int lowestY;
	
	public ArenaData(Schematic schematic, Arena arena) {
		this.schematic = schematic;
		this.arena = arena;
		arena.getRegistry().handle(this);
	}
	
	public Schematic getSchematic() {
		return schematic;
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public void setCurrentChunkData(Map<Long, List<ChunkEntry>> chunkData) {
		this.chunkData = chunkData;
		lowestY = Integer.MAX_VALUE;
		for(List<ChunkEntry> list : chunkData.values()) {
			for(ChunkEntry entry : list) {
				int y = entry.getY();
				if(y < lowestY) lowestY = y;
			}
		}
	}
	
	public Map<Long, List<ChunkEntry>> getCurrentChunkData() {
		return chunkData;
	}
	
	public void loadSchematic() {
		arena.getWorld().loadSchematic(chunkData);
	}
	
	public void setCenter(Location center) {
		this.center = center;
	}
	
	public Location getCenter() {
		return center;
	}
	
	public int getLowestY() {
		return lowestY;
	}
	
	public void registerDataObject(String name, ArenaDataObject dataObject) {
		if(dataObjects == null) dataObjects = new HashMap<>();
		dataObjects.put(name, dataObject);
	}
	
	public ArenaDataObject getDataObject(String name) {
		return (dataObjects != null) ? dataObjects.get(name) : null;
	}
	
	public boolean hasDataObject(String name) {
		return dataObjects != null && dataObjects.get(name) != null;
	}
	
	public void forEachDataObject(Consumer<? super ArenaDataObject> action) {
		if(dataObjects != null) dataObjects.values().forEach(action);
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof ArenaData && ((ArenaData)o).chunkData.equals(chunkData);
	}
	
}
