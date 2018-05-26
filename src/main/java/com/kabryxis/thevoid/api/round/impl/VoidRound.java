package com.kabryxis.thevoid.api.round.impl;

import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.kabutils.time.TimeLeft;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.schematic.Schematic;
import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.game.Gamer;
import com.kabryxis.thevoid.api.round.DeathReason;
import com.kabryxis.thevoid.api.round.Round;
import com.kabryxis.thevoid.api.round.RoundManager;
import org.bukkit.event.Event;

import java.util.*;

public class VoidRound implements Round {
	
	private final RoundManager roundManager;
	private final String name;
	private final Config data;
	
	public VoidRound(RoundManager roundManager, String name) {
		this.roundManager = roundManager;
		this.name = name;
		this.data = roundManager.getData(this);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public Config getData() {
		return data;
	}
	
	public void setEnabled(boolean enabled) {
		data.set("enabled", enabled);
	}
	
	public boolean isEnabled() {
		return data.getBoolean("enabled");
	}
	
	@Override
	public int getRoundLength() {
		return data.getInt("round-length");
	}
	
	@Override
	public int getWeight() {
		return data.getInt("weight");
	}
	
	@Override
	public boolean test(Object obj) {
		if(obj instanceof VoidRound) return ((VoidRound)obj).isEnabled();
		if(obj instanceof Arena) return data.getStringList("worlds").contains(((Arena)obj).getWorld().getName()); // TODO consider replacing with arena names instead, or possibly find a way to support both
		if(obj instanceof Schematic) return data.getStringList("schematics").contains(((Schematic)obj).getName());
		return false;
	}
	
	@Override
	public void load(Game game) {}
	
	@Override
	public void start(Game game) {}
	
	@Override
	public void tick(Game game, int time, TimeLeft timeLeft) {}
	
	@Override
	public void end(Game game) {}
	
	@Override
	public void event(Game game, Event event) {}
	
	@Override
	public void kill(Gamer gamer, DeathReason reason) { // TODO most likely needs to be modified
		gamer.decrementRoundPoints(false);
		gamer.kill();
		gamer.teleport(20);
	}
	
	@Override
	public Collection<Gamer> getRoundWinners(Game game) { // TODO probably needs to be modified
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
	
	@Override
	public void customTimer() {}
	
	public void setCustomDefaults(Config data) {}
	
	public Map<String, Class<?>> getRequiredObjects() {
		return Collections.emptyMap();
	}
	
	/*@Override
	public Location[] getSpawns(Game game) {
		BaseArenaData baseArenaData = game.getCurrentRoundInfo().getArena().getCurrentArenaData();
		return Teleport.getEquidistantPoints(baseArenaData.getCenter(), game.getGamers().size(), baseArenaData.getRadius());
	}*/
	
}
