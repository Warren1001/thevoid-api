package com.kabryxis.thevoid.api.arena;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.kabryxis.kabutils.data.Arrays;
import com.kabryxis.kabutils.data.Lists;
import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.kabutils.spigot.version.wrapper.world.world.WrappedWorld;
import com.kabryxis.kabutils.spigot.world.WorldManager;
import com.kabryxis.thevoid.api.schematic.Schematic;

public class Arena {
	
	public final static String PATH = "plugins" + File.separator + "TheVoid" + File.separator + "arenas" + File.separator;
	
	private final Queue<SchematicData> schematics = new ConcurrentLinkedQueue<>();
	private final Random random = new Random();
	
	private final String name;
	private final ArenaOrientation orientation;
	private final Location start;
	private final WrappedWorld<?> world;
	
	private SchematicData currentSchematicData;
	
	private Set<Entity> spawnedEntities = new HashSet<>();
	
	public Arena(Config config) {
		this.name = config.getName();
		this.orientation = ArenaOrientation.valueOf(config.getString("orientation"));
		this.start = new Location(Bukkit.getWorld(config.getString("world")), config.getDouble("x"), config.getDouble("y"), config.getDouble("z"));
		this.world = WorldManager.getWorld(start.getWorld());
	}
	
	public Arena(String name, ArenaOrientation orientation, Location start) {
		this.name = name;
		this.orientation = orientation;
		this.start = start;
		this.world = WorldManager.getWorld(start.getWorld());
	}
	
	public String getName() {
		return name;
	}
	
	public ArenaOrientation getOrientation() {
		return orientation;
	}
	
	public WrappedWorld<?> getWorld() {
		return world;
	}
	
	public String getWorldName() {
		return world.getName();
	}
	
	public void queueSchematics(List<? extends Schematic> list) {
		Map<Schematic, SchematicData> alreadyCreated = new HashMap<>();
		for(Schematic schematic : list) {
			if(alreadyCreated.containsKey(schematic)) {
				schematics.add(alreadyCreated.get(schematic));
				continue;
			}
			Map<Long, List<int[]>> schematicChunkData = new HashMap<>();
			int[][] schematicData = schematic.getSchematicData();
			Config data = schematic.getData();
			Location center = start.clone();
			if(orientation == ArenaOrientation.BOTTOM) center = center.add(data.getInt("center.x"), data.getInt("center.y"), data.getInt("center.z"));
			String walkableString = data.getString("walkable");
			int[] walkableYs = null;
			List<Location> walkableLocs = null;
			if(walkableString != null) {
				if(walkableString.contains(",")) {
					String[] walkableSplit = walkableString.split(",");
					if(walkableSplit.length > 0) {
						int size = walkableSplit.length;
						walkableYs = new int[size];
						walkableLocs = new ArrayList<>(size);
						for(int i = 0; i < walkableSplit.length; i++) {
							walkableYs[i] = Integer.parseInt(walkableSplit[i]);
						}
					}
				}
				else {
					walkableYs = new int[] { Integer.parseInt(walkableString) };
					walkableLocs = new ArrayList<>();
				}
			}
			for(int[] d : schematicData) {
				int y = d[1], trueX = d[0] + center.getBlockX(), trueY = y + center.getBlockY(), trueZ = d[2] + center.getBlockZ();
				schematicChunkData.computeIfAbsent(world.toLong(trueX >> 4, trueZ >> 4), l -> new ArrayList<>()).add(new int[] { trueX & 0x0f, trueY, trueZ & 0x0f, d[3], d[4] });
				if(walkableLocs != null && Arrays.containsInt(walkableYs, y)) walkableLocs.add(new Location(world.getWorld(), trueX, trueY, trueZ));
			}
			SchematicData sd = new SchematicData(schematic, center, schematicChunkData, walkableLocs);
			alreadyCreated.put(schematic, sd);
			schematics.add(sd);
		}
	}
	
	public void nextSchematic() {
		this.currentSchematicData = schematics.poll();
	}
	
	public void loadSchematic() {
		world.loadSchematic(currentSchematicData.getCurrentChunkData());
	}
	
