package com.kabryxis.thevoid.api.impl.arena.schematic;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.util.arena.ArenaEntry;
import com.kabryxis.thevoid.api.impl.arena.VoidArena;
import com.kabryxis.thevoid.api.arena.object.ArenaDataObject;
import com.kabryxis.thevoid.api.arena.schematic.BaseArenaData;
import com.kabryxis.thevoid.api.arena.schematic.BaseSchematic;
import com.kabryxis.thevoid.api.util.arena.schematic.SchematicEntry;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;
import java.util.function.Consumer;

public class VoidBaseArenaData extends VoidArenaData implements BaseArenaData {
	
	private Location center;
	private Map<Class<? extends ArenaDataObject>, ArenaDataObject> dataObjects;
	private int lx, ly, lz, mx, my, mz;
	
	public VoidBaseArenaData(VoidArena arena, BaseSchematic schematic) {
		super(arena, schematic);
		center = arena.getLocation().clone().add(0, 0.75, 0);
		if(schematic.isOdd()) center.add(0.5, 0, 0.5);
		Set<ArenaEntry> arenaEntries;
		int lx, ly, lz, mx, my, mz;
		if(schematic instanceof VoidEmptySchematic) {
			arenaEntries = Collections.emptySet();
			Location loc = arena.getLocation();
			int x = loc.getBlockX(), z = loc.getBlockZ();
			lx = x - 32;
			ly = loc.getBlockY();
			lz = z - 32;
			mx = x + 32;
			my = 0;
			mz = z + 32;
		}
		else {
			arena.getRegistry().handle(this);
			Set<SchematicEntry> schematicEntries = schematic.getSchematicEntries();
			arenaEntries = new HashSet<>(schematicEntries.size());
			Location trueStart = arena.getLocation().clone().subtract(schematic.getCenterX(), schematic.getCenterY(), schematic.getCenterZ());
			lx = Integer.MAX_VALUE;
			ly = Integer.MAX_VALUE;
			lz = Integer.MAX_VALUE;
			mx = Integer.MIN_VALUE;
			my = Integer.MIN_VALUE;
			mz = Integer.MIN_VALUE;
			for(SchematicEntry schematicEntry : schematicEntries) {
				ArenaEntry arenaEntry = schematicEntry.toArenaEntry(trueStart.getBlockX(), trueStart.getBlockY(), trueStart.getBlockZ());
				com.sk89q.worldedit.Vector pos = arenaEntry.getPos();
				int x = pos.getBlockX(), y = pos.getBlockY(), z = pos.getBlockZ();
				if(x < lx) lx = x;
				if(x > mx) mx = x;
				if(y < ly) ly = y;
				if(y > my) my = y;
				if(z < lz) lz = z;
				if(z > mz) mz = z;
				arenaEntries.add(arenaEntry);
				forEachDataObject(object -> object.next(schematicEntry, arenaEntry));
			}
		}
		setArenaEntries(arenaEntries);
		setMinsAndMaxs(lx, ly, lz, mx, my, mz);
	}
	
	/*public VoidBaseArenaData(VoidArena arena, BaseSchematic schematic) {
		super(arena, schematic);
		center = arena.getLocation().clone().add(0, 0.75, 0);
		if(schematic.isOdd()) center.add(0.5, 0, 0.5);
		arena.getRegistry().handle(this);
	}*/
	
	@Override
	public BaseSchematic getSchematic() {
		return (BaseSchematic)super.getSchematic();
	}
	
	@Override
	public Location getCenter() {
		return center;
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
	public double getRadius() {
		return getSchematic().getRadius();
	}
	
	@Override
	public void setMinsAndMaxs(int lx, int ly, int lz, int mx, int my, int mz) {
		this.lx = lx;
		this.ly = ly;
		this.lz = lz;
		this.mx = mx;
		this.my = my;
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
		int radius = 3;
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
				((VoidArena)arena).getChunkLoader().keepInMemory(arena, chunk); // TODO
				if(!chunk.isLoaded()) chunk.load();
			});
		});
	}
	
}
