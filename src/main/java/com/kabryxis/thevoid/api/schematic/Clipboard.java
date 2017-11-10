package com.kabryxis.thevoid.api.schematic;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.kabryxis.kabutils.spigot.utility.Vectors;

public class Clipboard {
	
	private final Player player;
	
	private Vector left;
	private Vector right;
	
	public Clipboard(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setLeft(Vector vector) {
		left = vector;
	}
	
	public void setRight(Vector vector) {
		right = vector;
	}
	
	public boolean isReady() {
		return left != null && right != null;
	}
	
	public Vector getLeft() {
		return left;
	}
	
	public Vector getRight() {
		return right;
	}
	
	public void extreme() {
		Vectors.extreme(left, right);
	}
	
}
