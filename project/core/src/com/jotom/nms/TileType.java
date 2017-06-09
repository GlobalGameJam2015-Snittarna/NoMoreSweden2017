package com.jotom.nms;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TileType {
	private char marker; // represents it in map file
	private TextureRegion texture;
	private boolean destructible;
	
	public char getMarker() {
		return marker;
	}

	public TextureRegion getTexture() {
		return texture;
	}

	public boolean isDestructible() {
		return destructible;
	}

	public TileType(char marker, TextureRegion texture, boolean destructible) {
		this.marker = marker;
		this.texture = texture;
		this.destructible = destructible;
	}
}
