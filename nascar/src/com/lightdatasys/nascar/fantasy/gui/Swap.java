package com.lightdatasys.nascar.fantasy.gui;

public class Swap
{
	private int distance;
	private int maxDelta;
	private int currDelta;
	
	
	public Swap(int distance, int maxDelta)
	{
		this.distance = distance;
		this.maxDelta = maxDelta;
		this.currDelta = 0;
	}
	
	
	public void increment()
	{
		currDelta++;
	}
	
	public int getDelta()
	{
		return currDelta;
	}
	
	public int getMaxDelta()
	{
		return maxDelta;
	}
	
	public int getDisplacement()
	{
		int displacement = 0;
		
		int halfDelta = maxDelta / 2;
		
		float maxSpeed = 2.0f * ((.5f * distance) / halfDelta);
		float accel = maxSpeed / halfDelta;
		
		if(isDone())
			displacement = distance;
		else if(currDelta >= halfDelta)
		{
			//System.out.println("a");
			int delta = currDelta - halfDelta;
			
			displacement = Math.round(distance / 2.0f + maxSpeed * delta - .5f * accel * delta * delta); 
			//displacement = (int)(.5f * accel * halfDelta * halfDelta);
		}
		else
		{
			//System.out.println("b" + accel);
			displacement = Math.round(.5f * accel * currDelta * currDelta);
		}
		
		return displacement;
	}
	
	public boolean isDone()
	{
		return (currDelta >= maxDelta);
	}
}
