package com.badlogic.androidgames.greeracer;

import android.util.FloatMath;

public class Gear {

	private float left;
	private float peak;
	private float width;

	// private float startPosition;
	// private float startVelocity;

	private float kVelocity;
	private float kPosition;

	public double gearTime;
	public int gearNumber;

	public float velocityPerfectShift;
	public float velocityFinish;
	
	public double timeFromStartLine;
	public double timeToFinishLine;
	public double timeFinished = -1;
	private float timeSinceFinish = 0f;
	
	public boolean isFinished = false;
	
	public static final float FINISH_BRAKE_FORCE = -2f;
	public static final float FINISH_DISTANCE = 100f;
	
	public Gear(int gearNumber, float left, float width, float peak) {
		this.left = left;
		this.width = width;
		this.peak = peak;
		this.gearNumber = gearNumber;
	}

	public void shiftIn(double timeFromStartLine, float startPosition, float startVelocity) {
		float startTime = 0f;
		this.timeFromStartLine = timeFromStartLine; 
		kVelocity = (float) Math.exp(4 * peak * startTime / width) * (left - startVelocity + width) / (left - startVelocity);
		kPosition = startPosition - left * startTime - width * width * (float) Math.log(Math.exp(4 * peak * startTime / width) - kVelocity) / (4 * peak);
		gearTime = 0;
		timeToFinishLine = getTimeToFinishLine();
	}

	public void update(double deltaTime) {
		if (isFinished) {
			updateAfterFinishLine(deltaTime);
			return;
		}
		
		if (deltaTime >= timeToFinishLine && timeToFinishLine != -1) {
			double excessTime = deltaTime - timeToFinishLine;
			gearTime += timeToFinishLine;
			velocityFinish = getCurrentVelocity();
			timeToFinishLine = 0;
			timeFinished = timeFromStartLine + gearTime;
			isFinished = true;
			doFinish();
			updateAfterFinishLine(excessTime);
		}else{
			timeToFinishLine -= deltaTime;
			gearTime += deltaTime;
		}
	}
	
	private void doFinish() {
		
	}
	
	private void updateAfterFinishLine(double deltaTime) {
		float timeToStop = (0f - velocityFinish) / FINISH_BRAKE_FORCE;		
		timeSinceFinish += deltaTime;
		if (timeSinceFinish > timeToStop) {
			timeSinceFinish = timeToStop;
		}
	}
	
	public float getCurrentVelocity() {
		if (!isFinished) {
			return left + width / (1 - kVelocity * (float) Math.exp(-4 * peak * gearTime / width));		
		}else{
			return velocityFinish + FINISH_BRAKE_FORCE * timeSinceFinish;						
		}
	}

	public double getTimeToVelocity(float velocity) {
		if (velocity < 0) return -1.0;
		return  -width * (float) Math.log((width / (left - velocity) + 1) / kVelocity) / (4 * peak) - gearTime;
	}
	
	public float setVelocityPerfectShift(Gear nextGear) {
		if (nextGear == null) {
			this.velocityPerfectShift = -1.0f;
		} else {
			this.velocityPerfectShift = nextGear.getVelocityPerfectShift(peak, left, width);
			//Log.w(this.toString(), "perfect shift at: " + velocityPerfectShift);			
		}
		return velocityPerfectShift;
		// (b*h*sqrt(a^2*h^2+4*a*b*c*g-2*a*b*g*h-4*a*b*g*k+4*a*c^2*g-4*a*c*g*h-8*a*c*g*k+4*a*g*h*k+4*a*g*k^2+b^2*g^2)-a*b*h^2-2*a*c*h^2+b^2*g*h+2*b^2*g*k)/(2*(b^2*g-a*h^2))
	}

	public float getVelocityPerfectShift(float inPeak, float inLeft, float inWidth) {
		float a = inPeak;
		float b = inWidth;
		float c = inLeft;
		float g = peak;
		float h = width;
		float k = left;
		return (b * h * FloatMath.sqrt(4 * a * g * k * k + (4 * a * g * h + (-8 * a * c - 4 * a * b) * g) * k + a * a * h * h + (-4 * a * c - 2 * a * b) * g * h + b * b * g * g + (4 * a * c * c + 4 * a * b * c) * g) - 2 * b * b * g * k + (2 * a * c + a * b) * h * h - b * b * g * h) / (2 * a * h * h - 2 * b * b * g);
	}

	public float getCurrentPosition() {
		if (!isFinished) {
			return (float) (left * gearTime + (width * width * (float) Math.log(Math.exp(4 * peak * gearTime / width) - kVelocity)) / (4 * peak) + kPosition);
		}else{
			return FINISH_DISTANCE + velocityFinish * timeSinceFinish + 0.5f * FINISH_BRAKE_FORCE * timeSinceFinish * timeSinceFinish;
		}
	}

	private double getTimeToFinishLine() {

		double projectedPosition = 0;

		double now = gearTime;
		double lower = 0;
		double upper = 300;
		double diff = 0;
		
		while (true) {
			projectedPosition = left * (now + (upper + lower) / 2d) + (width * width * Math.log(Math.exp(4 * peak * (now + (upper + lower) / 2d) / width) - kVelocity)) / (4 * peak) + kPosition;
			if (projectedPosition > FINISH_DISTANCE) {
				upper = lower + (upper - lower) / 2d;
			} else {
				lower = (upper + lower) / 2d;
			}
			diff = upper - lower;
			if (diff <= 5.0e-9) {
				return ((upper + lower) / 2d);
			}
		}
	}
}
