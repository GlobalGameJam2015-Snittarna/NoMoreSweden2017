package com.jotom.nms;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Player extends GameObject {
	private final float ORGINAL_SPEED = 150;
	private final float MAX_ANIMATION_COUNT = 0.2f;
	
	private final int START_HEALTH = 1;
	
	public boolean isCurrentPlayer;
	public boolean roundOver;
	
	private boolean cantDoNextMove;
	private boolean hasGivenScore;
	
	public enum WeaponTypes {PISTOL, SPREADGUN, MACHINE_GUN, ROCKET_LAUNCHER};
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
	private int score;
	
	private int currentFrame;
	
	private float animationCount;
	
	private float speed;
	private float shootAngle;
	private float showArrowTime;
	private float showArrowOpacity;
	private float moveAngle;
	private ArrayList<Float> shootAngels;
	
	private WeaponTypes weapon;
	
	private Random random = new Random();
	
	// temp
	private int leftKey, rightKey, downKey, upKey, shootKey;
	
	boolean notMoving;
	boolean canNotAim;
	
	private float controllerShootAngle;
	
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
		
		shootAngle = random.nextFloat() * 2 * (float)Math.PI;
		
		showArrowTime = 5;
		showArrowOpacity = 1;
		
		weapon = WeaponTypes.PISTOL;
		giveRandomWeapon();
	}
	
	public void update(float dt) {
		int index = 1;
		for (Controller controller : Controllers.getControllers()) {
			if(tag == index) {
				float axisValueX = controller.getAxis(1);
				float axisValueY = controller.getAxis(0);
				
				if(axisValueX < 0.15f && axisValueX > -0.15f) axisValueX = 0;
				if(axisValueY < 0.15f && axisValueY > -0.15f) axisValueY = 0;

				notMoving = axisValueX == 0 && axisValueY == 0;
				
				moveAngle = (float)Math.atan2(-axisValueY, axisValueX);
				
				float axisValueRightX = controller.getAxis(3);
				float axisValueRightY = controller.getAxis(2);
				
				if(axisValueRightX < 0.1f && axisValueRightX > -0.1f) axisValueRightX = 0;
				if(axisValueRightY < 0.1f && axisValueRightY > -0.1f) axisValueRightY = 0;
				
				canNotAim = axisValueRightX == 0 && axisValueRightY == 0;
				
				controllerShootAngle = (float)Math.atan2(-axisValueRightY, axisValueRightX);
			}
			index--;
		}
		
		// wowww j�vla copy-cat
		if (roundOver || ((GameScene) getScene()).isGameOver()) dt = 0;
		
		setRotation(shootAngle*57.2957795f);
				
		if(health > 0) {
			for(GameObject g : getScene().getObjects()) {
				if(g instanceof Explosion) {
					if(g.getHitbox().collision(getHitbox()) && ((Explosion) g).isDeadliy()) {
						health = 0;
						setSprite(new Animation(new Sprite(AssetManager.getTexture("dead" + (tag+1)))));
						getSprite().setColor(1, 0, 0, 1);
						((GameScene)getScene()).raiseScore(getTag() == 1 ? 0 : 1, (isCurrentPlayer ? 10 : 1));
						getScene().getCamera().shake(1 + (isCurrentPlayer ? 3 : 0));
					}
				}
				
				if(g instanceof Projectile) {
					if(g.getHitbox().collision(getHitbox()) && ((Projectile) g).getTag() != tag) {
						if(((Projectile) g).getType() == Projectile.Types.ROCKET) getScene().addObject(new Explosion(new Vector2(getPosition().x+16, getPosition().y)));
						health -= 1;
						if(health <= 0) {
							setSprite(new Animation(new Sprite(AssetManager.getTexture("dead" + (tag+1)))));
							getSprite().setColor(1, 0, 0, 1);
							((GameScene)getScene()).raiseScore(getTag() == 1 ? 0 : 1, (isCurrentPlayer ? 10 : 1) * (((Projectile)g).isByCurrentPlayer() ? 2 : 1));
							getScene().getCamera().shake(1 + (isCurrentPlayer ? 3 : 0) + (((Projectile)g).isByCurrentPlayer() ? 3 : 0));
						}
						getScene().removeObject(g);
						getScene().addObject(new BloodSplatter(getPosition()));
						int np = 100;
						for (int i = 0; i < np; i++) {
							float angle = i *360 / np + random.nextFloat() * 10;
							getScene().addObject(new Particle(getPosition(), new Vector2((float)Math.cos(angle), (float)Math.sin(angle)).scl(50 + random.nextFloat() * 100), Color.RED, 5));
						}
						AssetManager.getSound("death").play();
					}
				}
			}
		} else {
			getSprite().setColor(1, lerp(getSprite().getColor().g, 1, 3f*dt), lerp(getSprite().getColor().b, 1, 3f*dt), 1);
		}
		
		showArrowTime -= 1*dt;
		
		if(isCurrentPlayer && health > 0) {
			if(showArrowTime <= 1) {
				showArrowOpacity -= 1*dt;
				showArrowOpacity  = MathUtils.clamp(showArrowOpacity, 0, 1);
			}
			
			showArrowTime -= 1*dt;
			if(Gdx.input.isKeyPressed(leftKey)) {
				shootAngle += 5 * dt;
			}
			
			if(Gdx.input.isKeyPressed(rightKey)) {
				shootAngle -= 5 * dt;
			}
			
			// N�GON GL�MMDE VISST EN LINTE CONTROLL(�R DET DEN ONDE RUBEN M�N TRO)
			if(tag == 3) {
				if(Gdx.input.isKeyPressed(upKey)) {
					movmentDirection = new Vector2(((float)Math.cos(shootAngle)*speed)*dt, ((float)Math.sin(shootAngle)*speed)*dt);
					animationCount += 1*dt;
				}
				
				if(Gdx.input.isKeyPressed(downKey)) {
					movmentDirection = new Vector2(((float)Math.cos(shootAngle)*-speed)*dt, ((float)Math.sin(shootAngle)*-speed)*dt);
					animationCount += 1*dt;
				}
				
				if(!Gdx.input.isKeyPressed(downKey) && !Gdx.input.isKeyPressed(upKey)) {
					movmentDirection = Vector2.Zero;
					currentFrame = 0;
					setSprite(new Animation(new Sprite(AssetManager.getTexture("player" + (tag+1) + "step" + currentFrame))));
					setRotation(shootAngle*57.2957795f);
				}
			} else {
				if(!roundOver && !((GameScene) getScene()).isGameOver()) {
					if(!notMoving) movmentDirection = new Vector2(((float)Math.cos(moveAngle)*speed)*dt, ((float)Math.sin(moveAngle)*speed)*dt);
					else movmentDirection = Vector2.Zero;
				
					if(!canNotAim) shootAngle = controllerShootAngle;
					
					if(movmentDirection.equals(Vector2.Zero)) {
						currentFrame = 0;
						setSprite(new Animation(new Sprite(AssetManager.getTexture("player" + (tag+1) + "step" + currentFrame))));
						setRotation(shootAngle*57.2957795f);
					} else {
						animationCount += 1*dt;
					}
				}
			}
			
			if(shootDelay > 0) 
				shootDelay += 1;
			if(shootDelay == maxShootDelay) {
				shootDelay = 0;
			}
			
			if((Gdx.input.isKeyPressed(shootKey) || !canNotAim) && shootDelay == 0) {
				shoot();
				shootDelay = 1;
				moves.add(Move.SHOOT);
			} else {
				moves.add(Move.WALK);
			}
			
			tryMove(movmentDirection.cpy());
			
			if(!roundOver && !((GameScene) getScene()).isGameOver()) moveDirections.add(movmentDirection.cpy());
			currentMoveIndex += 1;
			shootAngels.add(new Float(shootAngle));
		}
		
		if(!isCurrentPlayer && !roundOver && !((GameScene) getScene()).isGameOver()) {
			if(!cantDoNextMove && currentMoveIndex != moveDirections.size()-1 && health > 0) {
				doMove(currentMoveIndex, dt);
				currentMoveIndex++;
			}
		}
		
		if(animationCount >= MAX_ANIMATION_COUNT && health > 0) {
			if(currentFrame == 4) currentFrame = 0;
			String n = "player" + (tag+1) + "step" + currentFrame;
			setSprite(new Animation(new Sprite(AssetManager.getTexture(n))));
			setRotation(shootAngle*57.2957795f);
			currentFrame++;
			animationCount = 0;
		}
		
		if(((GameScene) getScene()).isGameOver() || roundOver) {
			speed = 0;
			movmentDirection = Vector2.Zero;
		}
		
		super.update(dt);
	}
	
	private void tryMove(Vector2 delta) {
		Vector2 oldPos = getPosition().cpy();
		final int steps = 5;
		
		for (int step = 0; step < steps; step++) {
			setPosition(oldPos.cpy().add(delta.x * step / steps, 0));
			Tile t = Map.collidesWihTile(getHitbox(), getScene());
			if (t != null && ! t.getType().isWalkable()) {
				setPosition(oldPos.cpy().add(delta.x * (step - 1) / steps, 0));	
				break;
			}
		}
		
		oldPos = getPosition().cpy();
		
		for (int step = 0; step < steps; step++) {
			setPosition(oldPos.cpy().add(0, delta.y * step / steps));
			Tile t = Map.collidesWihTile(getHitbox(), getScene());
			if (t != null && ! t.getType().isWalkable()) {
				setPosition(oldPos.cpy().add(0, delta.y * (step - 1) / steps));	
				break;
			}
		}
	}
	
	public void doMove(int index, float dt) {
		if(currentMoveIndex != moveDirections.size()-1) {
			shootAngle = shootAngels.get(currentMoveIndex);
			
			tryMove(moveDirections.get(currentMoveIndex).cpy());
			
			if(moveDirections.get(currentMoveIndex).equals(Vector2.Zero)) {
				currentFrame = 0;
				setSprite(new Animation(new Sprite(AssetManager.getTexture("player" + (tag+1) + "step" + currentFrame))));
				setRotation(shootAngle*57.2957795f);
			} else {
				animationCount += 1*dt;
			}
		
			if(moves.get(currentMoveIndex) == Move.SHOOT) {
				shoot();
			}
		}
	}
	
	public void giveRandomWeapon() {
		int id = random.nextInt(4);
		
		if(id == 3 && random.nextBoolean()) weapon = WeaponTypes.ROCKET_LAUNCHER;
		else weapon = WeaponTypes.PISTOL;
		
		if(id == 0) weapon = WeaponTypes.PISTOL;
		if(id == 1) weapon = WeaponTypes.MACHINE_GUN;
		if(id == 2) weapon = WeaponTypes.SPREADGUN;
	}
	
	public void shoot() {
		// fan e detta, men som en opperturnist brukar jag det
		if (roundOver || ((GameScene) getScene()).isGameOver()) return;
		
		if(weapon == WeaponTypes.PISTOL) {
			getScene().addObject(new Projectile(new Vector2(getPosition().cpy().x + getSize().x/2, getPosition().cpy().y + getSize().y/2), new Vector2(8, 8), new Animation(new Sprite(AssetManager.getTexture("bullet"))), shootAngle, 500, tag, isCurrentPlayer, Projectile.Types.BULLET));
			maxShootDelay = 32; 
			AssetManager.getSound("shot").play();
		}
		
		if(weapon == WeaponTypes.MACHINE_GUN) {
			getScene().addObject(new Projectile(new Vector2(getPosition().cpy().x + getSize().x/2, getPosition().cpy().y + getSize().y/2), new Vector2(8, 8), new Animation(new Sprite(AssetManager.getTexture("bullet"))), shootAngle, 500, tag, isCurrentPlayer, Projectile.Types.BULLET));
			maxShootDelay = 64/4; 
			AssetManager.getSound("other-shot").play();
		}
		
		if(weapon == WeaponTypes.SPREADGUN) {
			for(int i = -1; i < 2; i++) {
				getScene().addObject(new Projectile(new Vector2(getPosition().cpy().x + getSize().x/2, getPosition().cpy().y + getSize().y/2), new Vector2(8, 8), new Animation(new Sprite(AssetManager.getTexture("bullet"))), shootAngle+((float)Math.PI/8)*i, 500, tag, isCurrentPlayer, Projectile.Types.DEACCELERTING));
			}
			maxShootDelay = 32; 
			AssetManager.getSound("shot").play();
		}
		
		if(weapon == WeaponTypes.ROCKET_LAUNCHER) {
			getScene().addObject(new Projectile(new Vector2(getPosition().cpy().x + getSize().x/2, getPosition().cpy().y + getSize().y/2), new Vector2(12, 12), new Animation(new Sprite(AssetManager.getTexture("rocket"))), shootAngle, 500, tag, isCurrentPlayer, Projectile.Types.ROCKET));
			maxShootDelay = 64; 
			AssetManager.getSound("other-shot").play();
		}
	}
	
	public Rectangle getHitbox() {
		return new Rectangle(getPosition().x+7, getPosition().y+7, 22, 22);
	}
	
	public void draw(SpriteBatch batch) {
		if(health > 0) {
			int length = 32*5;
			for(int i = 0; i < length; i++) {
				//f�rmodligen ingen som kommer m�rka men s� kan det g�
				if(i % 4 == 0 && ((GameScene) getScene()).pointIsOnTile(new Vector2(getPosition().cpy().x + ((float)Math.cos(shootAngle)*i) + getSize().x/2, getPosition().cpy().y+ ((float)Math.sin(shootAngle)*i) + getSize().y/2))) {
					break;
				}
				Animation a = new Animation(new Sprite(AssetManager.getTexture("plot")));
				a.setSize(1, 1);
				float t = (float)i;
				a.setColor((tag == 0) ? 1 : 0, 0, (tag == 1) ? 1 : 0, 1 - (t/((float)length)));
				a.setPosition(getPosition().cpy().x + ((float)Math.cos(shootAngle)*i) + getSize().x/2, getPosition().cpy().y+ ((float)Math.sin(shootAngle)*i) + getSize().y/2);
				
				a.draw(batch);
			}
			
			if(isCurrentPlayer) {
				Animation a = new Animation(new Sprite(AssetManager.getTexture("you")));
				a.setColor((tag == 0) ? 1 : 0, 0, (tag == 1) ? 1 : 0, showArrowOpacity);
				a.setPosition(getPosition().x-16, getPosition().y+40);
				a.draw(batch);
			}
		}
		super.draw(batch);
	}
	
	public void reset() {
		isCurrentPlayer = false;
		
		currentMoveIndex = 0;
		setPosition(startPoint.cpy());
		shootAngle = 0;
		movmentDirection = Vector2.Zero;
		health = START_HEALTH;
		speed = ORGINAL_SPEED;
		
		roundOver = false;
		
		setSprite(new Animation(new Sprite(AssetManager.getTexture("player" + (tag+1)))));
	}
	
	public boolean isDead() {
		return health <= 0;
	}
	
	public int getTag() {
		return tag;
	}
}
