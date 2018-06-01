package com.kabryxis.thevoid.api.impl.game;

import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.game.GamePlayer;
import com.kabryxis.thevoid.api.game.PlayerManager;
import com.kabryxis.thevoid.api.util.game.Board;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public class VoidPlayerManager implements PlayerManager {
	
	private final Map<UUID, VoidPlayer> voidPlayers = new HashMap<>();
	private final List<VoidPlayer> activePlayers = new ArrayList<>(24);
	private final List<VoidPlayer> alivePlayers = new ArrayList<>(24);
	
	private final Game game;
	private final Board board;
	
	public VoidPlayerManager(Game game) {
		this.game = game;
		this.board = new Board(game);
	}
	
	@Override
	public Game getGame() {
		return game;
	}
	
	@Override
	public Board getBoard() {
		return board;
	}
	
	@Override
	public VoidPlayer getPlayer(Player player) {
		UUID uuid = player.getUniqueId();
		VoidPlayer voidPlayer;
		if(voidPlayers.containsKey(uuid)) {
			voidPlayer = voidPlayers.get(uuid);
			voidPlayer.updatePlayer(player);
		}
		else {
			voidPlayer = new VoidPlayer(game, board, player);
			voidPlayers.put(uuid, voidPlayer);
		}
		return voidPlayer;
	}
	
	@Override
	public VoidPlayer getPlayer(UUID uuid) {
		return voidPlayers.computeIfAbsent(uuid, u -> new VoidPlayer(game, board, Bukkit.getPlayer(u)));
	}
	
	@Override
	public void kill(GamePlayer gamePlayer) {
		synchronized(alivePlayers) {
			alivePlayers.remove(gamePlayer);
		}
	}
	
	@Override
	public List<VoidPlayer> getActivePlayers() {
		return activePlayers;
	}
	
	@Override
	public List<VoidPlayer> getAlivePlayers() {
		return alivePlayers;
	}
	
	@Override
	public void forEachAlivePlayer(Consumer<? super GamePlayer> action) {
		synchronized(alivePlayers) {
			alivePlayers.forEach(action);
		}
	}
	
	@Override
	public void forEachActivePlayer(Consumer<? super GamePlayer> action) {
		activePlayers.forEach(action);
	}
	
	@Override
	public int getAlive() {
		return alivePlayers.size();
	}
	
	@Override
	public void resetAlivePlayers() {
		alivePlayers.clear();
		alivePlayers.addAll(activePlayers); // TODO eventually differentiate between playing players
		Collections.shuffle(alivePlayers);
	}
	
}
