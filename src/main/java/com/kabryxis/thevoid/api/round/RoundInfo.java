package com.kabryxis.thevoid.api.round;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.schematic.BaseSchematic;
import com.kabryxis.thevoid.api.game.Game;

public interface RoundInfo {
	
	Round getRound();
	
	Arena getArena();
	
	BaseSchematic getSchematic();
	
	void load(Game game);
	
}
