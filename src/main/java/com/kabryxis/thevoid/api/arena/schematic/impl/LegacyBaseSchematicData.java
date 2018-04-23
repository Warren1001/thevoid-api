package com.kabryxis.thevoid.api.arena.schematic.impl;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.kabutils.spigot.world.ChunkLoader;
import com.kabryxis.thevoid.api.arena.impl.LegacyArena;
import com.kabryxis.thevoid.api.arena.object.ArenaDataObject;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class LegacyBaseSchematicData extends LegacySchematicData {
	
	private final Map<Class<? extends ArenaDataObject>, ArenaDataObject> dataObjects = new HashMap<>();
	
	private Location center;
	private int lx, ly, lz;
	private int mx, mz;
	
	public LegacyBaseSchematicData(LegacyBaseSchematic schematic, LegacyArena arena) {
		super(schematic, arena);
		//arena.getRegistry().handle(this);
	}
	
	@Override
	public LegacyBaseSchematic getSchematic() {
		return (LegacyBaseSchematic)super.getSchematic();
	}
	
	@Override
	public void loadSchematic() {
		super.loadSchematic();
		preloadChunks();
	}
	
	public boolean isOdd() {
		return getSchematic().isOdd();
	}
	
	public void setCenter(Location center) {
		this.center = center;
	}
	
	public Location getCenter() {
		return center;
	}
	
	public void setMinsAndMaxs(int lx, int ly, int lz, int mx, int mz) {
		this.lx = lx;
		this.ly = ly;
		this.lz = lz;
		this.mx = mx;
		this.mz = mz;
	}
	
	public void preloadChunks() {
		int lcx = lx >> 4, lcz = lz >> 4, mcx = mx >> 4, mcz = mz >> 4;
		Set<Chunk> chunkSet = new HashSet<>();
		LegacyArena arena = getArena();
		World world = arena.getWorld();
		int radius = 1;
		BukkitThreads.sync(() -> {
			Chunk baseChunk = arena.getLocation().getChunk();
			chunkSet.add(baseChunk);
			int baseChunkX = baseChunk.getX(), baseChunkZ = baseChunk.getZ();
			for(int x = -radius; x <= radius; x++) {
				for(int z = -radius; z <= radius; z++) {
					chunkSet.add(world.getChunkAt(baseChunkX + x, baseChunkZ + z));
				}
			}
			for(int cx = lcx; cx <= mcx; cx++) {
				for(int cz = lcz; cz <= mcz; cz++) {
					chunkSet.add(world.getChunkAt(cx, cz));
				}
			}
			chunkSet.forEach(chunk -> {
				ChunkLoader.keepInMemory(arena, chunk);
				if(!chunk.isLoaded()) chunk.load();
			});
		});
	}
	
	public void registerDataObject(ArenaDataObject dataObject) {
		dataObjects.put(dataObject.getClass(), dataObject);
	}
	
	public <T extends ArenaDataObject> T getDataObject(Class<T> clazz) {
		ArenaDataObject object = dataObjects.get(clazz);
		return object == null ? null : clazz.cast(object);
	}
	
	public boolean hasDataObject(Class<? extends ArenaDataObject> clazz) {
		return dataObjects.get(clazz) != null;
	}
	
	public void forEachDataObject(Consumer<? super ArenaDataObject> action) {
		if(dataObjects != null) dataObjects.values().forEach(action);
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof LegacyBaseSchematicData)) return false;
		LegacyBaseSchematicData d = (LegacyBaseSchematicData)o;
		return d.getArena().getName().equals(getArena().getName()) && d.getSchematic().getName().equals(getSchematic().getName());
	}
	
}
