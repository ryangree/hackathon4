package com.blogic.androidgames.greeracer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.blogic.androidgames.framework.Screen;
import com.blogic.androidgames.framework.impl.GLGame;
import com.blogic.androidgames.greeracer.screens.ScreenMainMenu;

public class GreeRacer extends GLGame {
	boolean firstTimeCreate = true;

	@Override
	public Screen getStartScreen() {
		return new ScreenMainMenu(this);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
		if (firstTimeCreate) {
			Settings.load(getFileIO());
			Assets.load(this);
			firstTimeCreate = false;
		} else {
			Assets.reload();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		//if (Settings.soundEnabled) Assets.music.pause();
	}
}