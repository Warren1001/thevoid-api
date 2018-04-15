package com.kabryxis.thevoid.api.round;

import com.kabryxis.kabutils.random.Weighted;
import com.kabryxis.kabutils.time.TimeLeft;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.game.Gamer;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Round extends Weighted {
	
	default void load(Game game, Arena arena) {}
	
	default void start(Game game, Arena arena) {}
	
	default void tick(Game game, Arena arena, int time, TimeLeft timeLeft) {}
	
	default void end(Game game, Arena arena) {}
	
	default void event(Game game, Event event) {}
	
	default void fell(Game game, Gamer gamer) {
		gamer.decrementRoundPoints(false);
		gamer.kill();
		gamer.teleport(20);
	}
	
	default void customTimer() {}
	
	ItemStack[] getInventory();
	
	ItemStack[] getArmor();
	
	int getStartingPoints();
	
	String getName();
	
	int getRoundLength();
	
	List<String> getWorldNames();
	
	List<String> getSchematics();
	
}
