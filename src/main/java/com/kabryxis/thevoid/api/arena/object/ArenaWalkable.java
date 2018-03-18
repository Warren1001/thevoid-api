package com.kabryxis.thevoid.api.arena.object;

import com.kabryxis.kabutils.data.Arrays;
import com.kabryxis.kabutils.string.Strings;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.schematic.BaseSchematic;
import com.kabryxis.thevoid.api.schematic.SchematicEntry;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ArenaWalkable implements ArenaDataObject {
	
	private final List<Location> walkableLocs = new ArrayList<>();
	private final BaseSchematic schematic;
	private final Arena arena;
	private final int[] walkableY;
	
	public ArenaWalkable(BaseSchematic schematic, Arena arena) {
		this.schematic = schematic;
		this.arena = arena;
		String[] walkableSplit = Strings.split(schematic.getData().getString("walkable"), ",");
		int size = walkableSplit.length;
		walkableY = new int[size];
		for(int i = 0; i < size; i++) {
			walkableY[i] = Integer.parseInt(walkableSplit[i]);
		}
	}
	
	@Override
	public void next(SchematicEntry entry, int trueX, int trueY, int trueZ) {
		if(Arrays.containsInt(walkableY, entry.getY())) walkableLocs.add(new Location(arena.getWorld().getWorld(), trueX, trueY, trueZ));
	}
	
	public List<Location> get() {
		return walkableLocs;
	}
	
}
