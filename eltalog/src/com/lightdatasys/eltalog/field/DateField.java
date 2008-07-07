package com.lightdatasys.eltalog.field;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JPanel;

import com.lightdatasys.eltalog.gui.TextPanel;

public class DateField extends AbstractField 
{
	private static final long serialVersionUID = 200806181106L;
	
	private TextPanel panel;
	
	private Calendar date;
	
		
	public DateField(String label)
	{
		super(label);
		
		this.date = new GregorianCalendar();
	}
	
	public DateField(String label, Date date)
	{
		this(label);
		
		if(date == null)
			date = new Date();
		
		setDate(date);
	}

	
	public void setDate(Date date)
	{
		this.date.setTime(date);
	} 

	public Date getDate() {return date.getTime();}
	

	@Override
	public JPanel getPanel() 
	{
		if(panel == null)
		{
			//panel = new TextPanel(title);
		}
		
		return panel;
	}

	@Override
	public void keepValues()
	{
		if(panel != null)
		{
			//title = panel.getValue();
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
		return dateFormat.format(getDate());
	}
}
