package com.badlogic.androidgames.greeracer;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Car {

	private Gear gear1 = new Gear(1, -0.6f, 05.05f, 2.00f);
	private Gear gear2 = new Gear(2, -0.5f, 06.45f, 1.30f);
	private Gear gear3 = new Gear(3, -0.4f, 08.23f, 0.85f);
	private Gear gear4 = new Gear(4, -0.3f, 10.51f, 0.55f);
	private Gear gear5 = new Gear(5, -0.2f, 13.43f, 0.36f);
	private Gear gear6 = new Gear(6, -0.1f, 17.14f, 0.23f);

	private Gear gearCurrent;

	
	public final static int RACER_NORMAL = 0;
	public final static int RACER_RIVAL = 1;
	public final static int RACER_CPU = 2;
	public final static int RACER_CPUGOD = 3;

	private List<Double> shiftTimesRival;
	public List<Double> shiftTimesRecord;
	private double rivalShiftTimeCurrent;
	
	private List<Gear> gears = new ArrayList<Gear>();
	
	public float position = 0;
	public float velocity = 0;
	public float shiftAtVelocity = 0;
	public double timeToFinishLine = -1;
	private double timeSinceStart = 0;
	
	private boolean driving = false;
	public int racerType = 0;
	
	private float cpuErrorShiftTime;
	
	public Car() {
		makeGears();
	}
	
	public Car(final int cpuType) {
		racerType = cpuType;
		makeGears();
	}
	
	public Car(final List<Double> shiftTimes) {
		racerType = RACER_RIVAL;
		if (shiftTimes.size() < 1) {
			throw new Error("Car: shiftTimes Empty");
		}
		shiftTimesRival = shiftTimes;
		rivalShiftTimeCurrent = getNextRivalShiftTime();
		makeGears();
	}
	
	private void makeGears() {
		shiftTimesRecord = new ArrayList<Double>();
		gears.add(gear1);
		gears.add(gear2);
		gears.add(gear3);
		gears.add(gear4);
		gears.add(gear5);
		gears.add(gear6);
		
	}
	
	private void getNextGear() {
		Gear gearOld = gearCurrent;
		if (gears != null && gears.size() > 0) {
			gearCurrent = gears.remove(0);
			Gear gearNext = null;
			if (gears != null && gears.size() > 0) {
				gearNext = gears.get(0);
			}
			if (gearOld == null) {
				gearCurrent.shiftIn(0, 0, 0);
			} else {
				gearCurrent.shiftIn(gearOld.timeFromStartLine + gearOld.gearTime, gearOld.getCurrentPosition(), gearOld.getCurrentVelocity());
			}
			shiftAtVelocity = gearCurrent.setVelocityPerfectShift(gearNext);
		}
	}
	
	public void drive() {
		if (driving) return;
		driving = true;
		getNextGear();
	}
	
	public void shift() {
		
		timeSinceStart = gearCurrent.gearTime;
		shiftTimesRecord.add(timeSinceStart);
		
		if (racerType == Car.RACER_CPU) {
			cpuErrorShiftTime = (float) (Math.random() - 0.5f) * 3f;
		}
		
		if (this.racerType == Car.RACER_RIVAL) {
			Log.i("Car.shift()",  timeSinceStart + "");
		}
		
		if (gearCurrent != null && !gearCurrent.isFinished) {
			getNextGear();			
		}
	}
	
	private double getNextRivalShiftTime() {
		if (shiftTimesRival ==  null) {
			return 0;
		}else if (shiftTimesRival.size() > 1) {
			return shiftTimesRival.remove(0);
		}
		return 0;
	}
	
	public void update(float timeDelta) {
		if (gearCurrent == null) return;
		
		if (racerType == RACER_CPUGOD || racerType == RACER_CPU) {
			double timeLeftToShift;
			timeLeftToShift = gearCurrent.getTimeToVelocity(gearCurrent.velocityPerfectShift); //Perfect GodLike
			if (racerType == RACER_CPU) {
				timeLeftToShift += cpuErrorShiftTime; //Imperfect for Regular CPU
			}
			if (timeDelta > timeLeftToShift && timeLeftToShift > 0) {
				double excessTime = timeDelta - timeLeftToShift;
				gearCurrent.update(timeLeftToShift);	
				shift();
				gearCurrent.update(excessTime);	
			} else {
				gearCurrent.update(timeDelta);			
			}
		} else if (racerType == RACER_RIVAL) {
			if (gearCurrent.gearTime + timeDelta > rivalShiftTimeCurrent && rivalShiftTimeCurrent > 0) {
				double excessTime = gearCurrent.gearTime + timeDelta - rivalShiftTimeCurrent;
				gearCurrent.update(rivalShiftTimeCurrent - gearCurrent.gearTime);	
				shift();
				gearCurrent.update(excessTime);	
				rivalShiftTimeCurrent = getNextRivalShiftTime();
			} else {
				gearCurrent.update(timeDelta);			
			}
		} else{
			gearCurrent.update(timeDelta);			
		}
		
		position = gearCurrent.getCurrentPosition();
		velocity = gearCurrent.getCurrentVelocity();
		timeToFinishLine = gearCurrent.timeToFinishLine;
		//Log.w(this.toString(), gearCurrent.velocityPerfectShift + " " + velocity + " " + position);
	}
	
	public float getCurrentVelocity() {
		if (gearCurrent == null) return 0;
		return gearCurrent.getCurrentVelocity();
	}
	
	public double getTimeToVelocity(float velocity) {
		if (gearCurrent == null) {
			return -1;
		}
		return gearCurrent.getTimeToVelocity(velocity);
	}
	
	public int getCurrentGearNumber() {
		if (gearCurrent == null) return 0;
		return gearCurrent.gearNumber;
	}
	
	public double getFinishTime() {
		if (gearCurrent == null) {
			return -1;
		}
		return gearCurrent.timeFinished;
	}
}
