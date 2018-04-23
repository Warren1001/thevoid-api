package com.kabryxis.thevoid.api.arena.schematic.impl;

import com.kabryxis.thevoid.api.arena.ArenaEntry;
import com.kabryxis.thevoid.api.arena.impl.LegacyArena;
import com.kabryxis.thevoid.api.arena.schematic.util.SchematicWork;
import com.sk89q.worldedit.Vector;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LegacySchematicData {
	
	private final LegacySchematic schematic;
	private final LegacyArena arena;
	
	private Set<ArenaEntry> arenaData;
	
	private Map<Class<? extends SchematicWork>, SchematicWork> extraWorks;
	private int lowestY;
	
	public LegacySchematicData(LegacySchematic schematic, LegacyArena arena) {
		this.schematic = schematic;
		this.arena = arena;
		if(schematic.hasSchematicWork()) {
			Set<Class<? extends SchematicWork>> schematicWorks = schematic.getSchematicWork();
			extraWorks = new HashMap<>(schematicWorks.size());
			schematicWorks.forEach(clazz -> {
				try {
					extraWorks.put(clazz, clazz.newInstance());
				}
				catch(InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	public LegacySchematic getSchematic() {
		return schematic;
	}
	
	public LegacyArena getArena() {
		return arena;
	}
	
	public void setCurrentArenaData(Set<ArenaEntry> arenaData) {
		this.arenaData = arenaData;
		lowestY = Integer.MAX_VALUE;
		for(ArenaEntry entry : arenaData) {
			Vector pos = entry.getPos();
			int x = pos.getBlockX(), y = pos.getBlockY(), z = pos.getBlockZ();
			int type = entry.getBlock().getId();
			int data = entry.getBlock().getData();
			if(y < lowestY) lowestY = y;
			if(extraWorks != null) extraWorks.values().forEach(extra -> extra.doExtra(arena.getWorld().getBlockAt(x, y, z), Material.getMaterial(type), data));
		}
	}
	
	public Set<ArenaEntry> getCurrentArenaData() {
		return arenaData;
	}
	
	public boolean hasExtraWork(Class<? extends SchematicWork> clazz) {
		return extraWorks != null && extraWorks.get(clazz) != null;
	}
	
	public <T extends SchematicWork> T getExtraWork(Class<T> clazz) {
		return extraWorks == null ? null : clazz.cast(extraWorks.get(clazz));
	}
	
	public void loadSchematic() {
		/*EditSession editSession = arena.getEditSession();
		arenaData.forEach(arenaEntry -> arenaEntry.set(editSession));
		editSession.flushQueue();*/
	}
	
	public void eraseSchematic() {
		/*EditSession editSession = arena.getEditSession();
		arenaData.forEach(arenaEntry -> arenaEntry.erase(editSession));
		editSession.flushQueue();*/
	}
	
	public int getLowestY() {
		return lowestY;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof LegacySchematicData)) return false;
		LegacySchematicData d = (LegacySchematicData)o;
		return d.getArena().getName().equals(arena.getName()) && d.getSchematic().getName().equals(getSchematic().getName());
	}
	
}
