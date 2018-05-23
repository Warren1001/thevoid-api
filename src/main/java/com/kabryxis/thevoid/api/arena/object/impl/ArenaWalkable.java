package com.kabryxis.thevoid.api.arena.object.impl;

import com.kabryxis.kabutils.data.Arrays;
import com.kabryxis.kabutils.string.Strings;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.ArenaEntry;
import com.kabryxis.thevoid.api.arena.object.ArenaDataObject;
import com.kabryxis.thevoid.api.arena.schematic.BaseSchematic;
import com.kabryxis.thevoid.api.arena.schematic.util.SchematicEntry;
import com.sk89q.worldedit.Vector;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;

public class ArenaWalkable implements ArenaDataObject {
	
	private final List<Location> walkableLocs = new ArrayList<>();
	private final List<Set<Block>> diamondPatternBlocks = new ArrayList<>();
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
	public void next(SchematicEntry schematicEntry, ArenaEntry arenaEntry) {
		if(Arrays.containsInt(walkableY, schematicEntry.getY())) {
			Vector pos = arenaEntry.getPos();
			walkableLocs.add(new Location(arena.getWorld(), pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()));
		}
	}
	
	public void loadDiamondPatternBlocks(int centerX, int centerZ) {
		Map<Integer, Set<Block>> map = new HashMap<>();
		if(schematic.isOdd()) {
			for(Location loc : walkableLocs) {
				map.computeIfAbsent(Math.abs(loc.getBlockX() - centerX) + Math.abs(loc.getBlockZ() - centerZ), i -> new HashSet<>()).add(loc.getBlock());
			}
		}
		else {
			for(Location loc : walkableLocs) {
				int bx = loc.getBlockX() - centerX, bz = loc.getBlockZ() - centerZ;
				if(bx < 0) bx += 1;
				if(bz < 0) bz += 1;
				map.computeIfAbsent(Math.abs(bx) + Math.abs(bz), i -> new HashSet<>()).add(loc.getBlock());
			}
		}
		for(int i = 0; i <= getHighest(map.keySet()); i++) {
			Set<Block> blocks = map.get(i);
			if(blocks != null) diamondPatternBlocks.add(blocks);
		}
	}
	
	private int getHighest(Set<Integer> integers) {
		int highest = 0;
		for(Integer i : integers) {
			if(i > highest) highest = i;
		}
		return highest;
	}
	
	public List<Location> getWalkableLocations() {
		return walkableLocs;
	}
	
	public List<Set<Block>> getDiamondPatternBlocks() {
		return diamondPatternBlocks;
	}
	
}
