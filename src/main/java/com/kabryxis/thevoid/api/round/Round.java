package com.kabryxis.thevoid.api.round;

import com.kabryxis.kabutils.random.weighted.conditional.ConditionalWeighted;
import com.kabryxis.kabutils.time.TimeLeft;
import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.game.GamePlayer;
import com.kabryxis.thevoid.api.util.game.DeathReason;
import org.bukkit.Location;
import org.bukkit.event.Event;

import java.util.List;

public interface Round extends ConditionalWeighted<Object> {
	
	String getName();
	
	int getRoundLength();
	
	void load(Game game, RoundInfo info);
	
	void start(Game game);
	
	void tick(Game game, int time, TimeLeft timeLeft);
	
	void end(Game game);
	
	void unload(Game game);
	
	void event(Game game, Event event);
	
	void kill(GamePlayer gamePlayer, DeathReason reason);
	
	void setup(GamePlayer gamePlayer);
	
	Location[] getSpawns(Game game, int amount);
	
	List<? extends GamePlayer> getRoundWinners(Game game);
	
	void customTimer(Game game);
	
}
