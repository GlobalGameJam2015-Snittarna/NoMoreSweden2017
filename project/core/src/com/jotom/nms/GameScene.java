package com.jotom.nms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GameScene extends Scene {
	private final int MAX_SCORE = 1000;
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

		
		getCamera().translate(10 * 32, (float) (7.5 * 32));
		
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
		if(Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			resetRound();
		}

		for(int i = 0; i < scores.length; i++) {
			if(scores[i] >= MAX_SCORE) {
				gameOver = true;
				winningPlayer = i;
			}
		}
		if(nextRoundDelay > 0) dt = 0;
		updateRound(dt);
		
		super.update(dt);
	}
	
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
			
			if(nextRoundDelay > 64*4) {
				dt = 1;
				resetRound();
				roundTime = 0;
				nextRoundDelay = 0;
				if(firstRound) firstRound = false;
			}
		}
	}
	
	public void resetRound() {
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
		Game.setCurrentScene(new StartScreen());
	}
	
	public void drawUi(SpriteBatch uiBatch) {
		if(gameOver) AssetManager.font.draw(uiBatch, "GAME OVER \nPlayer " + (winningPlayer+1) + " won!", 0, 0);
		
		for(int i = 0; i < scores.length; i++)
			AssetManager.font.draw(uiBatch, "Player " + (i+1) + ": " + scores[i], -145, 70-i*24);
		if((int)(MAX_ROUND_TIME - roundTime) <= MAX_ROUND_TIME/3) {
			AssetManager.font.setColor(1, 0, 0, 1);
		}
		AssetManager.font.draw(uiBatch, "TIME LEFT: " + (int)(MAX_ROUND_TIME - roundTime), -32+8, 0);
		if(nextRoundDelay > 0 && nextRoundDelay < 100000) {
			uiBatch.draw(new Sprite(AssetManager.getTexture("roundover")), -100, -50);
		}
		if((int)(MAX_ROUND_TIME - roundTime) <= MAX_ROUND_TIME/3) {
			AssetManager.font.setColor(1, 1, 1, 1);
		}
		super.drawUi(uiBatch);
	}
	
	public void drawGame(SpriteBatch batch) {
		super.drawGame(batch);
	}
	
	public void raiseScore(int index, int amount) {
		scores[index] += amount;
	}
}