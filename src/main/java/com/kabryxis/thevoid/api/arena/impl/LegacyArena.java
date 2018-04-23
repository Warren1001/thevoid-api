package com.kabryxis.thevoid.api.arena.impl;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.kabryxis.kabutils.random.Weighted;
import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.kabutils.spigot.world.ChunkLoader;
import com.kabryxis.thevoid.api.arena.ArenaEntry;
import com.kabryxis.thevoid.api.arena.object.IArenaDataObjectRegistry;
import com.kabryxis.thevoid.api.arena.schematic.impl.LegacyBaseSchematic;
import com.kabryxis.thevoid.api.arena.schematic.impl.LegacyBaseSchematicData;
import com.kabryxis.thevoid.api.arena.schematic.impl.LegacySchematic;
import com.kabryxis.thevoid.api.arena.schematic.impl.LegacySchematicData;
import com.kabryxis.thevoid.api.arena.schematic.util.SchematicEntry;
import com.sk89q.worldedit.EditSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LegacyArena implements Weighted {
	
	public final static String PATH = "plugins" + File.separator + "TheVoid" + File.separator + "arenas" + File.separator;
	
	private final Map<LegacySchematic, LegacySchematicData> arenaDatas = new HashMap<>();
	private final Queue<LegacyBaseSchematicData> schematics = new ConcurrentLinkedQueue<>();
	
	private final IArenaDataObjectRegistry registry;
	private final String name;
	private final int weight;
	private final Location start;
	private final World world;
	private final EditSession editSession;
	
	private LegacyBaseSchematicData currentSchematicData;
	private Set<LegacySchematicData> otherCurrentSchematics;
	
	private Set<Entity> spawnedEntities;
	
	public LegacyArena(IArenaDataObjectRegistry registry, Config config) {
		this.registry = registry;
		this.name = config.getName();
		this.weight = config.getInt("weight");
		this.start = new Location(Bukkit.getWorld(config.getString("world")), config.getDouble("x"), config.getDouble("y"), config.getDouble("z"));
		this.world = start.getWorld();
		this.editSession = new EditSessionBuilder(FaweAPI.getWorld(world.getName())).fastmode(true).build();
	}
	
	public LegacyArena(IArenaDataObjectRegistry registry, String name, int weight, Location start) {
		this.registry = registry;
		this.name = name;
		this.weight = weight;
		this.start = start;
		this.world = start.getWorld();
		this.editSession = new EditSessionBuilder(FaweAPI.getWorld(world.getName())).fastmode(true).build();
	}
	
	public IArenaDataObjectRegistry getRegistry() {
		return registry;
	}
	
	public String getName() {
		return name;
	}
	
	public World getWorld() {
		return world;
	}
	
	public String getWorldName() {
		return world.getName();
	}
	
	public EditSession getEditSession() {
		return editSession;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public Location getLocation() {
		return start;
	}
	
	public void queueSchematics(List<? extends LegacyBaseSchematic> list) {
		for(LegacyBaseSchematic schematic : list) {
			schematics.add(getSchematicData(schematic));
		}
	}
	
	private LegacyBaseSchematicData getSchematicData(LegacyBaseSchematic schematic) {
		LegacyBaseSchematicData schematicData = new LegacyBaseSchematicData(schematic, this);
		Set<ArenaEntry> arenaData = new HashSet<>();
		List<SchematicEntry> data = schematic.getSchematicData();
		Location center = start.clone();
		int lx = Integer.MAX_VALUE, ly = Integer.MAX_VALUE, lz = Integer.MAX_VALUE;
		int mx = Integer.MIN_VALUE, mz = Integer.MIN_VALUE;
		double halfX = schematic.getSizeX() / 2.0, halfZ = schematic.getSizeZ() / 2.0;
		for(SchematicEntry schematicEntry : data) {
			int x = schematicEntry.getX() + center.getBlockX() - (int)Math.ceil(halfX), y = schematicEntry.getY() + center.getBlockY(), z = schematicEntry.getZ() + center.getBlockZ() - (int)Math.ceil(halfZ);
			if(x < lx) lx = x;
			if(x > mx) mx = x;
			if(y < ly) ly = y;
			if(z < lz) lz = z;
			if(z > mz) mz = z;
			ArenaEntry arenaEntry = new ArenaEntry(x, y, z, schematicEntry.getType().getId(), schematicEntry.getData());
			arenaData.add(arenaEntry);
			schematicData.forEachDataObject(object -> object.next(schematicEntry, arenaEntry));
		}
		schematicData.setCurrentArenaData(arenaData);
		schematicData.setCenter(center);
		schematicData.setMinsAndMaxs(lx, ly, lz, mx, mz);
		arenaDatas.put(schematic, schematicData);
		return schematicData;
	}
	
	public LegacySchematicData loadAnotherSchematic(LegacySchematic schematic) {
		if(arenaDatas.containsKey(schematic)) {
			LegacySchematicData schematicData = arenaDatas.get(schematic);
			otherCurrentSchematics.add(schematicData);
			schematicData.loadSchematic();
			return schematicData;
		}
		if(otherCurrentSchematics == null) otherCurrentSchematics = new HashSet<>(3);
		LegacySchematicData schematicData = new LegacySchematicData(schematic, this);
		Set<ArenaEntry> arenaData = new HashSet<>();
		List<SchematicEntry> data = schematic.getSchematicData();
		Location center = getCurrentSchematicData().getCenter();
		double halfX = schematic.getSizeX() / 2.0, halfZ = schematic.getSizeZ() / 2.0;
		for(SchematicEntry entry : data) {
			int x = (int)Math.ceil((double)entry.getX() + center.getX() - halfX) - 1, y = (int)Math.ceil((double)entry.getY() + center.getY()), z = (int)Math.ceil((double)entry.getZ() + center.getZ() - halfZ);
			arenaData.add(new ArenaEntry(x, y, z, entry.getType().getId(), entry.getData()));
		}
		schematicData.setCurrentArenaData(arenaData);
		arenaDatas.put(schematic, schematicData);
		otherCurrentSchematics.add(schematicData);
		schematicData.loadSchematic();
		return schematicData;
	}
	
	public void nextSchematic() {
		this.currentSchematicData = schematics.poll();
	}
	
	public void loadSchematic() {
		currentSchematicData.loadSchematic();
	}
	
	public void eraseSchematic() {
		if(hasNextArenaData() && getNextArenaData().equals(currentSchematicData)) return;
		ChunkLoader.releaseFromMemory(this);
		currentSchematicData.eraseSchematic();
		if(otherCurrentSchematics != null && !otherCurrentSchematics.isEmpty()) {
			otherCurrentSchematics.forEach(LegacySchematicData::eraseSchematic);
			spawnedEntities.forEach(Entity::remove);
			spawnedEntities.clear();
		}
	}
	
	public void spawnedCustomEntity(Entity entity) {
		if(spawnedEntities == null) spawnedEntities = new HashSet<>();
		spawnedEntities.add(entity);
	}
	
	public Location getCenter() {
		return currentSchematicData.getCenter();
	}
	
	public LegacyBaseSchematicData getCurrentSchematicData() {
		return currentSchematicData;
	}
	
	public LegacyBaseSchematicData getNextArenaData() {
		return schematics.peek();
	}
	
	public boolean hasNextArenaData() {
		return !schematics.isEmpty();
	}
	
	public LegacySchematicData getSchematicData(LegacySchematic schematic) {
		return arenaDatas.get(schematic);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof LegacyArena && ((LegacyArena)obj).start.equals(start);
	}
	
}
