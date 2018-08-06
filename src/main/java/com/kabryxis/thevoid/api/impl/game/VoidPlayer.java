package com.kabryxis.thevoid.api.impl.game;

import com.kabryxis.kabutils.spigot.version.wrapper.entity.player.WrappedEntityPlayer;
import com.kabryxis.kabutils.spigot.version.wrapper.packet.WrappedPacket;
import com.kabryxis.kabutils.spigot.version.wrapper.packet.out.chat.WrappedPacketPlayOutChat;
import com.kabryxis.thevoid.api.game.GamePlayer;
import com.kabryxis.thevoid.api.util.arena.schematic.BlockSelection;
import com.kabryxis.thevoid.api.impl.arena.schematic.VoidSchematicCreator;
import com.kabryxis.thevoid.api.util.game.Board;
import com.kabryxis.thevoid.api.util.game.BoardEntry;
import com.kabryxis.thevoid.api.game.Game;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VoidPlayer implements GamePlayer {
	
	private final static ItemStack[] EMPTY_ITEMS = new ItemStack[36], EMPTY_ARMOR = new ItemStack[4];
	
	private final Game game;
	private final UUID uuid;
	private final BoardEntry boardEntry;
	
	private Player player;
	private WrappedEntityPlayer entityPlayer;
	
	private boolean alive = true;
	private int points;
	private long pointsTimeAchieved;
	
	private boolean inBuilderMode = false;
	private BlockSelection selection = new BlockSelection(player);
	private VoidSchematicCreator creator = new VoidSchematicCreator(this);
	
	public VoidPlayer(Game game, Board board, Player player) {
		this.game = game;
		this.uuid = player.getUniqueId();
		this.boardEntry = board.getBoardEntry(this);
		updatePlayer(player);
		reset();
	}
	
	@Override
	public Player getPlayer() {
		return player;
	}
	
	@Override
	public WrappedEntityPlayer getWrappedPlayer() {
		return entityPlayer;
	}
	
	@Override
	public void updatePlayer(Player player) {
		if(this.player != player) {
			this.player = player;
			this.entityPlayer = WrappedEntityPlayer.newInstance(player);
		}
	}
	
	@Override
	public String getName() {
		return player.getDisplayName();
	}
	
	@Override
	public boolean isOnline() {
		return player.isOnline();
	}
	
	@Override
	public void sendMessage(String message) {
		player.sendMessage(message);
	}
	
	@Override
	public Game getGame() {
		return game;
	}
	
	@Override
	public void setBuilderMode(boolean inBuilderMode) {
		this.inBuilderMode = inBuilderMode;
	}
	
	@Override
	public boolean isInBuilderMode() {
		return inBuilderMode;
	}
	
	@Override
	public BlockSelection getSelection() {
		return selection;
	}
	
	@Override
	public VoidSchematicCreator getCreator() {
		return creator;
	}
	
	@Override
	public void reset() {
		setGameMode(GameMode.ADVENTURE);
		setFlying(false);
		setPoints(0);
	}
	
	@Override
	public PlayerInventory getInventory() {
		return player.getInventory();
	}
	
	@Override
	public void setInventory(ItemStack[] items, ItemStack[] armor) {
		PlayerInventory playerInventory = getInventory();
		playerInventory.setContents(items);
		playerInventory.setArmorContents(armor);
	}
	
	@Override
	public void clearInventory() {
		setInventory(EMPTY_ITEMS, EMPTY_ARMOR);
	}
	
	@Override
	public Location getLocation() {
		return player.getLocation();
	}
	
	@Override
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
	
	@Override
	public void sendActionMessage(String message) {
		WrappedPacketPlayOutChat.newInstance(message).send(player);
	}
	
	@Override
	public void sendPacket(WrappedPacket packet) {
		entityPlayer.sendPacket(packet);
	}
	
	@Override
	public void teleport(double y) {
		Location newLocation = player.getLocation();
		newLocation.setY(y);
		teleport(newLocation);
	}
	
	@Override
	public void teleportUp(double y) {
		teleport(player.getLocation().add(0, y, 0));
	}
	
	@Override
	public void teleport(Location location) {
		player.teleport(location);
	}
	
	@Override
	public void teleportRelative(Location location) {
		entityPlayer.teleportRelative(location);
	}
	
	@Override
	public void setLevel(int level) {
		player.setLevel(level);
	}
	
	@Override
	public void clearEffects() {
		player.getActivePotionEffects().iterator().forEachRemaining(pe -> player.removePotionEffect(pe.getType()));
	}
	
	@Override
	public void playSound(Sound sound, float volume, float pitch) {
		player.playSound(player.getLocation(), sound, volume, pitch);
	}
	
	@Override
	public boolean isAlive() {
		return alive;
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {
		player.setGameMode(gameMode);
	}
	
	@Override
	public void setFlying(boolean flying) {
		player.setFlying(flying);
	}
	
	@Override
	public void revive() { // TODO ?
		alive = true;
	}
	
	@Override
	public boolean kill() {
		alive = false;
		boolean ending = game.kill(this);
		setGameMode(GameMode.SPECTATOR);
		setFlying(true);
		return ending;
	}
	
	@Override
	public void incrementPoints() {
		setPoints(getPoints() + 1);
	}
	
	@Override
	public void setPoints(int points) {
		points = Math.max(0, points);
		if(this.points != points) {
			this.points = points;
			pointsTimeAchieved = System.currentTimeMillis();
			boardEntry.updateScore();
		}
	}
	
	@Override
	public void decrementPoints() {
		setPoints(getPoints() - 1);
	}
	
	@Override
	public int getPoints() {
		return points;
	}
	
	@Override
	public long getPointsTimeAchieved() {
		return pointsTimeAchieved;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof GamePlayer && ((GamePlayer)obj).getPlayer().getUniqueId().equals(uuid);
	}
	
	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
	
}
