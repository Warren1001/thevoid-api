package com.kabryxis.thevoid.api.round;

import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.kabutils.spigot.world.Teleport;
import com.kabryxis.kabutils.time.TimeLeft;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.schematic.BaseArenaData;
import com.kabryxis.thevoid.api.arena.schematic.Schematic;
import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.game.GamePlayer;
import com.kabryxis.thevoid.api.util.game.DeathReason;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.Event;

import java.util.Collections;
import java.util.Map;

public abstract class BasicRound implements Round {
	
	private final RoundManager<? super BasicRound> roundManager;
	private final String name;
	private final Config data;
	private final boolean groundInteraction;
	
	public BasicRound(RoundManager<? super BasicRound> roundManager, String name, boolean groundInteraction) {
		this.roundManager = roundManager;
		this.name = name;
		this.data = roundManager.getData(this);
		this.groundInteraction = groundInteraction;
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
	public void load(Game game, RoundInfo info) {}
	
	@Override
	public void start(Game game) {}
	
	@Override
	public void tick(Game game, int time, TimeLeft timeLeft) {}
	
	@Override
	public void end(Game game) {}
	
	@Override
	public void unload(Game game) {}
	
	@Override
	public void event(Game game, Event event) {}
	
	@Override
	public void kill(GamePlayer gamePlayer, DeathReason reason) {
		gamePlayer.kill();
		gamePlayer.teleportUp(7.5);
	}
	
	@Override
	public void setup(GamePlayer gamePlayer) {
		gamePlayer.setGameMode(groundInteraction ? GameMode.SURVIVAL : GameMode.ADVENTURE);
		gamePlayer.setFlying(false);
	}
	
	@Override
	public Location[] getSpawns(Game game, int amount) {
		BaseArenaData arenaData = game.getCurrentRoundInfo().getArena().getCurrentArenaData();
		return Teleport.getEquidistantPoints(arenaData.getCenter(), amount, arenaData.getRadius());
	}
	
	@Override
	public void customTimer(Game game) {}
	
	@Override
	public boolean test(Object obj) {
		if(obj instanceof BasicRound) return ((BasicRound)obj).isEnabled();
		if(obj instanceof Arena) return data.getStringList("worlds").contains(((Arena)obj).getWorld().getName()); // TODO consider replacing with arena names instead, or possibly find a way to support both
		if(obj instanceof Schematic) return data.getStringList("schematics").contains(((Schematic)obj).getName());
		return false;
	}
	
	public void setCustomDefaults(Config data) {}
	
	public Map<String, Class<?>> getRequiredObjects() {
		return Collections.emptyMap();
	}
	
}
