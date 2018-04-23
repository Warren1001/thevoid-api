package com.kabryxis.thevoid.api.arena.impl;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.kabryxis.kabutils.random.Weighted;
import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.kabutils.spigot.world.ChunkLoader;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.ArenaEntry;
import com.kabryxis.thevoid.api.arena.object.IArenaDataObjectRegistry;
import com.kabryxis.thevoid.api.arena.schematic.ArenaData;
import com.kabryxis.thevoid.api.arena.schematic.BaseArenaData;
import com.kabryxis.thevoid.api.arena.schematic.IBaseSchematic;
import com.kabryxis.thevoid.api.arena.schematic.ISchematic;
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
	
	private final Map<ISchematic, ArenaData> arenaDatas = new HashMap<>();
	private final Queue<BaseArenaData> schematics = new ConcurrentLinkedQueue<>();
	
	private final IArenaDataObjectRegistry dataObjectRegistry;
	private final Config data;
	private final Location location;
	private final EditSession editSession;
	private final int weight;
	
	private BaseArenaData currentArenaData;
	private Set<ArenaData> otherCurrentSchematics;
	
	private Set<Entity> spawnedEntities;
	
	public VoidArena(IArenaDataObjectRegistry dataObjectRegistry, Config data) {
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
	public IArenaDataObjectRegistry getRegistry() {
		return dataObjectRegistry;
	}
	
	public EditSession getEditSession() {
		return editSession;
	}
	
	@Override
	public void queueSchematics(List<? extends IBaseSchematic> list) {
		for(IBaseSchematic baseSchematic : list) {
			schematics.add(getArenaData(baseSchematic));
		}
	}
	
	private BaseArenaData getArenaData(IBaseSchematic schematic) {
		if(arenaDatas.containsKey(schematic)) return (BaseArenaData)arenaDatas.get(schematic);
		VoidBaseArenaData arenaData = new VoidBaseArenaData(this, schematic);
		Set<ArenaEntry> arenaEntries = new HashSet<>();
		List<SchematicEntry> schematicEntries = schematic.getSchematicEntries();
		Location center = location.clone();
		int lx = Integer.MAX_VALUE, ly = Integer.MAX_VALUE, lz = Integer.MAX_VALUE;
		int mx = Integer.MIN_VALUE, mz = Integer.MIN_VALUE;
		double halfX = schematic.getSizeX() / 2.0, halfZ = schematic.getSizeZ() / 2.0;
		for(SchematicEntry schematicEntry : schematicEntries) {
			int x = schematicEntry.getX() + center.getBlockX() - (int)Math.ceil(halfX), y = schematicEntry.getY() + center.getBlockY(), z = schematicEntry.getZ() + center.getBlockZ() - (int)Math.ceil(halfZ);
			if(x < lx) lx = x;
			if(x > mx) mx = x;
			if(y < ly) ly = y;
			if(z < lz) lz = z;
			if(z > mz) mz = z;
			ArenaEntry arenaEntry = new ArenaEntry(x, y, z, schematicEntry.getType().getId(), schematicEntry.getData());
			arenaEntries.add(arenaEntry);
			arenaData.forEachDataObject(object -> object.next(schematicEntry, arenaEntry));
		}
		arenaData.setArenaEntries(arenaEntries);
		//arenaData.setCenter(center);
		arenaData.setMinsAndMaxs(lx, ly, lz, mx, mz);
		arenaDatas.put(schematic, arenaData);
		return arenaData;
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
	public ArenaData loadAnotherSchematic(ISchematic schematic) {
		if(arenaDatas.containsKey(schematic)) {
			ArenaData arenaData = arenaDatas.get(schematic);
			otherCurrentSchematics.add(arenaData);
			arenaData.loadSchematic();
			return arenaData;
		}
		if(otherCurrentSchematics == null) otherCurrentSchematics = new HashSet<>(3);
		VoidArenaData arenaData = new VoidArenaData(this, schematic);
		Set<ArenaEntry> arenaEntries = new HashSet<>();
		List<SchematicEntry> schematicEntries = schematic.getSchematicEntries();
		Location center = location.clone();
		double halfX = schematic.getSizeX() / 2.0, halfZ = schematic.getSizeZ() / 2.0;
		for(SchematicEntry entry : schematicEntries) {
			int x = (int)Math.ceil((double)entry.getX() + center.getX() - halfX) - 1, y = (int)Math.ceil((double)entry.getY() + center.getY()), z = (int)Math.ceil((double)entry.getZ() + center.getZ() - halfZ);
			arenaEntries.add(new ArenaEntry(x, y, z, entry.getType().getId(), entry.getData()));
		}
		arenaData.setArenaEntries(arenaEntries);
		arenaDatas.put(schematic, arenaData);
		otherCurrentSchematics.add(arenaData);
		arenaData.loadSchematic();
		return arenaData;
	}
	
	@Override
	public ArenaData getArenaData(ISchematic schematic) {
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
		if(spawnedEntities != null) {
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
