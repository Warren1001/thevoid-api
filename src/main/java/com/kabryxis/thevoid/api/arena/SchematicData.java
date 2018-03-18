package com.kabryxis.thevoid.api.arena;

import com.kabryxis.kabutils.spigot.world.ChunkEntry;
import com.kabryxis.thevoid.api.schematic.Schematic;
import com.kabryxis.thevoid.api.schematic.SchematicWork;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SchematicData {
	
	private final Schematic schematic;
	private final Arena arena;
	
	private Map<Long, List<ChunkEntry>> chunkData;
	
	private Map<Class<? extends SchematicWork>, SchematicWork> extraWorks;
	private int lowestY;
	
	public SchematicData(Schematic schematic, Arena arena) {
		this.schematic = schematic;
		this.arena = arena;
		if(schematic.hasSchematicWork()) {
			Set<Class<? extends SchematicWork>> schematicWorks = schematic.getSchematicWork();
			extraWorks = new HashMap<>(schematicWorks.size());
			schematicWorks.forEach(clazz -> {
				try {
					extraWorks.put(clazz, clazz.newInstance());
				}
				catch(InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			});
		}
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
		for(Map.Entry<Long, List<ChunkEntry>> entry : chunkData.entrySet()) {
			long chunk = entry.getKey();
			int cx = arena.getWorld().getChunkX(chunk), cz = arena.getWorld().getChunkZ(chunk);
			for(ChunkEntry chunkEntry : entry.getValue()) {
				int x = chunkEntry.getX(), y = chunkEntry.getY(), z = chunkEntry.getZ();
				Material type = chunkEntry.getType();
				int data = chunkEntry.getData();
				if(y < lowestY) lowestY = y;
				if(extraWorks != null) extraWorks.values().forEach(extra -> extra.doExtra(arena.getWorld().getWorld().getChunkAt(cx, cz).getBlock(x, y, z), type, data));
			}
		}
	}
	
	public Map<Long, List<ChunkEntry>> getCurrentChunkData() {
		return chunkData;
	}
	
	public boolean hasExtraWork(Class<? extends SchematicWork> clazz) {
		return extraWorks != null && extraWorks.get(clazz) != null;
	}
	
	public <T extends SchematicWork> T getExtraWork(Class<T> clazz) {
		return extraWorks == null ? null : clazz.cast(extraWorks.get(clazz));
	}
	
	public void loadSchematic() {
		arena.getWorld().loadSchematic(chunkData);
	}
	
	public int getLowestY() {
		return lowestY;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof SchematicData)) return false;
		SchematicData d = (SchematicData)o;
		return d.getArena().getName().equals(arena.getName()) && d.getSchematic().getName().equals(getSchematic().getName());
	}
	
}
