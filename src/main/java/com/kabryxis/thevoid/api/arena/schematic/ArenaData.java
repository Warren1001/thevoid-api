package com.kabryxis.thevoid.api.arena.schematic;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.util.arena.ArenaEntry;
import com.kabryxis.thevoid.api.util.arena.schematic.SchematicWork;

import java.util.Set;

public interface ArenaData {
	
	Schematic getSchematic();
	
	Arena getArena();
	
	void setArenaEntries(Set<ArenaEntry> arenaEntries);
	
	void loadSchematic();
	
	void eraseSchematic();
	
	<T extends SchematicWork> T getSchematicWork(Class<T> clazz);
	
}
