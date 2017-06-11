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
		AssetManager.font.getData().setScale(0.7f);
		AssetManager.font.draw(uiBatch, "PRESS SPACE TO START", -100, 50);
		AssetManager.font.draw(uiBatch, "Left thumbstick to move\nRight thumbstick to shoot and aim\nWhen round time runs out the round restarts \nand ghost that follows your past movment appears", -150, -0);
		AssetManager.font.getData().setScale(0.5f);
	}
}
