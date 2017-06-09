package com.jotom.nms;

public class TileType {
	private char marker; // represents it in map file
	private Animation animation;
	private boolean destructible;
	
	public char getMarker() {
		return marker;
	}

	public Animation getAnimation() {
		return animation;
	}

	public boolean isDestructible() {
		return destructible;
	}

	public TileType(char marker, Animation animation, boolean destructible) {
		this.marker = marker;
		this.animation = animation;
		this.destructible = destructible;
	}
}
