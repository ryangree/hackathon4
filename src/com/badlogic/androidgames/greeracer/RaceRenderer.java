package com.badlogic.androidgames.greeracer;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.util.FloatMath;

import com.badlogic.androidgames.framework.gl.AmbientLight;
import com.badlogic.androidgames.framework.gl.DirectionalLight;
import com.badlogic.androidgames.framework.gl.LookAtCamera;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.impl.GLGraphics;

public class RaceRenderer {
	GLGraphics glGraphics;
	LookAtCamera cameraPlayer;
	LookAtCamera cameraFinishline;
	AmbientLight ambientLight;
	DirectionalLight directionalLight;
	SpriteBatcher batcher;
	float invaderAngle = 0;

	float averageAccelX;
	
	private final float FOV_HEIGHT = 45f;

	private final float WHEEL_RADIUS = 0.658f;
	
	private final int GAME_PREVIEW = 0;
	private float previewTime = 0;
	List<Float> accelX = new ArrayList<Float>();

	public RaceRenderer(GLGraphics glGraphics) {
		this.glGraphics = glGraphics;
		cameraPlayer = new LookAtCamera(FOV_HEIGHT, glGraphics.getWidth() / (float) glGraphics.getHeight(), 0.1f, 1000);
		cameraPlayer.getPosition().set(0, 6f, 12f);
		cameraPlayer.getLookAt().set(0, 0, -6);

		ambientLight = new AmbientLight();
		ambientLight.setColor(0.2f, 0.2f, 0.2f, 1.0f);
		directionalLight = new DirectionalLight();
		directionalLight.setDirection(-1, -0.5f, 0);
		batcher = new SpriteBatcher(glGraphics, 10);
	}

