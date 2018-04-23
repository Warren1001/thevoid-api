package com.kabryxis.thevoid.api.round;

import com.kabryxis.kabutils.random.Weighted;
import com.kabryxis.kabutils.spigot.world.Teleport;
import com.kabryxis.kabutils.time.TimeLeft;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.game.Gamer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Round extends Weighted {
	
	default void load(Game game, Arena arena) {}
	
	default void start(Game game, Arena arena) {}
	
	default void tick(Game game, Arena arena, int time, TimeLeft timeLeft) {}
	
	default void customTimer() {}
	
	default void end(Game game, Arena arena) {}
	
	default void event(Game game, Event event) {}
	
	default void fell(Gamer gamer) {
		kill(gamer);
	}
	
	default void kill(Gamer gamer) {
		gamer.decrementRoundPoints(false);
		gamer.kill();
		gamer.teleport(20);
	}
	
	default Location[] getSpawns(Game game, int radius) {
		return Teleport.getEquidistantPoints(game.getCurrentRoundInfo().getArena().getLocation().clone().add(0, 0.75, 0), game.getGamers().size(), radius);
	}
	
	default Collection<Gamer> getRoundWinners(Game game) {
		Collection<Gamer> gamers = game.getGamers();
		Set<Gamer> winners = new HashSet<>();
		int mostPoints = 0;
		for(Gamer gamer : gamers) {
			int points = gamer.getRoundPoints();
			if(points > mostPoints) mostPoints = points;
		}
		if(mostPoints > 0) {
			for(Gamer gamer : gamers) {
				if(gamer.getRoundPoints() == mostPoints) winners.add(gamer);
			}
		}
		return winners;
	}
	
	ItemStack[] getInventory();
	
	ItemStack[] getArmor();
	
	int getStartingPoints();
	
	GameMode getGameMode();
	
	String getName();
	
	int getRoundLength();
	
	List<String> getWorldNames();
	
	List<String> getSchematics();
	
}
