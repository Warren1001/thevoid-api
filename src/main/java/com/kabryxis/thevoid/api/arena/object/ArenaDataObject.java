package com.kabryxis.thevoid.api.arena.object;

import com.kabryxis.thevoid.api.util.arena.ArenaEntry;
import com.kabryxis.thevoid.api.util.arena.schematic.SchematicEntry;

public interface ArenaDataObject {
	
	void next(SchematicEntry schematicEntry, ArenaEntry arenaEntry);
	
}
