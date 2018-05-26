package com.kabryxis.thevoid.api.round;

import com.kabryxis.kabutils.random.PredicateWeighted;
import com.kabryxis.kabutils.time.TimeLeft;
import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.game.Gamer;
import org.bukkit.event.Event;

import java.util.Collection;

public interface Round extends PredicateWeighted<Object> {
	
	String getName();
	
	int getRoundLength();
	
	void load(Game game);
	
	void start(Game game);
	
	void tick(Game game, int time, TimeLeft timeLeft);
	
	void end(Game game);
	
	void event(Game game, Event event);
	
	void kill(Gamer gamer, DeathReason reason);
	
	Collection<Gamer> getRoundWinners(Game game);
	
	void customTimer();
	
}
