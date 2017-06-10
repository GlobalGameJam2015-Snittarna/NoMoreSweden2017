package com.jotom.nms;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Projectile extends GameObject {
	public enum Types {BULLET, DEACCELERTING, ROCKET};
	
	private float speed;
	private float angle;
	private float opacity;
	
	private int tag;
	
	private Types type;
	
	private boolean byCurrentPlayer;
	
	private Random random;
	
	public boolean isByCurrentPlayer() {
		return byCurrentPlayer;
	}
	
	public Projectile(Vector2 position, Vector2 size, Animation sprite, float angle, float speed, int tag, boolean byCurrentPlayer, Types type) {
		super(position, size, sprite);
		setOriginCenter();
		this.angle = angle;
		this.speed = speed;
		
		this.tag = tag;
		
		this.byCurrentPlayer = byCurrentPlayer;
		
		setRotation(angle*57.2957795f);
		
		opacity = 1f;
		
		this.type = type;
		
		random = new Random();
	}
	
	public void update(float dt) {
		setPosition(getPosition().add(moveDirection(dt).cpy()));
		
		Tile c = Map.collidesWihTile(getHitbox(), getScene());
		if (c != null && c.getType().isDestructible()) { 
			getScene().removeObject(c);
			if (c.getType().getMarker() == 'd') {
				int np = 10;
				for (int i = 0; i < np; i++) {
					float angle = i *360 / np + random.nextFloat() * 10;
					getScene().addObject(new Particle(getPosition(), new Vector2((float)Math.cos(angle), (float)Math.sin(angle)).scl(100), Color.BROWN));
				}
			}
		}
		if (c != null && !c.getType().isWalkable()) getScene().removeObject(this);
		
		if(type == Types.DEACCELERTING) {
			speed = lerp(speed, 0, 5*dt);
			if(speed <= 5) opacity = lerp(opacity, 0, 3*dt);
			getSprite().setColor(1,  1, 1, opacity);
		}
		
		super.update(dt);
	}
	
	public Types getType() {
		return type;
	}
	
	public Vector2 moveDirection(float dt) {
		return new Vector2(((float)Math.cos(angle)*speed)*dt, ((float)Math.sin(angle)*speed)*dt);
	}
	
	public int getTag() {
		return tag;
	}
}
