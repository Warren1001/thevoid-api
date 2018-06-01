package com.kabryxis.thevoid.api.game;

import com.kabryxis.kabutils.spigot.version.wrapper.entity.player.WrappedEntityPlayer;
import com.kabryxis.kabutils.spigot.version.wrapper.packet.WrappedPacket;
import com.kabryxis.thevoid.api.util.arena.schematic.BlockSelection;
import com.kabryxis.thevoid.api.impl.arena.schematic.VoidSchematicCreator;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Set;

public interface GamePlayer {
	
	Player getPlayer();
	
	WrappedEntityPlayer getWrappedPlayer();
	
	void updatePlayer(Player player);
	
	Game getGame();
	
	String getName();
	
	boolean isOnline();
	
	void sendMessage(String message);
	
	void setBuilderMode(boolean inBuilderMode);
	
	boolean isInBuilderMode();
	
	BlockSelection getSelection();
	
	VoidSchematicCreator getCreator();
	
	void reset();
	
	PlayerInventory getInventory();
	
	void setInventory(ItemStack[] items, ItemStack[] armor);
	
	void clearInventory();
	
	Location getLocation();
	
	Set<Block> getStandingBlocks(); // TODO move this elsewhere
	
	void sendActionMessage(String message);
	
	void sendPacket(WrappedPacket packet);
	
	void teleport(double y);
	
	void teleportUp(double y);
	
	void teleport(Location location);
	
	void setLevel(int level);
	
	void clearEffects();
	
	void playSound(Sound sound, float volume, float pitch);
	
	boolean isAlive();
	
	void setGameMode(GameMode gameMode);
	
	void setFlying(boolean flying);
	
	void revive();
	
	boolean kill();
	
	void incrementPoints();
	
	void setPoints(int points);
	
	void decrementPoints();
	
	int getPoints();
	
	long getPointsTimeAchieved();
	
	@Override
	boolean equals(Object obj);
	
	@Override
	int hashCode();
	
}
