package com.jotom.nms;

import com.badlogic.gdx.math.Vector2;

public class Projectile extends GameObject {
	public enum Types {BULLET, ROCKET};
	
	private float speed;
	private float angle;
	
	public Projectile(Vector2 position, Vector2 size, Animation sprite) {
		super(position, size, sprite);
	}
	
	public Vector2 moveDirection() {
		return new Vector2((float)Math.cos(angle)*speed, (float)Math.sin(angle)*speed);
	}
}
