package com.kabryxis.thevoid.api.arena.schematic.impl;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.kabutils.spigot.world.ChunkLoader;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.impl.VoidArena;
import com.kabryxis.thevoid.api.arena.object.ArenaDataObject;
import com.kabryxis.thevoid.api.arena.schematic.BaseArenaData;
import com.kabryxis.thevoid.api.arena.schematic.IBaseSchematic;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class VoidBaseArenaData extends VoidArenaData implements BaseArenaData {
	
	private Map<Class<? extends ArenaDataObject>, ArenaDataObject> dataObjects;
	private int lx, ly, lz, mx, mz;
	
	public VoidBaseArenaData(VoidArena arena, IBaseSchematic schematic) {
		super(arena, schematic);
		arena.getRegistry().handle(this);
	}
	
	@Override
	public IBaseSchematic getSchematic() {
		return (IBaseSchematic)super.getSchematic();
	}
	
	@Override
	public void loadSchematic() {
		super.loadSchematic();
		loadChunks();
	}
	
	/*@Override
	public boolean isOdd() {
		return getSchematic().isOdd();
	}*/
	
	@Override
	public int getRadius() {
		return getSchematic().getRadius();
	}
	
	@Override
	public void setMinsAndMaxs(int lx, int ly, int lz, int mx, int mz) {
		this.lx = lx;
		this.ly = ly;
		this.lz = lz;
		this.mx = mx;
		this.mz = mz;
	}
	
	@Override
	public int getLowestY() {
		return ly;
	}
	
	@Override
	public void registerDataObject(ArenaDataObject dataObject) {
		if(dataObjects == null) dataObjects = new HashMap<>();
		dataObjects.put(dataObject.getClass(), dataObject);
	}
	
	@Override
	public <T extends ArenaDataObject> T getDataObject(Class<T> clazz) {
		if(dataObjects == null) return null;
		ArenaDataObject object = dataObjects.get(clazz);
		return object == null ? null : clazz.cast(object);
	}
	
	@Override
	public boolean hasDataObject(Class<? extends ArenaDataObject> clazz) {
		return dataObjects != null && dataObjects.containsKey(clazz);
	}
	
	@Override
	public void forEachDataObject(Consumer<? super ArenaDataObject> action) {
		if(dataObjects != null) dataObjects.values().forEach(action);
	}
	
	@Override
	public Set<String> getDataObjectKeys() {
		return getSchematic().getData().getKeys(true);
	}
	
	public void loadChunks() {
		int lcx = lx >> 4, lcz = lz >> 4, mcx = mx >> 4, mcz = mz >> 4;
		Set<Chunk> chunkSet = new HashSet<>();
		Arena arena = getArena();
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
	
}
