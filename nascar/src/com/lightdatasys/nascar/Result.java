package com.lightdatasys.nascar;

public class Result
{
	private Race race;
	private Driver driver;
	private String car;
	private int start;
	private int finish;
	private boolean ledLaps;
	private boolean ledMostLaps;
	private int penalties;
	
	
	Result(Race race, Driver driver, String car, int start, int finish, boolean ledLaps, boolean ledMostLaps, int penalties)
	{		
		this.race = race;
		this.driver = driver;
		this.car = car;
		this.start = start;
		this.finish = finish;
		this.ledLaps = ledLaps;
		this.ledMostLaps = ledMostLaps;
		this.penalties = penalties;
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
}
