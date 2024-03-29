package com.blogic.androidgames.greeracer;

import com.blogic.androidgames.framework.Music;
import com.blogic.androidgames.framework.Sound;
import com.blogic.androidgames.framework.gl.Font;
import com.blogic.androidgames.framework.gl.ObjLoader;
import com.blogic.androidgames.framework.gl.Texture;
import com.blogic.androidgames.framework.gl.TextureRegion;
import com.blogic.androidgames.framework.gl.Vertices3;
import com.blogic.androidgames.framework.impl.GLGame;

public class Assets {
	//public static Texture background;
	public static Texture backgroundMenu;
	//public static Texture background3;
	public static Texture items;
	public static Texture hudDial;
	public static Texture hudNeedle;
	public static TextureRegion hudRegionDial;
	public static TextureRegion hudRegionNeedle;
	
	public static TextureRegion puddle;
	public static TextureRegion backgroundRegion;
	public static TextureRegion backgroundRegion2;
	public static TextureRegion backgroundRegion3;
	public static TextureRegion logoRegion;
	public static TextureRegion menuRegion;
	public static TextureRegion gameOverRegion;
	public static TextureRegion pauseRegion;
	public static TextureRegion settingsRegion;
	public static TextureRegion touchRegion;
	public static TextureRegion accelRegion;
	public static TextureRegion touchEnabledRegion;
	public static TextureRegion accelEnabledRegion;
	public static TextureRegion soundRegion;
	public static TextureRegion soundEnabledRegion;
	public static TextureRegion leftRegion;
	public static TextureRegion rightRegion;
	public static TextureRegion fireRegion;
	public static TextureRegion pauseButtonRegion;
	public static TextureRegion quote;
	public static Font font;

	//public static Texture explosionTexture;
	//public static Animation explosionAnim;
	
	public static Vertices3 modelGuy;
	//public static Vertices3 modelInvader;
	public static Vertices3 modelCar;
	//public static Vertices3 modelUrinal;
	public static Vertices3 modelShot;
	public static Vertices3 modelRoad;
	public static Vertices3 modelFinish;
	public static Vertices3 modelWheel;
	
	public static Texture textureShip;	
	
	public static Texture textureCarRed;
	public static Texture textureCarBlue;
	public static Texture textureWheel;
	public static Texture textureRoad;
	
	
	
	
	
	//public static Vertices3 shieldModel;

	
	
	
	//public static Music music;
	//public static Music water;
	public static Sound clickSound;
	//public static Sound hitSound;
	//public static Sound hurtSound;
	//public static Sound shotSound;
	//public static Sound missSound;

