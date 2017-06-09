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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GameScene extends Scene {
		
	public static boolean gameOver;
	
	private Random random;
	
	public GameScene() {
		
		random = new Random();
		
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
		super.update(dt);
	}
	
	public void drawUi(SpriteBatch uiBatch) {
		if(gameOver) AssetManager.font.draw(uiBatch, "GAME OVER", 0, 0);
		super.drawUi(uiBatch);
	}
	
	public void drawGame(SpriteBatch batch) {
		
	}
}
