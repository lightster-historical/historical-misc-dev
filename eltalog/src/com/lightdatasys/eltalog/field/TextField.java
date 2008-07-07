package com.lightdatasys.eltalog.field;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JPanel;

import com.lightdatasys.eltalog.gui.TextPanel;

public class TextField extends AbstractField 
{
	private static final long serialVersionUID = 200806181106L;
	
	private TextPanel panel;
	
	private String title;
	
		
	public TextField(String label)
	{
		super(label);
		
		this.title = "";
	}
	
	public TextField(String label, String title)
	{
		this(label);
		
		this.title = title;
	}

	
	public void setValue(String title) {this.title = title;} 

	public String getValue() {return title;}
	

	@Override
	public JPanel getPanel() 
	{
		if(panel == null)
		{
			panel = new TextPanel(title);
		}
		
		return panel;
	}

	@Override
	public void keepValues()
	{
		if(panel != null)
		{
			title = panel.getValue();
		}
	}

	@Override
	public void disposePanel()
	{
		if(panel != null)
		{
			panel.removeAll();
			panel = null;
		}
	}
	
	
	private void readObject(ObjectInputStream in) 
		throws IOException, ClassNotFoundException 
	{
	}

	private void writeObject(ObjectOutputStream out)
		throws IOException
	{
	}
	
	
	public String toString()
	{
		return getValue();
	}
}
