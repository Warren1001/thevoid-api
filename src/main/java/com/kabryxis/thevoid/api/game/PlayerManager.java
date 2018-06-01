package com.kabryxis.thevoid.api.game;

import com.kabryxis.thevoid.api.util.game.Board;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface PlayerManager {
	
	Game getGame();
	
	Board getBoard();
	
	GamePlayer getPlayer(Player player);
	
	GamePlayer getPlayer(UUID uuid);
	
	void kill(GamePlayer player);
	
	List<? extends GamePlayer> getActivePlayers();
	
	List<? extends GamePlayer> getAlivePlayers();
	
	void forEachAlivePlayer(Consumer<? super GamePlayer> action);
	
	void forEachActivePlayer(Consumer<? super GamePlayer> action);
	
	int getAlive();
	
	void resetAlivePlayers();
	
}
