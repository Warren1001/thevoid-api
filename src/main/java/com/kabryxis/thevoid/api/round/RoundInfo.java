package com.kabryxis.thevoid.api.round;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.schematic.Schematic;

public interface RoundInfo {
	
	Round getRound();
	
	Arena getArena();
	
	Schematic getSchematic();
	
	void load(Game game);
	
}
