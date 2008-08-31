package com.lightdatasys.gui;

import java.awt.Color;

public class ColorUtility 
{
	public static Color setAlpha(Color color, float alpha)
	{
		System.out.println(alpha + "f");
		return new Color(color.getRed(), color.getBlue(), color.getGreen(), (int)(alpha * 100));
	}
	
	public static Color setAlpha(Color color, int alpha)
	{
		System.out.println(alpha);
		return new Color(color.getRed(), color.getBlue(), color.getGreen(), alpha);
	}
}