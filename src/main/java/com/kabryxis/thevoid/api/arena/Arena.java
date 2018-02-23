package com.kabryxis.thevoid.api.arena;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.kabryxis.thevoid.api.arena.object.ArenaDataObjectRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.kabryxis.kabutils.cache.Cache;
import com.kabryxis.kabutils.data.Lists;
import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.kabutils.spigot.version.wrapper.world.world.WrappedWorld;
import com.kabryxis.kabutils.spigot.world.ChunkEntry;
import com.kabryxis.kabutils.spigot.world.WorldManager;
import com.kabryxis.thevoid.api.arena.object.ArenaWalkable;
import com.kabryxis.thevoid.api.schematic.Schematic;
import com.kabryxis.thevoid.api.schematic.SchematicEntry;

public class Arena {
	
	public final static String PATH = "plugins" + File.separator + "TheVoid" + File.separator + "arenas" + File.separator;
	
	private final Map<Schematic, ArenaData> arenaDatas = new HashMap<>();
	private final Queue<ArenaData> schematics = new ConcurrentLinkedQueue<>();
	
	private final ArenaDataObjectRegistry registry;
	private final String name;
	private final boolean orientation;
	private final Location start;
	private final WrappedWorld<?> world;
	
	private ArenaData currentArenaData;
	private Set<ArenaData> otherCurrentSchematics;
	
	private Set<Entity> spawnedEntities;
	
	public Arena(ArenaDataObjectRegistry registry, Config config) {
		this.registry = registry;
		this.name = config.getName();
		this.orientation = config.getBoolean("orientation");
		this.start = new Location(Bukkit.getWorld(config.getString("world")), config.getDouble("x"), config.getDouble("y"), config.getDouble("z"));
		this.world = WorldManager.getWorld(start.getWorld());
	}
	
	public Arena(ArenaDataObjectRegistry registry, String name, boolean orientation, Location start) {
		this.registry = registry;
		this.name = name;
		this.orientation = orientation;
		this.start = start;
		this.world = WorldManager.getWorld(start.getWorld());
	}
	
