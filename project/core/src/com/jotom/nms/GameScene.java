package com.jotom.nms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GameScene extends Scene {
	private final int MAX_SCORE = 150;
	private final float MAX_ROUND_TIME = 10;
	
	public static boolean gameOver;
	private boolean firstRound;
	
	private Random random;
	
	// (e du) god (eller) praxis
	private int[] scores;
	private int winningPlayer;
	
	private float roundTime;
	private float nextRoundDelay;
	
	public GameScene() {
		random = new Random();
		
		scores = new int[2];
		
		Map.loadMap(1, this);
		super.update(0); // load tiles into the right list
		nextRoundDelay = 100000+1;
		
		System.out.println(this.getObjects().size() + " objects");
		
		getCamera().setPosition(10 * 32, (float) (7.5 * 32));
		
		//addObject(new Player(new Vector2(64, 64), new Vector2(32, 32), new Animation(new Sprite(AssetManager.getTexture("player1")))));
		/*music = Gdx.audio.newMusic(Gdx.files.internal("music/song-intro.mp3"));
		music.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(Music m) {
				music = Gdx.audio.newMusic(Gdx.files.internal("music/song-loop.mp3"));
				music.setLooping(true);
				music.play();
			}
		});
		music.play();*/
	}
	
	public void onLeave() {
		/*System.out.println("stopping music 1");
		if (music != null) {
			System.out.println("stopping music");
			music.setLooping(false);
			music.stop();
		}*/
	}
	
	public void update(float dt) {
		getCamera().update(dt);
		
		for(int i = 0; i < scores.length; i++) {
			if(scores[i] >= MAX_SCORE) {
				gameOver = true;
				winningPlayer = i;
			}
		}

		if(gameOver) {
			for (Controller controller : Controllers.getControllers()) {
				if(controller.getButton(7)) resetGame();
			}
			if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
				resetGame();
			}
		}
		
		updateRound(dt);
		
		super.update(dt);
	}
	
	boolean playing;
	
	public void updateRound(float dt) {
		roundTime += 1 * dt;
		
		boolean allPlayersDead[] = new boolean[]{true, true};
		
		for(int i = 0; i < 2; i++)
			for(GameObject g : getObjects()) {
				if(g instanceof Player) {
					if(((Player) g).getTag() == i && !((Player) g).isDead()) {
						allPlayersDead[i] = false;
					}
				}
			}
		
		if(roundTime >= MAX_ROUND_TIME || ((allPlayersDead[0] || allPlayersDead[1]) && !firstRound) || (firstRound && (scores[0] != 0 || scores[1] != 0))) {
			nextRoundDelay += 1;
			
			for (GameObject g : getObjects()) {
				if (g instanceof Player) {
					((Player)g).roundOver = true;
				}
			}
			if (!playing) {
				playing = true;
				AssetManager.getSound("next-level").play();
			}
			
			if(nextRoundDelay > 64*4) {
				dt = 1;
				if(!gameOver) resetRound();
				roundTime = 0;
				nextRoundDelay = 0;
				if(firstRound) firstRound = false;
			}
		}
	}
	
	public void resetRound() {
		playing = false;
		for(GameObject g : getObjects()) {
			if(g instanceof Projectile || g instanceof Tile) {
				removeObject(g);
			}
			
			if(g instanceof Player) {
				((Player) g).reset();
			}
		}
		
		addObject(new Player(
				Map.firstTile('s', this)
				.getPosition()
				.add(Utils.randomVector(32, random)), new Vector2(32, 32), new Animation(new Sprite(AssetManager.getTexture("player1"))), 0));
		addObject(new Player(Map.firstTile('S', this).getPosition().add(Utils.randomVector(32, random)), new Vector2(32, 32), new Animation(new Sprite(AssetManager.getTexture("player2"))), 1));
		
		Map.loadMap(1, this);
	}
	
	public void resetGame() {
		for(GameObject g : getObjects()) {
			removeObject(g);
		}
		
		Map.loadMap(1, this);
		
		gameOver = false;
		
		Game.setCurrentScene(new StartScreen());
	}
	
	public void drawUi(SpriteBatch uiBatch) {
		if(gameOver) {
			AssetManager.font.getData().setScale(1.2f);
			AssetManager.font.draw(uiBatch, "GAME OVER \nPlayer " + (winningPlayer+1) + " won!\nPRRESS START TO RESTART", -100, 0);
			AssetManager.font.getData().setScale(0.5f);
		}
		else {
			for(int i = 0; i < scores.length; i++)
				AssetManager.font.draw(uiBatch, "Player " + (i+1) + ": " + scores[i], -145, 70-i*24);
			if((int)(MAX_ROUND_TIME - roundTime) <= MAX_ROUND_TIME/3) {
				AssetManager.font.setColor(1, 0, 0, 1);
			}
			if (nextRoundDelay <= 0) AssetManager.font.draw(uiBatch, "TIME LEFT: " + (int)(MAX_ROUND_TIME - roundTime), -32+8, 0);
			if(nextRoundDelay > 0 && nextRoundDelay < 100000) {
				uiBatch.draw(new Sprite(AssetManager.getTexture("roundover")), -100, -50);
			}
			if((int)(MAX_ROUND_TIME - roundTime) <= MAX_ROUND_TIME/3) {
				AssetManager.font.setColor(1, 1, 1, 1);
			}
		}
		super.drawUi(uiBatch);
	}
	
	public void drawGame(SpriteBatch batch) {
		getObjects().sort(new Comparator<GameObject>() {

			@Override
			public int compare(GameObject o1, GameObject o2) {
				if (o1 instanceof Tile && o2 instanceof Tile) {
					if (((Tile)o1).getType().isWalkable() == ((Tile)o2).getType().isWalkable()) return 0;
					else if (((Tile)o1).getType().isWalkable()) return -1;
					else if (((Tile)o2).getType().isWalkable()) return 1;
					else return 0;
				}
				else if (o1 instanceof Tile) return -1;
				else if (o2 instanceof Tile) return 1;
				else return 0;
			}
		});
		
		super.drawGame(batch);
	}
	
	public boolean pointIsOnTile(Vector2 point) {
		for(GameObject g : getObjects()) {
			if(g instanceof Tile) {
				if(!((Tile) g).getType().isWalkable()) {
					if(new Rectangle(point.x, point.y, 1, 1).collision(g.getHitbox())) {
						return true;
					}
				}
			}
		}
		return false;
		
	}
	
	public void raiseScore(int index, int amount) {
		scores[index] += amount;
	}
	
	public boolean isGameOver() {
		return gameOver;
	}
}