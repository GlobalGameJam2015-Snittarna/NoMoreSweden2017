package com.jotom.nms;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Player extends GameObject {
	private final float ORGINAL_SPEED = 50;
	
	private final int START_HEALTH = 1;
	
	public boolean isCurrentPlayer;
	
	private boolean cantDoNextMove;
	
	public enum WeaponTypes {PISTOL, SPREADGUN, MACHINE_GUN};
	public enum Move {SHOOT, WALK};
	
	private Vector2 startPoint;
	private Vector2 movmentDirection;
	
	private ArrayList<Vector2> moveDirections;
	
	private ArrayList<Move> moves;
	
	private int health;
	private int currentMoveIndex;
	private int shootDelay;
	private int maxShootDelay;
	private int tag;
	
	private float speed;
	private float shootAngle;
	private ArrayList<Float> shootAngels;
	
	private WeaponTypes weapon;
	
	// temp
	private int leftKey, rightKey, downKey, upKey, shootKey;
	
	public Player(Vector2 position, Vector2 size, Animation sprite, int tag) {
		super(position, size, sprite);
		setOriginCenter();
		
		this.tag = tag;
		
		startPoint = position.cpy();
		
		moveDirections = new ArrayList<Vector2>();
		moves = new ArrayList<Move>();
		shootAngels = new ArrayList<Float>();
		
		movmentDirection = new Vector2(0, 0);
		
		isCurrentPlayer = true;
		
		maxShootDelay = 64;
		
		speed = ORGINAL_SPEED;
		
		health = START_HEALTH;
		
		leftKey = (tag == 0) ? Keys.LEFT : Keys.A;
		rightKey = (tag == 0) ? Keys.RIGHT : Keys.D;
		upKey = (tag == 0) ? Keys.UP : Keys.W;
		downKey = (tag == 0) ? Keys.DOWN : Keys.S;
		shootKey = (tag == 0) ? Keys.M : Keys.Q;
	}
	
	public void update(float dt) {
		setRotation(shootAngle*57.2957795f);
		
		if(moveDirections.size() > 1 && Gdx.input.isKeyPressed(Keys.SPACE)) reset();
		
		if(health > 0) 
			for(GameObject g : getScene().getObjects()) {
				if(g instanceof Projectile) {
					if(g.getHitbox().collision(getHitbox()) && ((Projectile) g).getTag() != tag) {
						health -= 1;
						if(health <= 0) {
							setSprite(new Animation(new Sprite(AssetManager.getTexture("dead" + (tag+1)))));
						}
						getScene().removeObject(g);
					}
				}
			}
		
		if(isCurrentPlayer && health > 0) {
			if(Gdx.input.isKeyPressed(leftKey)) {
				shootAngle += 5 * dt;
			}
			
			if(Gdx.input.isKeyPressed(rightKey)) {
				shootAngle -= 5 * dt;
			}
			
			if(Gdx.input.isKeyPressed(upKey)) {
				movmentDirection = new Vector2(((float)Math.cos(shootAngle)*speed)*dt, ((float)Math.sin(shootAngle)*speed)*dt);
			}
			
			if(Gdx.input.isKeyPressed(downKey)) {
				movmentDirection = new Vector2(((float)Math.cos(shootAngle)*speed)*dt, ((float)Math.sin(shootAngle)*speed)*dt);
			}
			
			if(!Gdx.input.isKeyPressed(downKey) && !Gdx.input.isKeyPressed(upKey)) {
				movmentDirection = Vector2.Zero;
			}
			
			if(shootDelay > 0) 
				shootDelay += 1;
			if(shootDelay == maxShootDelay) {
				shootDelay = 0;
			}
			
			if(Gdx.input.isKeyPressed(shootKey) && shootDelay == 0) {
				shoot();
				shootDelay = 1;
				moves.add(Move.SHOOT);
			} else {
				moves.add(Move.WALK);
			}
			
			setPosition(getPosition().add(movmentDirection.cpy()));
			System.out.println(movmentDirection.toString() + " LOOOOL ");
			moveDirections.add(movmentDirection.cpy());
			currentMoveIndex += 1;
			shootAngels.add(new Float(shootAngle));
		}
		
		if(!isCurrentPlayer) {
			if(!cantDoNextMove && currentMoveIndex != moveDirections.size()-1 && health > 0) {
				doMove(currentMoveIndex);
				currentMoveIndex++;
			}
		}
		
		super.update(dt);
	}
	
	public void doMove(int index) {
		if(currentMoveIndex != moveDirections.size()-1) {
			shootAngle = shootAngels.get(currentMoveIndex);
			
			setPosition(getPosition().add(moveDirections.get(currentMoveIndex).cpy()));
		
			if(moves.get(currentMoveIndex) == Move.SHOOT) {
				shoot();
			}
		}
	}
	
	public void shoot() {
		getScene().addObject(new Projectile(new Vector2(getPosition().cpy().x + getSize().x/2, getPosition().cpy().y + getSize().y/2), new Vector2(8, 8), new Animation(new Sprite(AssetManager.getTexture("bullet"))), shootAngle, 8, tag));
	}
	
	public void reset() {
		isCurrentPlayer = false;
		
		currentMoveIndex = 0;
		setPosition(startPoint.cpy());
		shootAngle = 0;
		movmentDirection = Vector2.Zero;
		health = START_HEALTH;
		speed = ORGINAL_SPEED;
		
		setSprite(new Animation(new Sprite(AssetManager.getTexture("player" + (tag+1)))));
	}
}
