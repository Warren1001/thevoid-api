package com.kabryxis.thevoid.api.arena.impl;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.kabryxis.kabutils.random.Weighted;
import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.kabutils.spigot.world.ChunkLoader;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.ArenaEntry;
import com.kabryxis.thevoid.api.arena.object.ArenaDataObjectRegistry;
import com.kabryxis.thevoid.api.arena.schematic.ArenaData;
import com.kabryxis.thevoid.api.arena.schematic.BaseArenaData;
import com.kabryxis.thevoid.api.arena.schematic.BaseSchematic;
import com.kabryxis.thevoid.api.arena.schematic.Schematic;
import com.kabryxis.thevoid.api.arena.schematic.impl.VoidArenaData;
import com.kabryxis.thevoid.api.arena.schematic.impl.VoidBaseArenaData;
import com.kabryxis.thevoid.api.arena.schematic.util.SchematicEntry;
import com.sk89q.worldedit.EditSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VoidArena implements Arena, Weighted {
	
	public final static String PATH = "plugins" + File.separator + "TheVoid" + File.separator + "arenas" + File.separator;
	
	private final Map<Schematic, ArenaData> arenaDatas = new HashMap<>(); // TODO consider splitting normal vs base versions into seperate maps
	private final Queue<BaseArenaData> schematics = new ConcurrentLinkedQueue<>();
	
	private final ArenaDataObjectRegistry dataObjectRegistry;
	private final Config data;
	private final Location location;
	private final EditSession editSession;
	private final int weight;
	
	private BaseArenaData currentArenaData;
	private Set<ArenaData> otherCurrentSchematics;
	
	private Set<Entity> spawnedEntities = new HashSet<>();
	
	public VoidArena(ArenaDataObjectRegistry dataObjectRegistry, Config data) {
		this.dataObjectRegistry = dataObjectRegistry;
		this.data = data;
		this.location = new Location(Bukkit.getWorld(data.getString("world")), data.getDouble("x"), data.getDouble("y"), data.getDouble("z"));
		this.editSession = new EditSessionBuilder(FaweAPI.getWorld(getWorldName())).fastmode(true).build();
		this.weight = data.getInt("weight");
	}
	
	@Override
	public String getName() {
		return data.getName();
	}
	
	@Override
	public Location getLocation() {
		return location;
	}
	
	@Override
	public ArenaDataObjectRegistry getRegistry() {
		return dataObjectRegistry;
	}
	
	public EditSession getEditSession() {
		return editSession;
	}
	
	@Override
	public void queueSchematics(List<? extends BaseSchematic> list) {
		for(BaseSchematic baseSchematic : list) {
			schematics.add(getArenaData(baseSchematic));
		}
	}
	
	private BaseArenaData getArenaData(BaseSchematic schematic) {
		return (BaseArenaData)arenaDatas.computeIfAbsent(schematic, s -> new VoidBaseArenaData(this, schematic));
		/*if(arenaDatas.containsKey(schematic)) return (BaseArenaData)arenaDatas.get(schematic);
		VoidBaseArenaData arenaData = new VoidBaseArenaData(this, schematic);
		Set<SchematicEntry> schematicEntries = schematic.getSchematicEntries();
		Set<ArenaEntry> arenaEntries = new HashSet<>(schematicEntries.size());
		Location trueStart = location.clone().subtract(schematic.getCenterX(), schematic.getCenterY(), schematic.getCenterZ());
		int lx = Integer.MAX_VALUE, ly = Integer.MAX_VALUE, lz = Integer.MAX_VALUE;
		int mx = Integer.MIN_VALUE, my = Integer.MIN_VALUE, mz = Integer.MIN_VALUE;
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
			arenaData.forEachDataObject(object -> object.next(schematicEntry, arenaEntry));
		}
		arenaData.setArenaEntries(arenaEntries);
		arenaData.setMinsAndMaxs(lx, ly, lz, mx, my, mz);
		arenaDatas.put(schematic, arenaData);
		return arenaData;*/
	}
	
	@Override
	public void nextSchematic() {
		this.currentArenaData = schematics.poll();
	}
	
	@Override
	public void loadSchematic() {
		currentArenaData.loadSchematic();
	}
	
	@Override
	public void eraseSchematic() {
		if(hasNextArenaData() && getNextArenaData().equals(currentArenaData)) return;
		currentArenaData.eraseSchematic();
		if(otherCurrentSchematics != null && !otherCurrentSchematics.isEmpty()) {
			otherCurrentSchematics.forEach(ArenaData::eraseSchematic);
			otherCurrentSchematics.clear();
		}
		ChunkLoader.releaseFromMemory(this);
	}
	
	@Override
	public ArenaData loadAnotherSchematic(Schematic schematic) {
		return loadAnotherSchematic(schematic, 0, 0, 0);
	}
	
	@Override
	public ArenaData loadAnotherSchematic(Schematic schematic, int offsetX, int offsetY, int offsetZ) {
		if(arenaDatas.containsKey(schematic)) {
			ArenaData arenaData = arenaDatas.get(schematic);
			otherCurrentSchematics.add(arenaData);
			arenaData.loadSchematic();
			return arenaData;
		}
		if(otherCurrentSchematics == null) otherCurrentSchematics = new HashSet<>(3);
		VoidArenaData arenaData = new VoidArenaData(this, schematic);
		Set<SchematicEntry> schematicEntries = schematic.getSchematicEntries();
		Set<ArenaEntry> arenaEntries = new HashSet<>(schematicEntries.size());
		schematicEntries.forEach(entry -> arenaEntries.add(entry.toArenaEntry(location.getBlockX() + offsetX, location.getBlockY() + offsetY, location.getBlockZ() + offsetZ)));
		arenaData.setArenaEntries(arenaEntries);
		arenaDatas.put(schematic, arenaData);
		otherCurrentSchematics.add(arenaData);
		arenaData.loadSchematic();
		return arenaData;
	}
	
	@Override
	public ArenaData getArenaData(Schematic schematic) {
		return arenaDatas.get(schematic);
	}
	
	@Override
	public BaseArenaData getCurrentArenaData() {
		return currentArenaData;
	}
	
	public BaseArenaData getNextArenaData() {
		return schematics.peek();
	}
	
	public boolean hasNextArenaData() {
		return !schematics.isEmpty();
	}
	
	@Override
	public void endOfRound() {
		if(!spawnedEntities.isEmpty()) {
			spawnedEntities.forEach(Entity::remove);
			spawnedEntities.clear();
		}
	}
	
	@Override
	public void spawnedEntity(Entity entity) {
		spawnedEntities.add(entity);
	}
	
	@Override
	public int getWeight() {
		return weight;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof VoidArena && ((VoidArena)obj).getName().equals(getName());
	}
	
}
