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
	private final int MAX_SCORE = 20;
	private final float MAX_ROUND_TIME = 10;
	
	public static boolean gameOver;
	
	private Random random;
	
	// (e du) god (eller) praxis
	private int[] scores;
	private int winningPlayer;
	
	private float roundTime;
	
	public GameScene() {
		random = new Random();
		
		scores = new int[2];
		
		Map.loadMap(1, this);
		
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
		
		updateRound(dt);
		
		super.update(dt);
	}
	
	public void updateRound(float dt) {
		roundTime += 1 * dt;
		
		if(roundTime >= MAX_ROUND_TIME) {
			resetRound();
			roundTime = 0;
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
		
		addObject(new Player(new Vector2(64, 64+random.nextInt(200)), new Vector2(32, 32), new Animation(new Sprite(AssetManager.getTexture("player1"))), 0));
		addObject(new Player(new Vector2(400, 64+random.nextInt(200)), new Vector2(32, 32), new Animation(new Sprite(AssetManager.getTexture("player2"))), 1));
		
		Map.loadMap(1, this);
	}
	
	public void drawUi(SpriteBatch uiBatch) {
		if(gameOver) AssetManager.font.draw(uiBatch, "GAME OVER \nPlayer " + (winningPlayer+1) + " won!", 0, 0);
		
		for(int i = 0; i < scores.length; i++)
			AssetManager.font.draw(uiBatch, "Player " + (i+1) + ": " + scores[i], -150, 0-i*32);
		AssetManager.font.draw(uiBatch, "TIME LEFT: " + (int)(MAX_ROUND_TIME - roundTime), 0, 0);
		super.drawUi(uiBatch);
	}
	
	public void drawGame(SpriteBatch batch) {
		super.drawGame(batch);
	}
	
	public void raiseScore(int index) {
		scores[index] += 1;
	}
}
