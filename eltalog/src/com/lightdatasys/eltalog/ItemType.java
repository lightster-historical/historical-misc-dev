package com.lightdatasys.eltalog;

import java.io.Serializable;

import com.lightdatasys.eltalog.gui.ItemEditorPanel;


public interface ItemType<T extends Item>
	extends Serializable
{
	public String getTypeName();
	public Class<T> getItemClass();
	
	public int getColumnCount();
	public String getColumnName(int column)
		throws ArrayIndexOutOfBoundsException;
	public Object getColumn(T item, int column)
		throws ArrayIndexOutOfBoundsException;
	
	
	//public ItemEditorPanel<T> getEditorPanel();
}
