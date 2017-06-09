package com.jotom.nms;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class BloodSplatter extends GameObject {
	boolean hasHitGround;
	
	public float distanceToGround;
	public float speed;
	public float angle;
	
	Random random = new Random();
	
	public BloodSplatter(Vector2 position) {
		super(position, new Vector2(32, 32), new Animation(new Sprite(AssetManager.getTexture("bloodSplatter1"))));
		distanceToGround = 1f;
		
		angle = random.nextInt(180);
		
		speed = 9+random.nextInt(3);
	}
	
	public void update(float dt) {
		if(!hasHitGround) {
			distanceToGround += speed * dt;
			speed -= 6*dt;
			setPosition(getPosition().add(new Vector2(new Vector2((float)Math.cos(angle*0.0174532925)*speed, (float)Math.sin(angle*0.0174532925)*speed))));
		}
		
		setScale((distanceToGround/7) + 1);
		
		if(distanceToGround <= 0.01f) {
			setSprite(new Animation(new Sprite(AssetManager.getTexture("bloodSplatter2"))));
			speed = 0;
			distanceToGround = 0;
			hasHitGround = true;
		}
			
		super.update(dt);
	}
}
