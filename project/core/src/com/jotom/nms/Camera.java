package com.jotom.nms;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class Camera extends OrthographicCamera {

	/**
	 * an object to follow. Null if none set.
	 */
	private GameObject follow;
	private float deadZoneX, deadZoneY;
	
	/**
	 * construct a camera with a given viewport size.
	 * @param width
	 * @param height
	 */
	public Camera(int width, int height) {
		super(width, height);
	}
	
	/**
	 * 
	 * @param g
	 * @param deadZoneX goes in both directions.
	 * @param deadZoneY goes in both directions.
	 */
	public void setFollow(GameObject g, float deadZoneX, float deadZoneY) {
		this.follow = g;
		this.deadZoneX = deadZoneX;
		this.deadZoneY = deadZoneY;
	}
	
	public void update() {
		if (follow != null) {
			float dx = follow.getPosition().x - position.x;
			//float dy = position.y - follow.getPosition().y;
			if (Math.abs(dx) > deadZoneX) {
				position.x = MathUtils.lerp(position.x, dx - deadZoneX * Math.signum(dx), .1f);
				System.out.println(dx - deadZoneX * Math.signum(dx) - position.x);
			}
		}
		
		super.update();
	}
}
