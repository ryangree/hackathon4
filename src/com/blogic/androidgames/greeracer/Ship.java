package com.blogic.androidgames.greeracer;

import com.blogic.androidgames.framework.DynamicGameObject3D;

public class Ship extends DynamicGameObject3D {
	public static float SHIP_VELOCITY = 20f;
	static int SHIP_ALIVE = 0;
	static int SHIP_EXPLODING = 1;
	static float SHIP_EXPLOSION_TIME = 1.6f;
	static float SHIP_RADIUS = 0.5f;

	public int lives;
	int state;
	float stateTime = 0;

	public Ship(float x, float y, float z) {
		super(x, y, z, SHIP_RADIUS);
		lives = 100;
		state = SHIP_ALIVE;
	}

	public void update(float deltaTime, float accelY) {
		if (state == SHIP_ALIVE) {
			velocity.set(accelY / 10 * SHIP_VELOCITY, 0, 0);
			position.add(velocity.x * deltaTime, 0, 0);
			if (position.x < Race.WORLD_MIN_X+7)
				position.x = Race.WORLD_MIN_X+7;
			if (position.x > Race.WORLD_MAX_X-7)
				position.x = Race.WORLD_MAX_X-7;
			bounds.center.set(position);
		} else {
			if (stateTime >= SHIP_EXPLOSION_TIME) {
				lives--;
				stateTime = 0;
				state = SHIP_ALIVE;
			}
		}
		stateTime += deltaTime;
	}

	public void kill() {
		state = SHIP_EXPLODING;
		stateTime = 0;
		velocity.x = 0;
	}
}
