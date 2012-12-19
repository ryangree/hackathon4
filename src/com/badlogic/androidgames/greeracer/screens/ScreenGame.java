package com.badlogic.androidgames.greeracer.screens;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.view.Display;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.FPSCounter;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.impl.GLGame;
import com.badlogic.androidgames.framework.impl.GLScreen;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;
import com.badlogic.androidgames.greeracer.Assets;
import com.badlogic.androidgames.greeracer.Gear;
import com.badlogic.androidgames.greeracer.Race;
import com.badlogic.androidgames.greeracer.RaceRenderer;
import com.badlogic.androidgames.greeracer.Settings;
import com.badlogic.androidgames.greeracer.Ship;
import com.badlogic.androidgames.greeracer.Race.RaceListener;

public class ScreenGame extends GLScreen {
	private static final int GAME_PREVIEW = 0;
	private static final int GAME_RUNNING = 1;
	private static final int GAME_PAUSED = 2;
	private static final int GAME_OVER = 3;

	int state;
	Camera2D guiCam;
	Vector2 touchPoint;
	SpriteBatcher batcher;
	Race race;
	RaceListener raceListener;
	RaceRenderer renderer;
	Rectangle pauseBounds;
	Rectangle resumeBounds;
	Rectangle quitBounds;
	Rectangle leftBounds;
	Rectangle rightBounds;
	Rectangle shotBounds;
	int lastScore;
	int lastLives;
	int lastWaves;
	String scoreString;
	String raceString;
	FPSCounter fpsCounter;
	float waterSound = 0;
	float gameOverDelay = 3f;
	boolean reversedAxis;
	
	private float aspectRatio;
	private float camWidth = 480;
	private float camHeight = 320;
	
	private float previewTimer = 0;
	private boolean isRecording;
	
	public ScreenGame(Game game, boolean record) {
		super(game);
		isRecording = record;
		
		Display display = ((GLGame)(game)).getWindowManager().getDefaultDisplay(); 		
		aspectRatio = ((float)display.getWidth())/display.getHeight();

		state = GAME_PREVIEW;
		camWidth = aspectRatio * camHeight;
		guiCam = new Camera2D(glGraphics, camWidth, camHeight);
				
		touchPoint = new Vector2();
		batcher = new SpriteBatcher(glGraphics, 100);
		race = new Race(record);
		raceListener = new RaceListener() {

			private Game game;

			@Override
			public void hit() {
				//Assets.playSound(Assets.hitSound);
			}
			
			@Override
			public void setGame(Game inGame) {
				game = inGame;
				//Assets.playSound(Assets.hitSound);
			}
			
			@Override
			public void hurt() {
				//Assets.playSound(Assets.hurtSound);
			}
			
			@Override
			public void raceRecorded() {
				//Assets.playSound(Assets.hurtSound);
				Settings.save(game.getFileIO());
			}
		};
		raceListener.setGame(game);
		
		Settings.load(game.getFileIO());
		
		race.setRaceListener(raceListener);
		renderer = new RaceRenderer(glGraphics);
		pauseBounds = new Rectangle(camWidth - 64, camHeight - 64, 64, 64);
		resumeBounds = new Rectangle(camWidth/2f - 80, 160, 160, 32);
		quitBounds = new Rectangle(camWidth/2f - 80, 160 - 32, 160, 32);
		shotBounds = new Rectangle(camWidth - 64, 0, 64, 64);
		leftBounds = new Rectangle(0, 0, 64, 64);
		rightBounds = new Rectangle(64, 0, 64, 64);
		lastScore = 0;
		lastLives = race.ship.lives;
		lastWaves = race.waves;
		// scoreString = "lives:" + lastLives + " waves:" + lastWaves +
		// " score:" + lastScore;
		scoreString = "score:" + lastScore + "     lives:" + lastLives;
		raceString = " ";
		fpsCounter = new FPSCounter();
		reversedAxis = true;// (Math.abs(game.getInput().getAccelX()) >
							// Math.abs(game.getInput().getAccelY()) );
		race.carPlayer.drive();
		if (!isRecording) race.carRival.drive();
	}

