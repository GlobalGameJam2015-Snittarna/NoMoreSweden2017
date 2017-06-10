package com.jotom.nms;

import com.badlogic.gdx.math.Vector2;

public class Projectile extends GameObject {
	public enum Types {BULLET, DEACCELERTING, ROCKET};
	
	private float speed;
	private float angle;
	
	private int tag;
	
	public Projectile(Vector2 position, Vector2 size, Animation sprite, float angle, float speed, int tag) {
		super(position, size, sprite);
		setOriginCenter();
		this.angle = angle;
		this.speed = speed;
		
		this.tag = tag;
		
		setRotation(angle*57.2957795f);
	}
	
	public void update(float dt) {
		setPosition(getPosition().add(moveDirection(dt).cpy()));
		
		Tile c = Map.collidesWihTile(getHitbox(), getScene());
		if (c != null && c.getType().isDestructible()) getScene().removeObject(c);
		if (c != null && !c.getType().isWalkable()) getScene().removeObject(this);
		
		super.update(dt);
	}
	
	public Vector2 moveDirection(float dt) {
		return new Vector2(((float)Math.cos(angle)*speed)*dt, ((float)Math.sin(angle)*speed)*dt);
	}
	
	public int getTag() {
		return tag;
	}
}
