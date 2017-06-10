package com.jotom.nms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Particle extends GameObject {

	private Vector2 velocity;
	private Color color;
	private float lifetime = .5f;
	
	public Particle(Vector2 position, Vector2 velocity, Color color) {
		super(position, new Vector2(1, 1).scl(10), new Animation(new Sprite(AssetManager.getTexture("plot"))));
		this.velocity = velocity;
		this.color = color;
		getSprite().setColor(color);
	}
	
	public void update(float dt) {
		super.update(dt);
		setPosition(getPosition().add(velocity.cpy().scl(dt)));
		lifetime -= dt;
		if (lifetime <= 0) {
			getScene().removeObject(this);
			System.out.println("particle died");
		}
	}
}
