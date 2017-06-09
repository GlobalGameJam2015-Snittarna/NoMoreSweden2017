package com.jotom.nms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Map {
	
	private final static int TILESIZE = 32;
	
	private static TileType[] tileTypes = new TileType[] {
		new TileType('x', AssetManager.getTexture("indestructible-tile"), false),
		new TileType('d', AssetManager.getTexture("destructible-tile"), false),
		
	};
	
	private static TileType findTileType(char marker) {
		for (TileType t : tileTypes) {
			if (t.getMarker() == marker) return t;
		}
		System.out.println("couldnt find " + marker);
		return null;
	}
	
	public static void loadMap(int map, Scene scene) {
		String file = Gdx.files.internal("maps/" + map + ".map").readString();
		
		String[] lines = file.split("\n");
		int y = 0;
		for (String line : lines) {
			int x = 0;
			for (char c : line.toCharArray()) {
				TileType t = findTileType(c);
				if (t != null) scene.addObject(new Tile(new Vector2(x, y).scl(TILESIZE), t));
				x++;
			}
			y++;
		}
	}
}
