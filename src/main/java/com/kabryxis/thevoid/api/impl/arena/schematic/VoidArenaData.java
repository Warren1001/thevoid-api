package com.kabryxis.thevoid.api.impl.arena.schematic;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.util.arena.ArenaEntry;
import com.kabryxis.thevoid.api.impl.arena.VoidArena;
import com.kabryxis.thevoid.api.arena.schematic.ArenaData;
import com.kabryxis.thevoid.api.arena.schematic.Schematic;
import com.kabryxis.thevoid.api.util.arena.schematic.SchematicWork;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class VoidArenaData implements ArenaData {
	
	private final VoidArena arena;
	private final Schematic schematic;
	
	private Set<ArenaEntry> arenaEntries;
	
	private Map<Class<? extends SchematicWork>, SchematicWork> schematicWorks;
	//private int lowestY;
	
	public VoidArenaData(VoidArena arena, Schematic schematic) {
		this.arena = arena;
		this.schematic = schematic;
		if(schematic.hasSchematicWork()) {
			Set<Supplier<? extends SchematicWork>> schematicWorkSet = schematic.getSchematicWork();
			schematicWorks = new HashMap<>(schematicWorkSet.size());
			schematicWorkSet.forEach(supplier -> {
				SchematicWork work = supplier.get();
				schematicWorks.put(work.getClass(), work);
			});
		}
	}
	
	@Override
	public Arena getArena() {
		return arena;
	}
	
	@Override
	public Schematic getSchematic() {
		return schematic;
	}
	
	@Override
	public void setArenaEntries(Set<ArenaEntry> arenaEntries) {
		this.arenaEntries = arenaEntries;
		if(schematicWorks != null) {
			BukkitThreads.sync(() -> {
				for(ArenaEntry entry : arenaEntries) {
					Vector pos = entry.getPos();
					int x = pos.getBlockX(), y = pos.getBlockY(), z = pos.getBlockZ();
					int type = entry.getBlock().getId();
					int data = entry.getBlock().getData();
					schematicWorks.values().forEach(extra -> extra.doExtra(arena.getWorld().getBlockAt(x, y, z), Material.getMaterial(type), data));
				}
			});
		}
		/*lowestY = Integer.MAX_VALUE;
		for(ArenaEntry entry : arenaEntries) {
			Vector pos = entry.getPos();
			int x = pos.getBlockX(), y = pos.getBlockY(), z = pos.getBlockZ();
			int type = entry.getBlock().getId();
			int data = entry.getBlock().getData();
			//if(y < lowestY) lowestY = y;
			if(schematicWorks != null) schematicWorks.values().forEach(extra -> extra.doExtra(arena.getWorld().getBlockAt(x, y, z), Material.getMaterial(type), data));
		}*/
	}
	
	@Override
	public void loadSchematic() {
		EditSession editSession = arena.getEditSession();
		arenaEntries.forEach(arenaEntry -> arenaEntry.set(editSession));
		editSession.flushQueue();
	}
	
	@Override
	public void eraseSchematic() {
		EditSession editSession = arena.getEditSession();
		arenaEntries.forEach(arenaEntry -> arenaEntry.erase(editSession));
		editSession.flushQueue();
	}
	
	@Override
	public <T extends SchematicWork> T getSchematicWork(Class<T> clazz) {
		SchematicWork work = schematicWorks.get(clazz);
		return work == null ? null : clazz.cast(work);
	}
	
}
