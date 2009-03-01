package com.lightdatasys.nascar.live.gui;

public class Swap
{
	private static int currentCacheIndex = 1;
	
	private int startPos;
	private int endPos;
	private long maxDelta;
	private long start;
	
	private int cacheIndex;
	private int cachedPosition;
	
	
	public Swap(int startPos, int endPos, long maxDelta)
	{
		this.startPos = startPos;
		this.endPos = endPos;
		this.maxDelta = maxDelta;
		this.start = System.currentTimeMillis();
		
		this.cacheIndex = 0;
		this.cachedPosition = startPos;
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
		if(maxDelta == 0)
			return endPos;
		else if(cacheIndex == currentCacheIndex)
			return cachedPosition;
		
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
		
		cacheIndex = currentCacheIndex;
		cachedPosition = startPos + displacement;
		
		return cachedPosition;
	}
	
	public boolean isDone()
	{
		return (getDelta() >= getMaxDelta());
	}
	
	
	public static void incrementCacheIndex()
	{
		currentCacheIndex = (currentCacheIndex + 1) % 5;
	}
}
