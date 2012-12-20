package com.blogic.androidgames.greeracer.screens;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.blogic.androidgames.framework.Game;
import com.blogic.androidgames.framework.Input.TouchEvent;
import com.blogic.androidgames.framework.gl.Camera2D;
import com.blogic.androidgames.framework.gl.SpriteBatcher;
import com.blogic.androidgames.framework.impl.GLScreen;
import com.blogic.androidgames.framework.math.OverlapTester;
import com.blogic.androidgames.framework.math.Rectangle;
import com.blogic.androidgames.framework.math.Vector2;
import com.blogic.androidgames.greeracer.Assets;

public class ScreenMainMenu extends GLScreen {
	Camera2D guiCam;
	SpriteBatcher batcher;
	Vector2 touchPoint;
	Rectangle playBounds;
	Rectangle settingsBounds;

	public ScreenMainMenu(Game game) {
		super(game);
		guiCam = new Camera2D(glGraphics, 480, 320);
		batcher = new SpriteBatcher(glGraphics, 10);
		touchPoint = new Vector2();
		playBounds 		= new Rectangle(0, 80 - 00, 224, 32);
		settingsBounds 	= new Rectangle(0, 80 - 32, 224, 32);
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> events = game.getInput().getTouchEvents();
		int len = events.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = events.get(i);
			if (event.type != TouchEvent.TOUCH_UP)
				continue;

			guiCam.touchToWorld(touchPoint.set(event.x, event.y));
			if (OverlapTester.pointInRectangle(playBounds, touchPoint)) {
				Assets.playSound(Assets.clickSound);
				game.setScreen(new ScreenGame(game, true));
			}
			if (OverlapTester.pointInRectangle(settingsBounds, touchPoint)) {
				Assets.playSound(Assets.clickSound);
				game.setScreen(new ScreenGame(game, false));
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		guiCam.setViewportAndMatrices();

		gl.glEnable(GL10.GL_TEXTURE_2D);

		batcher.beginBatch(Assets.backgroundMenu);
		batcher.drawSprite(240, 160, 480, 320, Assets.backgroundRegion2);
		batcher.endBatch();

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		batcher.beginBatch(Assets.items);
		batcher.drawSprite(252 / 2, 200, 252, 128, Assets.logoRegion);
		batcher.drawSprite(224 / 2 + 15, 80, 224, 64, Assets.menuRegion);
		batcher.endBatch();

		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}