	@Override
	public void update(float deltaTime) {
		switch (state) {
		case GAME_PAUSED:
			updatePaused();
			break;
		case GAME_PREVIEW:
			updatePreview(deltaTime);
			break;
		case GAME_RUNNING:
			updateRunning(deltaTime);
			break;
		case GAME_OVER:
			updateGameOver(deltaTime);
			break;
		}

	}

	private void updatePaused() {
		List<TouchEvent> events = game.getInput().getTouchEvents();
		int len = events.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = events.get(i);
			if (event.type != TouchEvent.TOUCH_UP)
				continue;

			guiCam.touchToWorld(touchPoint.set(event.x, event.y));
			if (OverlapTester.pointInRectangle(resumeBounds, touchPoint)) {
				Assets.playSound(Assets.clickSound);
				state = GAME_RUNNING;
			}

			if (OverlapTester.pointInRectangle(quitBounds, touchPoint)) {
				Assets.playSound(Assets.clickSound);
				game.setScreen(new ScreenMainMenu(game));
			}
		}
	}

	private void updatePreview(float deltaTime) {
		previewTimer += deltaTime;
		if (previewTimer >= 8f) {
			state = GAME_RUNNING;
		}
		
		List<TouchEvent> events = game.getInput().getTouchEvents();
		int len = events.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = events.get(i);
			if (event.type == TouchEvent.TOUCH_DOWN) previewTimer = Float.MAX_VALUE;
		}
	}
	
	private void updateRunning(float deltaTime) {


		race.update(deltaTime, calculateInputAcceleration());
		
		// AutoShift the Rival Car
		//Car carRival = world.carRival;
		//if (!carRival.racerType) {
		//	if (carRival.getTimeToVelocity(carRival.shiftAtVelocity) < -0.8f) {
		//		carRival.shift();
		//	}
		//}
		
		List<TouchEvent> events = game.getInput().getTouchEvents();
		int len = events.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = events.get(i);
			if (event.type != TouchEvent.TOUCH_DOWN)
				continue;

			guiCam.touchToWorld(touchPoint.set(event.x, event.y));

			previewTimer = Float.MAX_VALUE;
			
			if (OverlapTester.pointInRectangle(pauseBounds, touchPoint)) {
				Assets.playSound(Assets.clickSound);
				state = GAME_PAUSED;
			}
			
			if (OverlapTester.pointInRectangle(shotBounds, touchPoint)) {
				race.shot();
				waterSound = 0.5f;
			}
			
			if (!isRecording && race.carRival.getCurrentGearNumber() == 0) race.carRival.shift();
			race.carPlayer.shift();
		}
		if (race.ship.lives != lastLives || race.score != lastScore || race.waves != lastWaves) {
			lastLives = race.ship.lives;
			lastScore = race.score;
			lastWaves = race.waves;
			/*
			 * scoreString = "lives:" + lastLives + " waves:" + lastWaves +
			 * " score:" + lastScore;
			 */
			scoreString = "score:" + lastScore + "     lives:" + lastLives;
		}
		if (race.isGameOver()) {
			state = GAME_OVER;
			// Assets.water.setVolume(0.0f);
			scoreString = "score:" + lastScore;
		}

		/*
		 * if (waterSound > 0) { waterSound -= deltaTime; if (waterSound > 0) {
		 * Assets.water.setVolume(1.0f); }else{ Assets.water.setVolume(0.0f); }
		 * }
		 */
	}

	private float calculateInputAcceleration() {
		float accelX = 0;
		if (Settings.touchEnabled) {
			for (int i = 0; i < 2; i++) {
				if (game.getInput().isTouchDown(i)) {

					guiCam.touchToWorld(touchPoint.set(game.getInput().getTouchX(i), game.getInput().getTouchY(i)));

					if (OverlapTester.pointInRectangle(leftBounds, touchPoint)) {
						accelX = -Ship.SHIP_VELOCITY / 10;
					}

					if (OverlapTester.pointInRectangle(rightBounds, touchPoint)) {
						accelX = Ship.SHIP_VELOCITY / 10;
					}
				}
			}
		} else {
			accelX = (!reversedAxis) ? game.getInput().getAccelY() : -game.getInput().getAccelX();
		}
		return accelX;
	}

	private void updateGameOver(float deltaTime) {
		if (gameOverDelay > 0) {
			gameOverDelay -= deltaTime;
		}

		List<TouchEvent> events = game.getInput().getTouchEvents();
		int len = events.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = events.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				//Assets.water.setVolume(0.0f);
				if (gameOverDelay > 0) {

				} else {
					Assets.playSound(Assets.clickSound);
					game.setScreen(new ScreenMainMenu(game));
				}
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		GL10 gl = glGraphics.getGL();

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		guiCam.setViewportAndMatrices();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		if (state == ScreenGame.GAME_OVER) {
			batcher.beginBatch(Assets.background3);
			batcher.drawSprite(camWidth/2f, 160, camWidth, camHeight, Assets.backgroundRegion3);
		} else {
			//float size = (100f - race.ship.lives) / 100f;
			//batcher.beginBatch(Assets.background);
			//batcher.drawSprite(camWidth/2f, 160, camWidth, camHeight, Assets.backgroundRegion);
			//gl.glEnable(GL10.GL_BLEND);
			//batcher.drawSprite(230, 20, 195f * size, 110f * size, Assets.puddle);
		}

		//batcher.endBatch();
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);

		renderer.render(race, deltaTime, state);
		
		
		switch (state) {
		case GAME_PREVIEW:
			presentPreview();
			break;
		case GAME_RUNNING:
			presentRace();
			presentRunning();
			break;
		case GAME_PAUSED:
			presentRace();
			presentPaused();
			break;
		case GAME_OVER:
			presentGameOver();
		}
		
		fpsCounter.logFrame();
	}

	private void presentRace() {
		guiCam.setViewportAndMatrices();
		float distGaugeWidth = 350f;
		float distGaugeHeight = 5f;
		float gaugeGap = 10f;
		float unitFinishRival;
		float unitFinishPlayer;

		unitFinishRival = (isRecording) ? 0 : race.carRival.position/Gear.FINISH_DISTANCE;
		unitFinishPlayer = race.carPlayer.position/Gear.FINISH_DISTANCE;
		
		unitFinishRival = (unitFinishRival > 1f) ? 1f: unitFinishRival;
		unitFinishPlayer = (unitFinishPlayer > 1f) ? 1f: unitFinishPlayer;

		float gaugeWidthRival = unitFinishRival * distGaugeWidth;
		float gaugeWidthPlayer = unitFinishPlayer * distGaugeWidth;
		
		GL10 gl = glGraphics.getGL();
		gl.glDisable(GL10.GL_TEXTURE_2D);

		gl.glColor4f(0.25f, 0.25f, 0.25f, 0.75f);
		batcher.beginBatch(Assets.items);
		batcher.drawSprite(camWidth/2f,  camHeight - 1f * gaugeGap, 700f, 35f, Assets.pauseRegion);
		batcher.endBatch();
		
		gl.glColor4f(0, 0, 0, 1f);
		batcher.beginBatch(Assets.items);
		batcher.drawSprite(camWidth/2f,  camHeight - 1f * gaugeGap, distGaugeWidth, distGaugeHeight, Assets.pauseRegion);
		batcher.drawSprite(camWidth/2f,  camHeight - 2f * gaugeGap, distGaugeWidth, distGaugeHeight, Assets.pauseRegion);
		batcher.endBatch();

		gl.glColor4f(0.8f, 1.0f, 1.0f, 1f);
		batcher.beginBatch(Assets.items);
		batcher.drawSprite(camWidth/2f - (distGaugeWidth-gaugeWidthRival)/2f,  camHeight - 1f * gaugeGap, gaugeWidthRival, distGaugeHeight, Assets.pauseRegion);
		batcher.drawSprite(camWidth/2f - (distGaugeWidth-gaugeWidthPlayer)/2f, camHeight - 2f * gaugeGap, gaugeWidthPlayer, distGaugeHeight, Assets.pauseRegion);
		batcher.endBatch();
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1f);
	}

	private void presentPaused() {
		GL10 gl = glGraphics.getGL();
		guiCam.setViewportAndMatrices();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		batcher.beginBatch(Assets.items);
		presentNumbers();
		batcher.drawSprite(camWidth/2, camHeight/2, 160, 64, Assets.pauseRegion);
		batcher.endBatch();

		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
		// Assets.water.setVolume(0.0f);
	}


	private void presentPreview() {
		
	}
	
	private void presentRunning() {
		GL10 gl = glGraphics.getGL();
		guiCam.setViewportAndMatrices();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		batcher.beginBatch(Assets.items);
		batcher.drawSprite(camWidth - 32, camHeight - 32, 64, 64, Assets.pauseButtonRegion);
		presentNumbers();
		if (Settings.touchEnabled) {
			batcher.drawSprite(32, 32, 64, 64, Assets.leftRegion);
			batcher.drawSprite(96, 32, 64, 64, Assets.rightRegion);
		}
		batcher.drawSprite(camWidth - 40, 32, 64, 64, Assets.fireRegion);
		batcher.endBatch();

		batcher.beginBatch(Assets.hudDial);
		batcher.drawSprite(camWidth - camWidth/2, 16, 128, 128, Assets.hudRegionDial);
		batcher.endBatch();

		float angle = race.carPlayer.getCurrentVelocity() / race.carPlayer.shiftAtVelocity;
		angle = 131f - 43f - (131f + 45f) * angle * 6f / 7f;
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		gl.glColor4f(1.0f, 0.4f, 0f, 1f);
		batcher.beginBatch(Assets.hudNeedle);
		batcher.drawSprite(camWidth - camWidth/2, 16, 105, 105, angle, Assets.hudRegionNeedle);
		batcher.endBatch();
		gl.glColor4f(1f, 1f, 1f, 1f);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
	}

	private void presentNumbers() {
		float yOffset = camHeight - 20;
		
		if (!isRecording) {
			Assets.font.drawText(batcher,  race.carRival.getCurrentGearNumber() + " " +  Math.round(race.carRival.velocity  * 100f) / 100f + " / " + Math.round(race.carRival.shiftAtVelocity  * 100f) / 100f, 10.0f, yOffset - 20, 0.5f);
			Assets.font.drawText(batcher, Math.round(race.carRival.position  * 100f) / 100f + "", 140.0f, yOffset - 20, 0.5f);
			Assets.font.drawText(batcher, Math.round(race.carRival.timeToFinishLine  * 100f) / 100f + "", 190.0f, yOffset - 20, 0.5f);
			Assets.font.drawText(batcher, Math.round(race.carRival.getFinishTime()  * 1000000f) / 1000000f + "", 231, yOffset - 20, 0.5f);
			Assets.font.drawText(batcher, Math.round(race.carRival.getTimeToVelocity(race.carRival.shiftAtVelocity)  * 1000f) / 1000f + "", camHeight, yOffset - 20, 0.5f);
		}
		
		Assets.font.drawText(batcher,  race.carPlayer.getCurrentGearNumber() + " " +  Math.round(race.carPlayer.velocity * 100f) / 100f + " / " + Math.round(race.carPlayer.shiftAtVelocity * 100f) / 100f, 10.0f, yOffset - 40, 0.5f);		
		Assets.font.drawText(batcher, Math.round(race.carPlayer.position * 100f) / 100f + "", 140.0f, yOffset - 40, 0.5f);
		Assets.font.drawText(batcher, Math.round(race.carPlayer.timeToFinishLine * 100f) / 100f + "", 190.0f, yOffset - 40, 0.5f);
		Assets.font.drawText(batcher, Math.round(race.carPlayer.getFinishTime() * 1000000f) / 1000000f + "", 231, yOffset - 40, 0.5f);
		Assets.font.drawText(batcher, Math.round(race.carPlayer.getTimeToVelocity(race.carPlayer.shiftAtVelocity)  * 1000f) / 1000f + "", camHeight, yOffset - 40, 0.5f);
		
	}
	
	private void presentGameOver() {
		// Assets.water.setVolume(0.0f);
		GL10 gl = glGraphics.getGL();
		guiCam.setViewportAndMatrices();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		batcher.beginBatch(Assets.items);
		batcher.drawSprite(128 / 2 + 30, camHeight, 128, 64, Assets.gameOverRegion);
		batcher.drawSprite(camWidth / 2, 45, 194, 64, Assets.quote);
		Assets.font.drawText(batcher, scoreString, 10, camHeight - 20);
		batcher.endBatch();

		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
	}

	@Override
	public void pause() {
		state = GAME_PAUSED;
	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}
}