	public void render(Race world, float deltaTime, int state) {
		GL10 gl = glGraphics.getGL();
		// camera.getPosition().x = world.ship.position.x;
		// camera.getLookAt().x = world.ship.position.x;

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_BLEND);
		gl.glCullFace(GL10.GL_BACK);
		gl.glFrontFace(GL10.GL_CCW);

		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		// ambientLight.enable(gl);
		directionalLight.enable(gl, GL10.GL_LIGHT0);
		if (state == GAME_PREVIEW) {
			setCameraPositionPreview(gl, world, deltaTime);		
		} else {
			setCameraPositionRunning(gl, world);			
		}
		renderRoad(gl, world);
		renderCars(gl, world);
		renderWheels(gl, world);
		gl.glDisable(GL10.GL_COLOR_MATERIAL);
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_DEPTH_TEST);

	}

	private void setCameraPositionPreview(GL10 gl, Race world, float deltaTime) {
		previewTime += deltaTime;
		cameraPlayer.getPosition().set(-12f, 4f,   0f + previewTime);
		cameraPlayer.getLookAt().set(	 0f, 0f, -12f + previewTime);
		cameraPlayer.setMatrices(gl);
		
	}
	
	private void setCameraPositionRunning(GL10 gl, Race world) {
		float lead = (world.carRival == null) ? -5f : (world.carPlayer.position - world.carRival.position);
		float camX = 12f * lead;
		float camY = 12f;
		float camAngle;
		Car carPlayer = world.carPlayer;
		
		float angleMax =  (float) (50f / 180f * Math.PI);
		float angleMult =  1f/25f;
		camAngle = (float) (2f * angleMax / (1f + Math.exp(2f * camX / angleMax * angleMult)) - (2f * angleMax) / 2f + Math.PI / 2f);
		
		camX = 12f * FloatMath.cos(camAngle);
		camY = 12f * FloatMath.sin(camAngle);
		
		float shake = carPlayer.getCurrentVelocity() / 400f;
		float shakeX = shake * FloatMath.cos(carPlayer.position * 10f + 0f / 3f * 3.14f);
		float shakeY = shake * FloatMath.cos(carPlayer.position * 10f + 2f / 3f * 3.14f);
		float shakeZ = shake * FloatMath.cos(carPlayer.position * 10f + 4f / 3f * 3.14f);
		
		if (Gear.FINISH_DISTANCE - carPlayer.position > 2f) {
			cameraPlayer.getPosition().set(	carPlayer.position * 10f - 4.5f + camX + shakeX, 	5.0f + shakeY,		shakeZ + camY);		
			cameraPlayer.getLookAt().set(	carPlayer.position * 10f - 4.5f + shakeX, 			1.0f + shakeY, 	shakeZ + 0);	
		}else{
			camX = (Gear.FINISH_DISTANCE - world.carPlayer.position);
			camY = 12f;
			cameraPlayer.getPosition().set(	Gear.FINISH_DISTANCE * 10f, 6f, camY);
			cameraPlayer.getLookAt().set(	Gear.FINISH_DISTANCE * 10f - (float) (16f * Math.atan(Math.PI * camX * 10f / 16f) / Math.PI), 2f, 0);
		}
		cameraPlayer.setMatrices(gl);
		
	}
	
	private void renderCars(GL10 gl, Race world) {
		float finishDistanceRival = (world.carRival == null) ? 0 : (world.carRival.position - Gear.FINISH_DISTANCE) * 2f;
		float finishDistancePlayer = (world.carPlayer.position - Gear.FINISH_DISTANCE) * 2f;

		Assets.modelCar.bind();
		
		if (world.carRival != null) {
			gl.glPushMatrix();
			gl.glTranslatef(world.carRival.position * 10f, 0f, -6.5f);
			Assets.textureCarBlue.bind();
			Assets.modelCar.draw(GL10.GL_TRIANGLES, 0, Assets.modelCar.getNumVertices());
			if (finishDistanceRival > 0f && finishDistanceRival < 1f) {
				gl.glDisable(GL10.GL_TEXTURE_2D);
					gl.glDisable(GL10.GL_LIGHTING);
						gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
							gl.glColor4f(1f, 1f, 1f, 1f-finishDistanceRival);
								gl.glDisable(GL10.GL_DEPTH_TEST);
									Assets.modelCar.draw(GL10.GL_TRIANGLES, 0, Assets.modelCar.getNumVertices());
								gl.glEnable(GL10.GL_DEPTH_TEST);
							gl.glColor4f(1f, 1f, 1f, 1f);
						gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
					gl.glEnable(GL10.GL_LIGHTING);
				gl.glEnable(GL10.GL_TEXTURE_2D);
			}
			gl.glPopMatrix();			
		}

		gl.glPushMatrix();
		gl.glTranslatef(world.carPlayer.position * 10f, 0f, 0f);
		Assets.textureCarRed.bind();
		Assets.modelCar.draw(GL10.GL_TRIANGLES, 0, Assets.modelCar.getNumVertices());
		if (finishDistancePlayer > 0f && finishDistancePlayer < 1f) {
			gl.glDisable(GL10.GL_TEXTURE_2D);
				gl.glDisable(GL10.GL_LIGHTING);
					gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
						gl.glColor4f(1f, 1f, 1f, 1f-finishDistancePlayer);
							gl.glDisable(GL10.GL_DEPTH_TEST);
								Assets.modelCar.draw(GL10.GL_TRIANGLES, 0, Assets.modelCar.getNumVertices());
							gl.glEnable(GL10.GL_DEPTH_TEST);
						gl.glColor4f(1f, 1f, 1f, 1f);
					gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				gl.glEnable(GL10.GL_LIGHTING);
			gl.glEnable(GL10.GL_TEXTURE_2D);
		}
		gl.glPopMatrix();


		Assets.modelCar.unbind();
	}

	private void renderWheels(GL10 gl, Race world) {
		float circumference = (float) (WHEEL_RADIUS * 2 * Math.PI);
		Assets.modelWheel.bind();

		Assets.textureWheel.bind();
		
		float distanceTraveled;	
		
		distanceTraveled = world.carPlayer.position * 10f;	
		
		gl.glPushMatrix();
		gl.glTranslatef(-2.05f + distanceTraveled, WHEEL_RADIUS, 1.8f);
		gl.glRotatef(-(distanceTraveled % circumference) / circumference * 360f, 0f, 0f, 1f);
		Assets.modelWheel.draw(GL10.GL_TRIANGLES, 0, Assets.modelWheel.getNumVertices());
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(-7.27f + distanceTraveled, WHEEL_RADIUS, 1.8f);
		gl.glRotatef(-(distanceTraveled % circumference) / circumference * 360f, 0f, 0f, 1f);
		Assets.modelWheel.draw(GL10.GL_TRIANGLES, 0, Assets.modelWheel.getNumVertices());
		gl.glPopMatrix();
		
		if (world.carRival != null) {
			distanceTraveled = world.carRival.position * 10f;	
			
			gl.glPushMatrix();
			gl.glTranslatef(-2.05f + distanceTraveled, WHEEL_RADIUS, 1.8f - 6.5f);
			gl.glRotatef(-(distanceTraveled % circumference) / circumference * 360f, 0f, 0f, 1f);
			Assets.modelWheel.draw(GL10.GL_TRIANGLES, 0, Assets.modelWheel.getNumVertices());
			gl.glPopMatrix();

			gl.glPushMatrix();
			gl.glTranslatef(-7.27f + distanceTraveled, WHEEL_RADIUS, 1.8f - 6.5f);
			gl.glRotatef(-(distanceTraveled % circumference) / circumference * 360f, 0f, 0f, 1f);
			Assets.modelWheel.draw(GL10.GL_TRIANGLES, 0, Assets.modelWheel.getNumVertices());
			gl.glPopMatrix();	
		}
		
		Assets.modelWheel.unbind();
	}
	
	private void renderRoad(GL10 gl, Race world) {
		
		Assets.modelRoad.bind();
		Assets.textureRoad.bind();
		gl.glPushMatrix();
		gl.glTranslatef(Math.round(world.carPlayer.position/10f) * 10f * 10f, 0f,0f);
		gl.glTranslatef(-300f, 0f,0f);
		for (int i = 0; i < 6; i++) {
			Assets.modelRoad.draw(GL10.GL_TRIANGLES, 0, Assets.modelRoad.getNumVertices());
			gl.glTranslatef(100f, 0f,0f);
		}
		gl.glPopMatrix();		
		Assets.modelRoad.unbind();

		gl.glDisable(GL10.GL_DEPTH_TEST);
		
		gl.glPushMatrix();
		gl.glTranslatef(Gear.FINISH_DISTANCE * 10f, 0.04f, 0f);
		Assets.modelFinish.bind();
		Assets.modelFinish.draw(GL10.GL_TRIANGLES, 0, Assets.modelFinish.getNumVertices());
		Assets.modelFinish.unbind();
		gl.glPopMatrix();		
		
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		
	}
}