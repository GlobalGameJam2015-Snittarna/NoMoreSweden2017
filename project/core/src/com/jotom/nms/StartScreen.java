package com.jotom.nms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StartScreen extends Scene {
	public StartScreen() {
		super();
	}
	
	public void update(float dt) {
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			Game.setCurrentScene(new GameScene());
		}
		super.update(dt);
	}
	
	public void drawUi(SpriteBatch uiBatch) {
		super.drawUi(uiBatch);
		AssetManager.font.draw(uiBatch, "PRESS SPACE TO START", -100, 50);
		AssetManager.font.draw(uiBatch, "WASD to move \nSpace to drop bombs\nPick up barrels to build bombs when you are a boat\nPick up planks to build a boat\nKill hands and surive for points", -165, -0);
	}
}
