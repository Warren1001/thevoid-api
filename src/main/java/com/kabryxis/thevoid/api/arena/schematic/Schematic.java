package com.kabryxis.thevoid.api.arena.schematic;

import com.kabryxis.thevoid.api.util.arena.schematic.SchematicEntry;
import com.kabryxis.thevoid.api.util.arena.schematic.SchematicWork;

import java.util.Set;
import java.util.function.Supplier;

public interface Schematic {
	
	String getName();
	
	Set<SchematicEntry> getSchematicEntries();
	
	double getSizeX();
	
	double getSizeZ();
	
	void addSchematicWork(Supplier<? extends SchematicWork> clazz);
	
	boolean hasSchematicWork();
	
	Set<Supplier<? extends SchematicWork>> getSchematicWork();
	
}
