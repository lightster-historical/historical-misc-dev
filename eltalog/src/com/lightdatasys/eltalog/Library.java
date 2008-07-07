package com.lightdatasys.eltalog;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.lightdatasys.eltalog.gui.LibraryTablePanel;
import com.lightdatasys.io.InputUtility;
import com.lightdatasys.io.OutputUtility;

public abstract class Library<I extends Item> 
	implements Serializable
{
	private static final long serialVersionUID = 1L;
	protected final static long CLASS_VERSION = 2008062401L;
	
	
	private ItemType<I> itemType;
	public ArrayList<I> items;
	
	
	
	public Library(ItemType<I> itemType)
		throws NullPointerException
	{
		super();
		
		if(itemType != null)
		{
			this.itemType = itemType;
			this.items = new ArrayList<I>();
		}
		else
			throw new NullPointerException("Input itemType must be non-null");
	}
	
	
	public ItemType<I> getItemType()
	{
		return itemType;
	}
	
	public LibraryTableModel getTableModel()
	{
		return new LibraryTableModel(this);
	}
	
	public abstract String getName();
	
	
	public LibraryTablePanel<I> getTablePanel()
	{
		return new LibraryTablePanel<I>(this);
	}
	
	
	public void add(I item)
	{
		items.add(item);
	}
	

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) 
		throws IOException, ClassNotFoundException 
	{		
		long objVersion = in.readLong();

		if(objVersion == 2008062401L)
		{
			Object objBuffer = in.readObject();

			if(objBuffer instanceof ItemType)
			{
				items = new ArrayList<I>();
				itemType = (ItemType<I>)objBuffer;
			
				InputUtility.readCollection(in, items, itemType.getItemClass());
			}
		}
		else
		{
			throw new IOException("Unknown version for Movie object: " + objVersion);
		}
	}
	
	//@Override
	private void writeObject(ObjectOutputStream out)
		throws IOException
	{
		System.out.println("Using Library.writeObject");
		out.writeLong(CLASS_VERSION);
		out.writeObject(itemType);
		OutputUtility.writeCollection(out, items);
	}
	
	

	
	public int getColumnCount() 
	{
		return itemType.getColumnCount();
	}

	public int getRowCount() 
	{
		return items.size();
	}

	public Object getValueAt(int row, int col) 
		throws ArrayIndexOutOfBoundsException
	{
		if(!(0 <= row && row < items.size()))
			throw new ArrayIndexOutOfBoundsException();
		if(!(0 <= col && col < getColumnCount()))
			throw new ArrayIndexOutOfBoundsException();
		else
			return items.get(row).getColumn(col);
	}
	
	public I getRowAt(int row)
		throws ArrayIndexOutOfBoundsException
	{
		if(!(0 <= row && row < items.size()))
			throw new ArrayIndexOutOfBoundsException();
		else
			return (I)items.get(row);
	}

	public String getColumnName(int col)
		throws ArrayIndexOutOfBoundsException 
	{
		return itemType.getColumnName(col);
	}
	
	
	
	
	public static Library<?> loadLibrary(String fileName)
		throws ClassNotFoundException, IOException
	{
		Library<?> library;
		
		FileInputStream fileIn = new FileInputStream(fileName);
		ObjectInputStream objIn = new ObjectInputStream(fileIn);
		library = (Library<?>)objIn.readObject();
		objIn.close();
		
		return library;
	}
	
	public class LibraryTableModel extends AbstractTableModel
	{
		public Library<I> library;
		
		public LibraryTableModel(Library<I> lib)
		{
			library = lib;
		}
		
		
		public int getColumnCount() 
		{
			return library.getColumnCount();
		}

		public int getRowCount() 
		{
			return library.getRowCount();
		}

		public Object getValueAt(int row, int col) 
			throws ArrayIndexOutOfBoundsException
		{
			return library.getValueAt(row, col);
		}
		
		public I getRowAt(int row)
			throws ArrayIndexOutOfBoundsException
		{
			return library.getRowAt(row);
		}

		public String getColumnName(int col)
			throws ArrayIndexOutOfBoundsException 
		{
			return library.getColumnName(col);
		}
	}
}
