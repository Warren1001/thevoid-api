package com.kabryxis.thevoid.api.game;

import com.kabryxis.kabutils.spigot.version.WrappableCache;
import com.kabryxis.kabutils.spigot.version.wrapper.entity.player.WrappedEntityPlayer;
import com.kabryxis.kabutils.spigot.version.wrapper.packet.WrappedPacket;
import com.kabryxis.kabutils.spigot.version.wrapper.packet.out.chat.WrappedPacketPlayOutChat;
import com.kabryxis.thevoid.api.round.Round;
import com.kabryxis.thevoid.api.arena.schematic.util.BlockSelection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

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
	
	private boolean alive = true;
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
		player.setGameMode(GameMode.ADVENTURE);
		player.setFlying(false);
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
	
	public Set<Block> getStandingBlocks() {
		Set<Block> blocks = new HashSet<>(4);
		Location loc = player.getLocation().subtract(0, 0.1, 0);
		Block block = loc.getBlock();
		double offsetX = loc.getX() - loc.getBlockX(), offsetZ = loc.getZ() - loc.getBlockZ();
		boolean two = false, lessX = offsetX <= 0.3, lessZ = offsetZ <= 0.3;
		blocks.add(block);
		if(lessX || offsetX >= 0.7) { // posx = east, posz = south
			Block b = block.getRelative(lessX ? BlockFace.WEST : BlockFace.EAST);
			blocks.add(b);
			two = true;
		}
		if(lessZ || offsetZ >= 0.7) {
			block = block.getRelative(lessZ ? BlockFace.NORTH : BlockFace.SOUTH);
			blocks.add(block);
			if(two) blocks.add(block.getRelative(lessX ? BlockFace.WEST : BlockFace.EAST));
		}
		return blocks;
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
		return alive;
	}
	
	public void setAlive(boolean alive) {
		if(this.alive && !alive) {
			int points = gamePointsAlive.getScore();
			player.getScoreboard().resetScores(gamePointsAlive.getEntry());
			gamePointsDead.setScore(points);
		}
		else if(!this.alive && alive) {
			int points = gamePointsDead.getScore();
			player.getScoreboard().resetScores(gamePointsDead.getEntry());
			gamePointsAlive.setScore(points);
			game.revive(this);
		}
		this.alive = alive;
	}
	
	public void setGameMode(GameMode gameMode) {
		player.setGameMode(gameMode);
	}
	
	public void nextRound(Round round, Location spawn) {
		setRoundPoints(0, true);
		setGameMode(round.getGameMode());
		player.setFlying(false);
		setInventory(round.getInventory(), round.getArmor());
		setRoundPoints(round.getStartingPoints(), false);
		teleport(spawn);
	}
	
	public boolean kill() {
		setAlive(false);
		boolean ending = game.kill(this);
		setGameMode(GameMode.SPECTATOR);
		player.setFlying(true);
		return ending;
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
		return alive ? gamePointsAlive.getScore() : gamePointsDead.getScore();
	}
	
	public long getGamePointsTimeAchieved() {
		return gamePointsTimeAchieved;
	}
	
}
