package com.lightdatasys.eltalog.field;

import java.io.Serializable;

import javax.swing.JPanel;

public abstract class AbstractField implements Serializable
{
	private String label;
	

	protected AbstractField()
	{
		this.label = null;
	}
	
	protected AbstractField(String label)
	{
		this.label = label;
	}
	
	
	public String getLabel() {return label;}
	
	
	public abstract JPanel getPanel();
	public abstract void keepValues();
	public abstract void disposePanel();
}
