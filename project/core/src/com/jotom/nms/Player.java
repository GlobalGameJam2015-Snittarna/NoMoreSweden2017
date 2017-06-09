package com.jotom.nms;

import java.util.ArrayList;

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
	
	private float speed;
	
	private WeaponTypes weapon;
	
	private float shootAngle;
	
	public Player(Vector2 position, Vector2 size, Animation sprite) {
		super(position, size, sprite);
	}
	
	public void update(float dt) {
		if(isCurrentPlayer) {
			getPosition().add(movmentDirection.cpy());
			moveDirections.add(movmentDirection.cpy());
			currentMoveIndex += 1;
			
		} else {
			if(!cantDoNextMove) {
				doMove(currentMoveIndex);
				currentMoveIndex++;
			}
		}
		
		super.update(dt);
	}
	
	public void doMove(int index) {
		if(currentMoveIndex != moveDirections.size()) getPosition().add(moveDirections.get(currentMoveIndex).cpy());
		
		if(moves.get(currentMoveIndex) == Move.SHOOT) {
			
		}
	}
	
	public void reset() {
		currentMoveIndex = 0;
		setPosition(startPoint.cpy());
		health = 1;
	}
}