	public ArenaDataObjectRegistry getRegistry() {
		return registry;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean getOrientation() {
		return orientation;
	}
	
	public WrappedWorld<?> getWorld() {
		return world;
	}
	
	public String getWorldName() {
		return world.getName();
	}
	
	public Location getLocation() {
		return start;
	}
	
	public void queueSchematics(List<? extends Schematic> list) {
		for(Schematic schematic : list) {
			if(arenaDatas.containsKey(schematic)) {
				schematics.add(arenaDatas.get(schematic));
				continue;
			}
			ArenaData arenaData = new ArenaData(schematic, this);
			Map<Long, List<ChunkEntry>> chunkData = new HashMap<>();
			List<SchematicEntry> schematicData = schematic.getSchematicData();
			Location center = start.clone();
			int lowestY = Integer.MAX_VALUE;
			double halfX = schematic.getSizeX() / 2.0, halfY = schematic.getSizeY() / 2.0, halfZ = schematic.getSizeZ() / 2.0;
			for(SchematicEntry entry : schematicData) {
				int tempX = entry.getX() + center.getBlockX(), tempY = entry.getY() + center.getBlockY(), tempZ = entry.getZ() + center.getBlockZ();
				if(orientation) {
					tempX -= halfX;
					tempY -= halfY;
					tempZ -= halfZ;
				}
				int trueX = tempX, trueY = tempY, trueZ = tempZ;
				ChunkEntry chunkEntry = Cache.get(ChunkEntry.class);
				chunkEntry.reuse(trueX & 0x0f, trueY, trueZ & 0x0f, entry.getType(), entry.getData());
				chunkData.computeIfAbsent(world.toLong(trueX >> 4, trueZ >> 4), Lists.getGenericCreator()).add(chunkEntry);
				arenaData.forEachDataObject(object -> object.next(entry, trueX, trueY, trueZ));
				if(trueY < lowestY) lowestY = trueY;
			}
			if(!orientation) center.add(schematic.getCenterX(), schematic.getCenterY(), schematic.getCenterZ());
			arenaData.setCurrentChunkData(chunkData);
			arenaData.setCenter(center);
			arenaDatas.put(schematic, arenaData);
			schematics.add(arenaData);
		}
	}
	
	public void loadAnotherSchematic(Schematic schematic) {
		if(otherCurrentSchematics == null) otherCurrentSchematics = new HashSet<>(3);
		//otherCurrentSchematics.add(schematic);
	}
	
	public void nextSchematic() {
		this.currentArenaData = schematics.poll();
	}
	
	public void loadSchematic() {
		currentArenaData.loadSchematic();
	}
	
	public void eraseSchematic() {
		if(hasNextArenaData() && getNextArenaData().equals(currentArenaData)) return;
		world.eraseSchematic(currentArenaData.getCurrentChunkData());
		if(schematics.isEmpty()) WorldManager.removeChunksFromMemory(name);
	}
	
	public void loadChunks() {
		WorldManager.removeChunksFromMemory(name);
		for(Long key : currentArenaData.getCurrentChunkData().keySet()) {
			WorldManager.keepChunkInMemory(name, key);
			int cx = world.getChunkX(key), cz = world.getChunkZ(key);
			Chunk chunk = world.getWorld().getChunkAt(cx, cz);
			if(!chunk.isLoaded()) chunk.load();
		}
	}
	
	public void setBlock(int x, int y, int z, Material type, byte data) {
		world.setBlock(start.getBlockX() + x, start.getBlockY() + y, start.getBlockZ() + z, type, data);
	}
	
	public void setBlock(int x, int y, int z, Material type) {
		setBlock(x, y, z, type, (byte)0);
	}
	
	public void setBlockFast(int x, int y, int z, Material type, byte data) {
		world.setBlockFast(start.getBlockX() + x, start.getBlockY() + y, start.getBlockZ() + z, type, data);
	}
	
	public Block getBlock(int x, int y, int z) {
		return world.getBlock(start.getBlockX() + x, start.getBlockY() + y, start.getBlockZ() + z);
	}
	
	public List<Location> getWalkableLocations() {
		return Lists.softCopy(((ArenaWalkable)currentArenaData.getDataObject("walkable")).get());
	}
	
	public void endOfRound() {
		if(spawnedEntities != null) {
			spawnedEntities.forEach(Entity::remove);
			spawnedEntities.clear();
		}
	}
	
	public <T extends Entity> T spawnEntity(double x, double y, double z, Class<T> clazz) {
		T entity = world.getWorld().spawn(getWorldLocation(x, y, z), clazz);
		spawnedCustomEntity(entity);
		return entity;
	}
	
	public void spawnedCustomEntity(Entity entity) {
		if(spawnedEntities == null) spawnedEntities = new HashSet<>();
		spawnedEntities.add(entity);
	}
	
	public Vector getArenaLocation(Location loc) {
		return new Vector(loc.getX() - start.getBlockX(), loc.getY() - start.getBlockY(), loc.getZ() - start.getBlockZ());
	}
	
	public Location getWorldLocation(double x, double y, double z) {
		return new Location(world.getWorld(), start.getX() + x, start.getY() + y, start.getZ() + z);
	}
	
	public Location getCenter() {
		return currentArenaData.getCenter();
	}
	
	/**
	 * Gets the current Schematic of the Arena.
	 * 
	 * @return The Schematic in the Arena.
	 */
	public ArenaData getCurrentArenaData() {
		return currentArenaData;
	}
	
	public ArenaData getNextArenaData() {
		return schematics.peek();
	}
	
	public boolean hasNextArenaData() {
		return !schematics.isEmpty();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Arena && ((Arena)obj).start.equals(start);
	}
	
}
