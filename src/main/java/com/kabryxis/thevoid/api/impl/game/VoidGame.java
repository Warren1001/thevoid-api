package com.kabryxis.thevoid.api.impl.game;

import com.kabryxis.kabutils.concurrent.Threads;
import com.kabryxis.kabutils.data.DataQueue;
import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.kabutils.spigot.version.wrapper.packet.out.chat.WrappedPacketPlayOutChat;
import com.kabryxis.kabutils.spigot.world.ChunkLoader;
import com.kabryxis.kabutils.time.CountdownManager;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.object.ArenaDataObjectRegistry;
import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.game.GamePlayer;
import com.kabryxis.thevoid.api.round.Round;
import com.kabryxis.thevoid.api.round.RoundInfo;
import com.kabryxis.thevoid.api.round.RoundInfoRegistry;
import com.kabryxis.thevoid.api.util.game.GameEventHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VoidGame implements Game {
	
	private final String name = "thevoid-game";
	private final DataQueue<RoundInfo> infos = new DataQueue<>();
	private final CountdownManager cm = new CountdownManager();
	private final List<GameEventHandler> eventHandlers = new ArrayList<>();
	
	private final Plugin plugin;
	private final VoidPlayerManager playerManager;
	private final ChunkLoader chunkLoader;
	private final RoundInfoRegistry infoRegistry;
	private final ArenaDataObjectRegistry objectRegistry;
	
	private Location spawn;
	
	public VoidGame(Plugin plugin, ChunkLoader chunkLoader, RoundInfoRegistry infoRegistry, ArenaDataObjectRegistry objectRegistry) {
		this.plugin = plugin;
		this.playerManager = new VoidPlayerManager(this);
		this.chunkLoader = chunkLoader;
		this.infoRegistry = infoRegistry;
		this.objectRegistry = objectRegistry;
		cm.constructNewCountdown("preGame", 1000, false, (time, timeLeft) -> broadcastMessage("Starting in " + time + " seconds."));
		cm.constructNewCountdown("gameTimer", 1000, true, (time, timeLeft) -> {
			RoundInfo roundInfo = getCurrentRoundInfo();
			roundInfo.getRound().tick(this, time, timeLeft);
			WrappedPacketPlayOutChat packetPlayOutChat = WrappedPacketPlayOutChat.newInstance(ChatColor.GOLD.toString() + time);
			playerManager.forEachActivePlayer(voidPlayer -> {
				voidPlayer.sendPacket(packetPlayOutChat);
				if(time <= 5) voidPlayer.playSound(Sound.NOTE_PLING, 0.7F, 1F);
			});
		}, () -> {
			WrappedPacketPlayOutChat packetPlayOutChat = WrappedPacketPlayOutChat.newInstance("");
			playerManager.forEachActivePlayer(voidPlayer -> {
				voidPlayer.sendPacket(packetPlayOutChat);
				voidPlayer.playSound(Sound.NOTE_PLING, 0.7F, 0.5F);
			});
		});
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Plugin getOwner() {
		return plugin;
	}
	
	@Override
	public VoidPlayerManager getPlayerManager() {
		return playerManager;
	}
	
	@Override
	public void threadStart() {
		infos.getNext().load(this);
		cm.count("preGame", 5);
		playerManager.getBoard().start();
	}
	
	@Override
	public boolean canRun() {
		return infos.hasNext();
	}
	
	@Override
	public void next() {
		infos.nextIndex();
		playerManager.resetAlivePlayers();
	}
	
	@Override
	public void start() {
		RoundInfo info = getCurrentRoundInfo();
		Round round = info.getRound();
		round.start(this);
		Location[] spawns = round.getSpawns(this, playerManager.getAlive());
		BukkitThreads.sync(() -> {
			List<VoidPlayer> voidPlayers = playerManager.getAlivePlayers();
			for(int i = 0; i < spawns.length; i++) {
				VoidPlayer voidPlayer = voidPlayers.get(i);
				voidPlayer.teleport(spawns[i]);
				round.setup(voidPlayer);
			}
		});
		if(infos.hasNext()) infos.getNext().load(this);
	}
	
	@Override
	public void timer() {
		RoundInfo info = getCurrentRoundInfo();
		Round round = info.getRound();
		int roundLength = round.getRoundLength();
		if(roundLength == -1) round.customTimer(this);
		else cm.count("gameTimer", (int)Math.ceil((double)roundLength * info.getSchematic().getTimeModifier()));
	}
	
	@Override
	public void end() {
		RoundInfo info = getCurrentRoundInfo();
		Round round = info.getRound();
		Arena arena = info.getArena();
		round.end(this);
		arena.endOfRound();
		round.getRoundWinners(this).forEach(GamePlayer::incrementPoints);
		playerManager.forEachActivePlayer(voidPlayer -> {
			voidPlayer.clearEffects();
			voidPlayer.clearInventory();
		});
		Threads.sleep(3500);
		playerManager.forEachActivePlayer(GamePlayer::revive);
		playerManager.getBoard().nextRound();
		info.getArena().eraseSchematic();
		round.unload(this);
	}
	
	@Override
	public void threadEnd() {
		infos.resetIndex();
		infos.clear();
		broadcastMessage(getGameWinner().getName() + " wins!");
		playerManager.getBoard().reset(); // Must be after game winner announcer method.
		BukkitThreads.sync(() -> playerManager.forEachActivePlayer(voidPlayer -> {
			voidPlayer.reset();
			voidPlayer.teleport(spawn);
		}));
	}
	
	@Override
	public void pause() {
		cm.pauseAll();
	}
	
	@Override
	public void unpause() {
		cm.unpauseAll();
	}
	
	@Override
	public RoundInfo getCurrentRoundInfo() {
		return infos.getCurrent();
	}
	
	@Override
	public void callEvent(Event event) {
		eventHandlers.forEach(handler -> handler.onEvent(event));
		if(isInProgress()) getCurrentRoundInfo().getRound().event(this, event);
	}
	
	@Override
	public boolean isInProgress() {
		return infos.getCurrentIndex() != -1;
	}
	
	@Override
	public boolean kill(GamePlayer gamePlayer) {
		playerManager.kill(gamePlayer);
		if(playerManager.getAlive() < 2) {
			cm.getCountdown("gameTimer").setCurrentTime(0);
			return true;
		}
		return false;
	}
	
	@Override
	public void revive(GamePlayer gamePlayer) {
		// TODO
	}
	
	public void registerEventHandler(GameEventHandler eventHandler) {
		eventHandlers.add(eventHandler);
	}
	
	public ArenaDataObjectRegistry getRegistry() {
		return objectRegistry;
	}
	
	public CountdownManager getCountdownManager() {
		return cm;
	}
	
	public void setSpawn(Location loc) {
		if(spawn != null) chunkLoader.releaseFromMemory(this);
		spawn = loc;
		chunkLoader.keepInMemory(this, spawn.getChunk());
	}
	
	public void addRounds(int amount) {
		infoRegistry.queueArenaData(infos, amount);
	}
	
	public void broadcastMessage(String message) {
		playerManager.forEachActivePlayer(voidPlayer -> voidPlayer.sendMessage(message));
	}
	
	private VoidPlayer getGameWinner() {
		int topPoints = 0;
		long topTimeAchieved = 0;
		VoidPlayer winner = null;
		for(VoidPlayer voidPlayer : playerManager.getActivePlayers()) {
			int points = voidPlayer.getPoints();
			long timeAchieved = voidPlayer.getPointsTimeAchieved();
			if(winner == null || points > topPoints || (points == topPoints && timeAchieved < topTimeAchieved)) {
				topPoints = points;
				topTimeAchieved = timeAchieved;
				winner = voidPlayer;
			}
		}
		return winner;
	}
	
	private List<VoidPlayer> getGameWinners() { // TODO sort by time achieved
		int topPoints = 0;
		for(VoidPlayer voidPlayer : playerManager.getActivePlayers()) {
			int points = voidPlayer.getPoints();
			if(points > topPoints) topPoints = points;
		}
		int finalTopPoints = topPoints;
		return playerManager.getActivePlayers().stream().filter(g -> g.getPoints() == finalTopPoints).collect(Collectors.toCollection(ArrayList::new));
	}
	
}