	public static void load(GLGame game) {
		//background = new Texture(game, "bathroom.png", true);
		backgroundMenu = new Texture(game, "bathroom_outside.jpg", true);
		//background3 = new Texture(game, "bathroom_gg.jpg", true);
		
		//backgroundRegion = new TextureRegion(background, 0, 0, 512, 314);
		backgroundRegion2 = new TextureRegion(backgroundMenu, 0, 0, 512, 314);
		//backgroundRegion3 = new TextureRegion(background3, 0, 0, 512, 314);

		//puddle = new TextureRegion(background, 0, 316, 195, 110);
		
		items = new Texture(game, "items4.png", true);
		hudDial = new Texture(game, "hud_dial2.png", true);
		hudNeedle = new Texture(game, "hud_needle2.png", true);
		hudRegionDial = new TextureRegion(hudDial, 0, 0, 512, 512);
		hudRegionNeedle = new TextureRegion(hudNeedle, 0, 0, 512, 512);
		
		
		logoRegion = new TextureRegion(items, 0, 256, 252, 126);
		menuRegion = new TextureRegion(items, 0, 128, 224, 64);
		gameOverRegion = new TextureRegion(items, 224, 128, 128, 64);
		pauseRegion = new TextureRegion(items, 0, 192, 160, 64);
		settingsRegion = new TextureRegion(items, 0, 160, 224, 32);
		
		touchRegion = new TextureRegion(items, 0, 384, 64, 64);
		touchEnabledRegion = new TextureRegion(items, 0, 448, 64, 64);
		
		accelRegion = new TextureRegion(items, 64, 384, 64, 64);
		accelEnabledRegion = new TextureRegion(items, 64, 448, 64, 64);
		
		soundRegion = new TextureRegion(items, 128, 384, 64, 64);
		soundEnabledRegion = new TextureRegion(items, 192, 384, 64, 64);
		
		leftRegion = new TextureRegion(items, 0, 0, 64, 64);
		rightRegion = new TextureRegion(items, 64, 0, 64, 64);
		
		fireRegion = new TextureRegion(items, 128, 0, 64, 64);
		pauseButtonRegion = new TextureRegion(items, 0, 64, 64, 64);
		quote = new TextureRegion(items, 318, 448, 194, 64);
		
		font = new Font(items, 224, 0, 16, 16, 20);
		//explosionTexture = new Texture(game, "explode.png", true);
		/*TextureRegion[] keyFrames = new TextureRegion[16];
		int frame = 0;
		for (int y = 0; y < 256; y += 64) {
			for (int x = 0; x < 256; x += 64) {
				keyFrames[frame++] = new TextureRegion(explosionTexture, x, y,
						64, 64);
			}
		}
		explosionAnim = new Animation(0.1f, keyFrames);*/

		textureShip = new Texture(game, "guy.png", true);
		textureWheel = new Texture(game, "wheel.png", true);
		textureRoad = new Texture(game, "road.png", false);
		
		modelGuy = ObjLoader.load(game, "guy.obj");
		modelCar = ObjLoader.load(game, "car.obj");
		modelRoad = ObjLoader.load(game, "road2.obj");
		modelFinish = ObjLoader.load(game, "finish2.obj");
		modelWheel = ObjLoader.load(game, "wheel.obj");
		
		
		textureCarRed = new Texture(game, "eclipse2003-diffuse-red.jpg", true);
		textureCarBlue = new Texture(game, "eclipse2003-diffuse-blue.jpg", true);
		//invaderTextureNormal = new Texture(game, "eclipse2003-normalmap.png", true);
		//carTextureDiffuseRed = new Texture(game, "gray.png", true);
		//invaderTexture = new Texture(game, "urinal.png", true);
		//invaderTexture = new Texture(game, "gray.png", true);
		//modelInvader = ObjLoader.load(game, "invader.obj");
		
		//modelUrinal = ObjLoader.load(game, "urinal.obj");
		//urinalModel = ObjLoader.load(game, "koenigsegg.obj");
		
		//shieldModel = ObjLoader.load(game, "shield.obj");
		modelShot = ObjLoader.load(game, "shot.obj");

//		music = game.getAudio().newMusic("porno.mp3");
//		music.setLooping(true);
//		music.setVolume(0.5f);
//		if (Settings.soundEnabled)
//			music.play();
		
//		water = game.getAudio().newMusic("water-continuous.wav");
//		water.setLooping(true);
//		water.setVolume(0.0f);
//		water.play();
		
		clickSound = game.getAudio().newSound("click.ogg");
//		hitSound = game.getAudio().newSound("coin.wav");
//		hurtSound = game.getAudio().newSound("ah.wav");
//		missSound = game.getAudio().newSound("buzzer.wav");
	}

	public static void reload() {
		//background.reload();
		backgroundMenu.reload();
		//background3.reload();
		items.reload();
		//explosionTexture.reload();
		textureShip.reload();
		textureWheel.reload();
		textureRoad.reload();
		textureCarRed.reload();
		textureCarBlue.reload();
		hudDial.reload();
		hudNeedle.reload();
		
//		if (Settings.soundEnabled){
//			//music.play();
//			water.play();
//			water.setVolume(0.0f);
//		}
	}

	public static void playSound(Sound sound) {
		if (Settings.soundEnabled)
			sound.play(1);
	}
}
