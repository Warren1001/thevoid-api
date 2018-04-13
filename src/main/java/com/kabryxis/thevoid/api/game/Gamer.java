package com.kabryxis.thevoid.api.game;

import com.kabryxis.kabutils.spigot.concurrent.BukkitThreads;
import com.kabryxis.kabutils.spigot.version.WrappableCache;
import com.kabryxis.kabutils.spigot.version.wrapper.entity.player.WrappedEntityPlayer;
import com.kabryxis.kabutils.spigot.version.wrapper.packet.WrappedPacket;
import com.kabryxis.kabutils.spigot.version.wrapper.packet.out.chat.WrappedPacketPlayOutChat;
import com.kabryxis.thevoid.api.schematic.BlockSelection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Gamer {
	
	private final static Map<UUID, Gamer> gamers = new HashMap<>();
	
	public static Gamer getGamer(Player player) {
		return getGamer(player.getUniqueId());
	}
	
	public static Gamer getGamer(UUID uuid) {
		return gamers.computeIfAbsent(uuid, Gamer::new);
	}
	
	private final static ItemStack[] EMPTY_ITEMS = new ItemStack[36], EMPTY_ARMOR = new ItemStack[4];
	
	private final UUID uuid;
	private final WrappedEntityPlayer<?> entityPlayer;
	
	private Player player;
	
	private int roundPoints = 0;
	private Score gamePointsAlive;
	private Score gamePointsDead;
	private long gamePointsTimeAchieved;
	
	private Game game;
	
	private boolean inBuilderMode = false;
	private BlockSelection selection = null;
	
	public Gamer(UUID uuid) {
		this.uuid = uuid;
		this.entityPlayer = WrappableCache.get(WrappedEntityPlayer.class);
		updatePlayer();
		reset();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public WrappedEntityPlayer<?> getEntityPlayer() {
		return entityPlayer;
	}
	
	public void updatePlayer() {
		this.player = Bukkit.getPlayer(uuid);
		entityPlayer.set(player);
	}
	
	public String getName() {
		return player.getDisplayName();
	}
	
	public boolean isOnline() {
		return Bukkit.getOnlinePlayers().contains(player);
	}
	
	public void message(String message) {
		player.sendMessage(message);
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return game;
	}
	
	public boolean isInGame() {
		return game != null;
	}
	
	public void setBuilderMode(boolean inBuilderMode) {
		this.inBuilderMode = inBuilderMode;
	}
	
	public boolean isInBuilderMode() {
		return inBuilderMode;
	}
	
	public void setLeftSelection(Location loc) {
		if(selection == null) selection = new BlockSelection(player);
		selection.setLeft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public void setRightSelection(Location loc) {
		if(selection == null) selection = new BlockSelection(player);
		selection.setRight(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public BlockSelection getSelection() {
		return selection;
	}
	
	public void reset() {
		if(!isAlive()) stopSpectating();
		Objective obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("thevoid-points");
		gamePointsAlive = obj.getScore(ChatColor.GREEN + player.getDisplayName());
		gamePointsDead = obj.getScore(ChatColor.DARK_GRAY + player.getDisplayName());
	}
	
	public PlayerInventory getInventory() {
		return player.getInventory();
	}
	
	public void setInventory(ItemStack[] items, ItemStack[] armor) {
		PlayerInventory playerInventory = getInventory();
		playerInventory.setContents(items);
		playerInventory.setArmorContents(armor);
	}
	
	public void clearInventory() {
		setInventory(EMPTY_ITEMS, EMPTY_ARMOR);
	}
	
	public Location getWorldLocation() {
		return player.getLocation();
	}
	
	/*public Vector getArenaLocation() {
		return game.getCurrentRoundInfo().getArena().getArenaLocation(getWorldLocation());
	}*/
	
	public void forEachBlockStanding(Predicate<Block> predicate, Consumer<Block> action) {
		Location loc = player.getLocation().subtract(0, 0.1, 0);
		Block block = loc.getBlock();
		double offsetX = loc.getX() - loc.getBlockX(), offsetZ = loc.getZ() - loc.getBlockZ();
		boolean two = false, lessX = offsetX <= 0.3, lessZ = offsetZ <= 0.3;
		if(predicate.test(block)) action.accept(block);
		if(lessX || offsetX >= 0.7) { // posx = east, posz = south
			Block b = block.getRelative(lessX ? BlockFace.WEST : BlockFace.EAST);
			if(predicate.test(b)) action.accept(b);
			two = true;
		}
		if(lessZ || offsetZ >= 0.7) {
			block = block.getRelative(lessZ ? BlockFace.NORTH : BlockFace.SOUTH);
			if(predicate.test(block)) action.accept(block);
			if(two && predicate.test(block)) action.accept(block.getRelative(lessX ? BlockFace.WEST : BlockFace.EAST));
		}
	}
	
	public void setScoreboard(Scoreboard board) {
		player.setScoreboard(board);
	}
	
	public void sendActionMessage(String message) {
		WrappedPacketPlayOutChat<?> packet = WrappableCache.get(WrappedPacketPlayOutChat.class);
		packet.newInstance(message);
		entityPlayer.sendPacket(packet);
		packet.cache();
	}
	
	public void sendPacket(WrappedPacket<?> packet) {
		entityPlayer.sendPacket(packet);
	}
	
	public void teleport(double y) {
		Location newLocation = player.getLocation().clone();
		newLocation.setY(y);
		teleport(newLocation);
	}
	
	public void teleport(Location location) {
		if(/*!player.getLocation().getWorld().getName().equals(location.getWorld().getName()) && */!BukkitThreads.isMainThread()) {
			BukkitThreads.sync(() -> teleport0(location));
		}
		else teleport0(location);
	}
	
	private void teleport0(Location location) {
		player.teleport(location);
	}
	
	public void setLevel(int level) {
		player.setLevel(level);
	}
	
	public void clearEffects() {
		player.getActivePotionEffects().iterator().forEachRemaining(pe -> player.removePotionEffect(pe.getType()));
	}
	
	public void playSound(Sound sound, float volume, float pitch) {
		player.playSound(player.getLocation(), sound, volume, pitch);
	}
	
	public boolean isAlive() {
		return player.getGameMode() == GameMode.SURVIVAL;
	}
	
	public void startSpectating() {
		if(!isAlive()) return;
		if(!BukkitThreads.isMainThread()) BukkitThreads.sync(this::startSpectating0);
		else startSpectating0();
	}
	
	public void startSpectating(Location teleport) {
		if(!isAlive()) return;
		if(!BukkitThreads.isMainThread()) BukkitThreads.sync(() -> startSpectating0(teleport));
		else startSpectating0(teleport);
	}
	
	private void startSpectating0() {
		game.died(this);
		player.setGameMode(GameMode.SPECTATOR);
		player.setFlying(true);
	}
	
	private void startSpectating0(Location teleport) {
		startSpectating0();
		teleport0(teleport);
	}
	
	public void stopSpectating() {
		if(!BukkitThreads.isMainThread()) BukkitThreads.sync(this::stopSpectating0);
		else stopSpectating0();
	}
	
	public void stopSpectating(Location teleport) {
		if(!BukkitThreads.isMainThread()) BukkitThreads.sync(() -> stopSpectating0(teleport));
		else stopSpectating0(teleport);
	}
	
	private void stopSpectating0() {
		player.setGameMode(GameMode.SURVIVAL);
		player.setFlying(false);
	}
	
	private void stopSpectating0(Location teleport) {
		stopSpectating0();
		teleport0(teleport);
	}
	
	public void kill() {
		startSpectating();
		kill0();
	}
	
	public void kill(Location teleport) {
		startSpectating(teleport);
		kill0();
	}
	
	private void kill0() {
		int points = gamePointsAlive.getScore();
		player.getScoreboard().resetScores(gamePointsAlive.getEntry());
		gamePointsDead.setScore(points);
	}
	
	public void revive() {
		stopSpectating();
		revive0();
	}
	
	public void revive(Location teleport) {
		stopSpectating(teleport);
		revive0();
	}
	
	private void revive0() {
		int points = gamePointsDead.getScore();
		player.getScoreboard().resetScores(gamePointsDead.getEntry());
		gamePointsAlive.setScore(points);
	}
	
	public int incrementRoundPoints(boolean setLevel) {
		return setRoundPoints(getRoundPoints() + 1, setLevel);
	}
	
	public int incrementRoundPoints() {
		return incrementRoundPoints(true);
	}
	
	public int setRoundPoints(int roundPoints, boolean setLevel) {
		if(this.roundPoints == roundPoints) return roundPoints;
		this.roundPoints = Math.max(0, roundPoints);
		if(setLevel) player.setLevel(this.roundPoints);
		return roundPoints;
	}
	
	public int setRoundPoints(int roundPoints) {
		return setRoundPoints(roundPoints, true);
	}
	
	public int decrementRoundPoints(boolean setLevel) {
		return setRoundPoints(getRoundPoints() - 1, setLevel);
	}
	
	public int decrementRoundPoints() {
		return decrementRoundPoints(true);
	}
	
	public int getRoundPoints() {
		return roundPoints;
	}
	
	public int incrementGamePoints() {
		return setGamePoints(getGamePoints() + 1);
	}
	
	public int setGamePoints(int gamePoints) {
		gamePointsTimeAchieved = System.currentTimeMillis();
		if(!isAlive()) {
			gamePointsDead.setScore(Math.max(0, gamePoints));
			return gamePointsDead.getScore();
		}
		else {
			gamePointsAlive.setScore(Math.max(0, gamePoints));
			return gamePointsAlive.getScore();
		}
		
	}
	
	public int decrementGamePoints() {
		return setGamePoints(getGamePoints() - 1);
	}
	
	public int getGamePoints() {
		return player.getGameMode() == GameMode.SPECTATOR ? gamePointsDead.getScore() : gamePointsAlive.getScore();
	}
	
	public long getGamePointsTimeAchieved() {
		return gamePointsTimeAchieved;
	}
	
}
