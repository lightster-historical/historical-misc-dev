package com.lightdatasys.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Map;

public class InputUtility
{
	@SuppressWarnings("unchecked")
	public static <V> void readCollection(ObjectInputStream in, Collection<V> list, Class<V> valType)
		throws IOException, ClassNotFoundException
	{
		long objVersion = in.readLong();
	
		if(objVersion == 2008062201L)
		{			
			int fieldCount = in.readInt();
			for(int i = 0; i < fieldCount; i++)
			{
				Object valBuffer = in.readObject();
				
				if(valType.isInstance(valBuffer))
				{
					V val = (V)valBuffer;
					
					list.add(val);
				}
				else
				{
					throw new IOException("Collection data is corrupt");
				}
			}
		}
		else
		{
			throw new IOException("Unknown version for Collection object: " + objVersion);
		}
	}
	

	@SuppressWarnings("unchecked")
	public static <K, V> void readMap(ObjectInputStream in, Map<K,V> map, Class<K> keyType, Class<V> valType)
		throws IOException, ClassNotFoundException
	{
		long objVersion = in.readLong();
		
		if(objVersion == 2008062201L)
		{			
			int fieldCount = in.readInt();
			for(int i = 0; i < fieldCount; i++)
			{
				Object keyBuffer = in.readObject();
				Object valBuffer = in.readObject();
				
				if(keyType.isInstance(keyBuffer) && valType.isInstance(valBuffer))
				{
					K key = (K)keyBuffer;
					V val = (V)valBuffer;
					
					map.put(key, val);
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
}
