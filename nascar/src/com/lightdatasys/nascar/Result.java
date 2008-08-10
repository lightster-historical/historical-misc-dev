package com.lightdatasys.nascar;

import java.util.AbstractMap;

public class Result
{
	private Race race;
	private Driver driver;
	private String car;
	private int start;
	private int finish;
	private int finishLast;
	private int finishChange;
	private int lapsLed;
	private boolean ledLaps;
	private boolean ledMostLaps;
	private int penalties;
	private float behindLeader;
	private int lapsDown;
	
	
	public Result(Race race, Driver driver, String car, int start, int finish, boolean ledLaps, boolean ledMostLaps, int penalties)
	{		
		this.race = race;
		this.driver = driver;
		this.car = car;
		this.start = start;
		this.finish = finish;
		this.finishLast = finish;
		this.finishChange = 0;
		this.lapsLed = 0;
		this.ledLaps = ledLaps;
		this.ledMostLaps = ledMostLaps;
		this.penalties = penalties;
		this.behindLeader = 0.0f;
		this.lapsDown = 0;
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
}
