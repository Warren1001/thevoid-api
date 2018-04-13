package com.kabryxis.thevoid.api.round;

import com.kabryxis.kabutils.random.Weighted;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.game.Gamer;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Round extends Weighted {
	
	void load(Game game, Arena arena);
	
	void start(Game game, Arena arena);
	
	void tick(Game game, Arena arena, int timeLeft); // TODO implement enum that dictates intervals in time left (fourth, half, third, ect), also reminder for dynamic time based on map size
	
	void end(Game game, Arena arena);
	
	void event(Game game, Event event);
	
	void fell(Game game, Gamer gamer);
	
	ItemStack[] getInventory();
	
	ItemStack[] getArmor();
	
	int getStartingPoints();
	
	String getName();
	
	int getRoundLength();
	
	List<String> getWorldNames();
	
	List<String> getSchematics();
	
	void customTimer();
	
}
