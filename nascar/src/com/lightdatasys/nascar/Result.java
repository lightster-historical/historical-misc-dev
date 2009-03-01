package com.lightdatasys.nascar;

import java.util.AbstractMap;
import java.util.Comparator;

public class Result
{
	private Race race;
	private Driver driver;
	private String car;
	private int start;
	private int finish;
	private int finishLast;
	private int finishChange;
	private int currentLap;
	private int lapsLed;
	private boolean ledLaps;
	private boolean ledMostLaps;
	private int penalties;
	private float behindLeader;
	private int lapsDown;
	private double lastLapSpeed;
	private float speed;
	private double throttle;
	private double brake;
	private boolean isCurrent;
	
	private int lastUpdatedLap;
	
	private short rowNumber;
	
	
	public Result(Race race, Driver driver, String car, int start, int finish, boolean ledLaps, boolean ledMostLaps, int penalties)
	{		
		this.race = race;
		this.driver = driver;
		this.car = car;
		this.start = start;
		this.finish = finish;
		this.finishLast = finish;
		this.finishChange = 0;
		this.currentLap = -1;
		this.lapsLed = 0;
		this.ledLaps = ledLaps;
		this.ledMostLaps = ledMostLaps;
		this.penalties = penalties;
		this.behindLeader = 0.0f;
		this.lapsDown = 0;
		this.speed = 0.0f;
		this.throttle = 0;
		this.brake = 0;
		this.lastLapSpeed = 0.0d;
		this.isCurrent = false;
		
		lastUpdatedLap = -1;
		
		this.rowNumber = 0;
	}
	
	
	public Race getRace()
	{
		return race;
	}
	
	public Driver getDriver()
	{
		return driver;
	}
	
	public String getCar()
	{
		return car;
	}
	
	public int getStart()
	{
		return start;
	}
	
	public int getFinish()
	{
		return finish;
	}
	
	public int getLastFinish()
	{
		return finishLast;
	}
	
	public int getPositionChange()
	{
		return finishChange;
	}
	
	public int getCurrentLap()
	{
		return currentLap;
	}
	
	public int getLapsLed()
	{
		return lapsLed;
	}
	
	public boolean ledLaps()
	{
		return ledLaps;
	}
	
	public boolean ledMostLaps()
	{
		return ledMostLaps;
	}
	
	public int getPenalties()
	{
		return penalties;
	}
	
	public float getBehindLeader()
	{
		return behindLeader;
	}
	
	public int getLapsDown()
	{
		return lapsDown;
	}
	
	public int getSeasonPoints()
	{
		AbstractMap<Integer,Standing> standings = getRace().getStandings();
		if(standings.containsKey(driver.getId()))
		{
			return standings.get(driver.getId()).points + getRacePoints();
		}
		
		return getRacePoints();
	}
	
	public int getRacePoints()
	{
		int points = 0;
		
		if(finish == 1)
			points = 185;
		else if(finish <= 6)
			points = 150 + (6-finish)*5;
		else if(finish <= 11)
			points = 130 + (11-finish)*4;
		else if(finish <= 43)
			points = 34 + (43 -finish)*3;
		
		if(ledLaps)
			points += 5;
		
		if(ledMostLaps)
			points += 5;
		
		return points;
	}
	
	public float getSpeed()
	{
		return speed;
	}
	
	public double getThrottle()
	{
		return throttle;
	}
	
	public double getBrake()
	{
		return brake;
	}
	
	public double getLastLapSpeed()
	{
		return lastLapSpeed;
	}
	
	public boolean isCurrent()
	{
		return (lastUpdatedLap == getRace().getCurrentLap());
	}
	
	public Standing getStanding()
	{
		return getRace().getStandings().get(driver);
	}
	
	public int getSeasonRank()
	{
		return getRace().getRankByDriver(driver);
	}
	
	public short getRowNumber()
	{
		return rowNumber;
	}
	

	public void setCurrentLap(int lap)
	{
		if(lap != this.currentLap)
		{
			this.currentLap = lap;
			this.lastUpdatedLap = this.getRace().getCurrentLap();
		}
	}
	
	public void setLapsLed(int lapsLed)
	{
		this.lapsLed = lapsLed;
	}
	
	public void setLedLaps(boolean ledLaps)
	{
		this.ledLaps = ledLaps;
	}
	
	public void setLedMostLaps(boolean ledMostLaps)
	{
		this.ledMostLaps = ledMostLaps;
	}
	
	public void setPenalties(int penalties)
	{
		this.penalties = penalties;
	}
	
	public void setBehindLeader(float behindLeader)
	{
		this.behindLeader = behindLeader;
	}
	
	public void setLapsDown(int lapsDown)
	{
		this.lapsDown = lapsDown;
	}
	
	public void setFinish(int finish)
	{
		if(race != null)
			race.setFinish(car, finish);
		
		this.finish = finish;
	}
	
	public void setLastFinish(int finish)
	{
		this.finishLast = finish;
	}
	
	public void setPositionChange(int change)
	{
		this.finishChange = change;
	}
	
	public void setSpeed(float speed)
	{
		this.speed = speed;
	}
	
	public void setThrottle(double throttle)
	{
		this.throttle = throttle;
	}
	
	public void setBrake(double brake)
	{
		this.brake = brake;
	}
	
	public void setLastLapSpeed(double speed)
	{
		this.lastLapSpeed = speed;
	}
	
	public void setRowNumber(short rowNumber)
	{
		this.rowNumber = rowNumber;
	}
	
	
	public static class SeasonPointsComparator implements Comparator<Result>
	{
		public int compare(Result r1, Result r2)
		{
			if(r1.getSeasonPoints() > r2.getSeasonPoints())
				return 1;
			else if(r1.getSeasonPoints() == r2.getSeasonPoints())
				return 0;
			else
				return -1;
		}
	}
}
