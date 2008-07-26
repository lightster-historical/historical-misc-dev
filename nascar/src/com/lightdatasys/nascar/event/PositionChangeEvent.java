package com.lightdatasys.nascar.event;

public class PositionChangeEvent
{
	private String car;
	private int oldPosition;
	private int newPosition;
	
	
	public PositionChangeEvent(String car, int oldPosition, int newPosition)
	{		
		this.car = car;
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}
	
	
	public String g()
	{
		return car;
	}
	
	public int getOldPosition()
	{
		return oldPosition;
	}
	
	public int getNewPosition()
	{
		return newPosition;
	}
}
