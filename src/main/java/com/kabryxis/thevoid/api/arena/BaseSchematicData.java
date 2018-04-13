package com.kabryxis.thevoid.api.arena;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.kabutils.spigot.world.ChunkLoader;
import com.kabryxis.thevoid.api.arena.object.ArenaDataObject;
import com.kabryxis.thevoid.api.schematic.BaseSchematic;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BaseSchematicData extends SchematicData {
	
	private final Map<Class<? extends ArenaDataObject>, ArenaDataObject> dataObjects = new HashMap<>();
	
	private Location center;
	private int lx, ly, lz;
	private int mx, mz;
	
	public BaseSchematicData(BaseSchematic schematic, Arena arena) {
		super(schematic, arena);
		arena.getRegistry().handle(this);
	}
	
	@Override
	public BaseSchematic getSchematic() {
		return (BaseSchematic)super.getSchematic();
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
		Arena arena = getArena();
		World world = arena.getWorld();
		BukkitThreads.sync(() -> {
			for(int cx = lcx; cx <= mcx; cx++) {
				for(int cz = lcz; cz <= mcz; cz++) {
					Chunk chunk = world.getChunkAt(cx, cz);
					ChunkLoader.keepInMemory(arena, chunk);
					if(!chunk.isLoaded()) chunk.load();
				}
			}
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
		if(!(o instanceof BaseSchematicData)) return false;
		BaseSchematicData d = (BaseSchematicData)o;
		return d.getArena().getName().equals(getArena().getName()) && d.getSchematic().getName().equals(getSchematic().getName());
	}
	
}
