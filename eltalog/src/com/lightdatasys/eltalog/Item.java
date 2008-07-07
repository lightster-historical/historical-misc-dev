package com.lightdatasys.eltalog;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.lightdatasys.eltalog.field.AbstractField;
import com.lightdatasys.io.OutputUtility;


public abstract class Item implements Serializable
{
	protected final static long CLASS_VERSION = 2008062201L;
	
	protected Map<String,AbstractField> fields;
	
	
	protected Item()
	{
		fields = new HashMap<String,AbstractField>();
	}
	
	
	public abstract ItemType<? extends Item> getItemType();
	
	public abstract Object getColumn(int column)
		throws ArrayIndexOutOfBoundsException;
	
	
	public AbstractField getField(String key)
	{
		return fields.get(key);
	}
	
	
	private void readObject(ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		//System.out.println("Using Item.readObject");
		long objVersion = in.readLong();
		
		if(objVersion == 2008062201L)
		{
			readFieldMap(in, objVersion);
		}
		else
		{
			throw new IOException("Unknown version for Item object: " + objVersion);
		}
	}
	
	protected void readFieldMap(ObjectInputStream in, long objVersion)
		throws IOException, ClassNotFoundException
	{
		if(objVersion == 2008062201L)
		{			
			fields = new HashMap<String,AbstractField>();
			
			int fieldCount = in.readInt();
			for(int i = 0; i < fieldCount; i++)
			{
				Object keyBuffer = in.readObject();
				Object valBuffer = in.readObject();
				
				if(keyBuffer instanceof String && valBuffer instanceof AbstractField)
				{
					String key = (String)keyBuffer;
					AbstractField val = (AbstractField)valBuffer;
					
					fields.put(key, val);
				}
				else
				{
					throw new IOException("Field map data is corrupt");
				}
			}
		}
		else
		{
			throw new IOException("Unknown version for Item object: " + objVersion);
		}
	}
	
	private void writeObject(ObjectOutputStream out)
		throws IOException
	{
		//System.out.println("Using Item.writeObject");
		out.writeLong(CLASS_VERSION);	// write the format version
		OutputUtility.writeMap(out, fields);			// write the map of fields
	}
	
	protected void writeFieldMap(ObjectOutputStream out)
		throws IOException
	{
		out.writeInt(fields.size());
		
		Iterator<Entry<String,AbstractField>> it = fields.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String,AbstractField> field = it.next();

			out.writeObject(field.getKey());
			out.writeObject(field.getValue());
		}
	}
}
