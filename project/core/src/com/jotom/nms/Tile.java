package com.jotom.nms;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Tile extends GameObject {

	public Tile(Vector2 position, TileType type) {
		super(position, new Vector2(32, 32), new Animation(new Sprite(type.getTexture())));
	}
	
	public void draw(SpriteBatch batch) {
		//System.out.println(getPosition());
		super.draw(batch);
	}

}
