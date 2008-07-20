package com.lightdatasys.nascar.fantasy.gui;

public class Swap
{
	private int startPos;
	private int endPos;
	private long maxDelta;
	private long start;
	
	
	public Swap(int startPos, int endPos, long maxDelta)
	{
		this.startPos = startPos;
		this.endPos = endPos;
		this.maxDelta = maxDelta;
		this.start = System.currentTimeMillis();
	}
	
	
	public long getDelta()
	{
		return System.currentTimeMillis() - start;
	}
	
	public long getMaxDelta()
	{
		return maxDelta;
	}
	
	public int getPosition()
	{
		int displacement = 0;
		
		int distance = endPos - startPos;
		
		long halfDelta = maxDelta / 2;
		
		float maxSpeed = 2.0f * ((.5f * distance) / halfDelta);
		float accel = maxSpeed / halfDelta;
		
		if(isDone())
			displacement = distance;
		else if(getDelta() >= halfDelta)
		{
			//System.out.println("a");
			long delta = getDelta() - halfDelta;
			
			displacement = Math.round(distance / 2.0f + maxSpeed * delta - .5f * accel * delta * delta); 
			//displacement = (int)(.5f * accel * halfDelta * halfDelta);
		}
		else
		{
			//System.out.println("b" + accel);
			displacement = Math.round(.5f * accel * getDelta() * getDelta());
		}
		
		return startPos + displacement;
	}
	
	public boolean isDone()
	{
		return (getDelta() >= maxDelta);
	}
}
