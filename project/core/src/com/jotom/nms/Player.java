package com.jotom.nms;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class Player extends GameObject {
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
	
	public Player(Vector2 position, Vector2 size, Animation sprite) {
		super(position, size, sprite);
		setOriginCenter();
		
		startPoint = position.cpy();
		
		moveDirections = new ArrayList<Vector2>();
		moves = new ArrayList<Move>();
		shootAngels = new ArrayList<Float>();
		
		movmentDirection = new Vector2(0, 0);
		
		isCurrentPlayer = true;
		
		speed = 50;
		
		leftKey = (tag == 0) ? Keys.LEFT : Keys.A;
		rightKey = (tag == 0) ? Keys.RIGHT : Keys.D;
		upKey = (tag == 0) ? Keys.UP : Keys.W;
		downKey = (tag == 0) ? Keys.DOWN : Keys.S;
		shootKey = (tag == 0) ? Keys.ALT_RIGHT : Keys.Q;
	}
	
	public void update(float dt) {
		setRotation(shootAngle*57.2957795f);
		
		if(moveDirections.size() > 1 && Gdx.input.isKeyPressed(Keys.SPACE)) reset();
		
		if(isCurrentPlayer) {
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
			
			if(Gdx.input.isKeyPressed(shootKey)) {
				moves.add(Move.SHOOT);
			} else {
				moves.add(Move.WALK);
			}
			
			setPosition(getPosition().add(movmentDirection.cpy()));
			System.out.println(movmentDirection.toString() + " LOOOOL ");
			moveDirections.add(movmentDirection.cpy());
			currentMoveIndex += 1;
			shootAngels.add(new Float(shootAngle));
		} else {
			if(!cantDoNextMove && currentMoveIndex != moveDirections.size()-1) {
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
				
			}
		}
	}
	
	public void reset() {
		isCurrentPlayer = false;
		
		currentMoveIndex = 0;
		setPosition(startPoint.cpy());
		shootAngle = 0;
		movmentDirection = Vector2.Zero;
		health = 1;
	}
}
