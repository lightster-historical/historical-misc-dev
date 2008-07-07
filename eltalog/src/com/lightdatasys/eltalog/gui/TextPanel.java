package com.lightdatasys.eltalog.gui;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextPanel extends JPanel 
{
	private static final long serialVersionUID = 200806181206L;
	
	private JTextField textField;
	
	
	public TextPanel(String value)
	{
		super();
		
		textField = new JTextField();
		add(textField);
	}
	
	
	public String getValue()
	{
		return textField.getText();
	}
}