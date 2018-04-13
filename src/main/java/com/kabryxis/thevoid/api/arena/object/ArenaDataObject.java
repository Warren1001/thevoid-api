package com.kabryxis.thevoid.api.arena.object;

import com.kabryxis.thevoid.api.arena.ArenaEntry;
import com.kabryxis.thevoid.api.schematic.SchematicEntry;

public interface ArenaDataObject {
	
	void next(SchematicEntry schematicEntry, ArenaEntry arenaEntry);
	
}
