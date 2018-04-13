package com.kabryxis.thevoid.api.arena;

import com.boydti.fawe.FaweCache;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;

public class ArenaEntry {
	
	public static final BaseBlock AIR = FaweCache.getBlock(0, 0);
	
	private final Vector pos;
	private final BaseBlock block;
	
	public ArenaEntry(int x, int y, int z, int type, int data) {
		this.pos = new Vector(x, y, z);
		this.block = FaweCache.getBlock(type, data);
	}
	
	public void set(EditSession editSession) {
		try {
			editSession.setBlock(pos, block);
		} catch(MaxChangedBlocksException e) {
			e.printStackTrace();
		}
	}
	
	public void erase(EditSession editSession) {
		try {
			editSession.setBlock(pos, AIR);
		} catch(MaxChangedBlocksException e) {
			e.printStackTrace();
		}
	}
	
	public Vector getPos() {
		return pos;
	}
	
	public BaseBlock getBlock() {
		return block;
	}
	
}