	public void eraseSchematic() {
		if(hasNextSchematicData() && getNextSchematicData().equals(currentSchematicData)) return;
		world.eraseSchematic(currentSchematicData.getCurrentChunkData());
		if(schematics.isEmpty()) WorldManager.removeChunksFromMemory(name);
	}
	
	public void loadChunks() {
		WorldManager.removeChunksFromMemory(name);
		for(Long key : currentSchematicData.getCurrentChunkData().keySet()) {
			WorldManager.keepChunkInMemory(name, key);
			int cx = world.getChunkX(key), cz = world.getChunkZ(key);
			Chunk chunk = world.getWorld().getChunkAt(cx, cz);
			if(!chunk.isLoaded()) chunk.load();
		}
	}
	
	public void setBlock(int x, int y, int z, int type, byte data) {
		world.setBlock(start.getBlockX() + x, start.getBlockY() + y, start.getBlockZ() + z, type, data);
	}
	
	public void setBlock(Vector vector, int type, byte data) {
		setBlock(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), type, data);
	}
	
	@SuppressWarnings("deprecation")
	public void setBlock(Vector vector, Material type, byte data) {
		setBlock(vector, type.getId(), data);
	}
	
	public void setBlock(Vector vector, Material type) {
		setBlock(vector, type, (byte)0);
	}
	
	public void setBlockFast(int x, int y, int z, int type, byte data) {
		world.setBlockFast(start.getBlockX() + x, start.getBlockY() + y, start.getBlockZ() + z, type, data);
	}
	
	public Block getBlock(int x, int y, int z) {
		return world.getBlock(start.getBlockX() + x, start.getBlockY() + y, start.getBlockZ() + z);
	}
	
	public int getBlockId(int x, int y, int z) {
		return world.getBlockId(start.getBlockX() + x, start.getBlockY() + y, start.getBlockZ() + z);
	}
	
	public void setMetadata(int x, int y, int z, String key, MetadataValue value) {
		world.setMetadata(start.getBlockX() + x, start.getBlockY() + y, start.getBlockZ() + z, key, value);
	}
	
	public void removeMetadata(int x, int y, int z, String key, Plugin plugin) {
		world.removeMetadata(start.getBlockX() + x, start.getBlockY() + y, start.getBlockZ() + z, key, plugin);
	}
	
	public boolean hasMetadata(int x, int y, int z, String key) {
		return world.hasMetadata(start.getBlockX() + x, start.getBlockY() + y, start.getBlockZ() + z, key);
	}
	
	public List<Location> getWalkableLocations() {
		return Lists.cloneCopy(currentSchematicData.getWalkableLocations());
	}
	
	public Location getRandomWalkableLocation() {
		List<Location> walkableLocs = currentSchematicData.getWalkableLocations();
		if(walkableLocs == null) return null;
		return walkableLocs.get(random.nextInt(walkableLocs.size()));
	}
	
	public void endOfRound() {
		spawnedEntities.forEach(Entity::remove);
		spawnedEntities.clear();
	}
	
	public <T extends Entity> T spawnEntity(Vector vector, Class<T> clazz) {
		T entity = world.getWorld().spawn(getWorldLocation(vector), clazz);
		spawnedEntities.add(entity);
		return entity;
	}
	
	public void spawnedCustomEntity(Entity entity) {
		spawnedEntities.add(entity);
	}
	
	public Vector getArenaLocation(Location loc) {
		return new Vector(loc.getX() - start.getBlockX(), loc.getY() - start.getBlockY(), loc.getZ() - start.getBlockZ());
	}
	
	public Location getWorldLocation(Vector vec) {
		return new Location(world.getWorld(), start.getBlockX() + vec.getX(), start.getBlockY() + vec.getY(), start.getBlockZ() + vec.getZ());
	}
	
	public Location getCenter() {
		return currentSchematicData.getCenter();
	}
	
	/**
	 * Gets the current Schematic of the Arena.
	 * 
	 * @return The Schematic in the Arena.
	 */
	public SchematicData getCurrentSchematicData() {
		return currentSchematicData;
	}
	
	public SchematicData getNextSchematicData() {
		return schematics.peek();
	}
	
	public boolean hasNextSchematicData() {
		return !schematics.isEmpty();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Arena ? ((Arena)obj).getName().equals(name) : false;
	}
	
}
