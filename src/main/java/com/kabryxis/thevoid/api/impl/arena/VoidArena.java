package com.kabryxis.thevoid.api.impl.arena;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.kabutils.spigot.world.ChunkLoader;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.util.arena.ArenaEntry;
import com.kabryxis.thevoid.api.arena.object.ArenaDataObjectRegistry;
import com.kabryxis.thevoid.api.arena.schematic.ArenaData;
import com.kabryxis.thevoid.api.arena.schematic.BaseArenaData;
import com.kabryxis.thevoid.api.arena.schematic.BaseSchematic;
import com.kabryxis.thevoid.api.arena.schematic.Schematic;
import com.kabryxis.thevoid.api.impl.arena.schematic.VoidArenaData;
import com.kabryxis.thevoid.api.impl.arena.schematic.VoidBaseArenaData;
import com.kabryxis.thevoid.api.util.arena.schematic.SchematicEntry;
import com.sk89q.worldedit.EditSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VoidArena implements Arena {
	
	public final static String PATH = "plugins" + File.separator + "TheVoid" + File.separator + "arenas" + File.separator;
	
	private final Map<Schematic, ArenaData> arenaDatas = new HashMap<>(); // TODO consider splitting normal vs base versions into seperate maps
	private final Queue<BaseArenaData> schematics = new ConcurrentLinkedQueue<>();
	
	private final ArenaDataObjectRegistry dataObjectRegistry;
	private final ChunkLoader chunkLoader;
	private final Config data;
	private final Location location;
	private final EditSession editSession;
	private final int weight;
	
	private BaseArenaData currentArenaData;
	private Set<ArenaData> otherCurrentSchematics;
	
	private Set<Entity> spawnedEntities = new HashSet<>();
	
	public VoidArena(ArenaDataObjectRegistry dataObjectRegistry, ChunkLoader chunkLoader, Config data) {
		this.dataObjectRegistry = dataObjectRegistry;
		this.chunkLoader = chunkLoader;
		this.data = data;
		this.location = new Location(Bukkit.getWorld(data.getString("world")), data.getDouble("x"), data.getDouble("y"), data.getDouble("z"));
		this.editSession = new EditSessionBuilder(FaweAPI.getWorld(getWorld().getName())).fastmode(true).build();
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
	public World getWorld() {
		return location.getWorld();
	}
	
	@Override
	public ArenaDataObjectRegistry getRegistry() {
		return dataObjectRegistry;
	}
	
	public ChunkLoader getChunkLoader() {
		return chunkLoader;
	}
	
	public EditSession getEditSession() {
		return editSession;
	}
	
	@Override
	public boolean test(Object obj) { // (trippy concept, reminder - the randomlist called on is the object type that passes through what is in the arguments)
		if(obj instanceof BaseSchematic) {
			// TODO
		}
		return false;
	}
	
	@Override
	public void queueSchematics(List<? extends BaseSchematic> list) {
		for(BaseSchematic baseSchematic : list) {
			schematics.add(getArenaData(baseSchematic));
		}
	}
	
	private BaseArenaData getArenaData(BaseSchematic schematic) {
		return (BaseArenaData)arenaDatas.computeIfAbsent(schematic, s -> new VoidBaseArenaData(this, schematic));
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
		chunkLoader.releaseFromMemory(this);
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
