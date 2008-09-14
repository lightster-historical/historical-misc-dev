package com.lightdatasys.nascar.event;

import java.util.AbstractMap;
import java.util.HashMap;

public abstract class PositionChangeListener 
{
	public AbstractMap<Object,Integer> oldPositions;
	
	public PositionChangeListener()
	{
		oldPositions = new HashMap<Object,Integer>();
	}
	
	public abstract void positionChanged(PositionChangeEvent event);
}
