package com.kabryxis.thevoid.api.round;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.schematic.IBaseSchematic;
import com.kabryxis.thevoid.api.game.Game;

public interface RoundInfo {
	
	Round getRound();
	
	Arena getArena();
	
	IBaseSchematic getSchematic();
	
	void load(Game game);
	
}
