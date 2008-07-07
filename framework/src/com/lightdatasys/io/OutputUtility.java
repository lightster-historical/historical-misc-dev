package com.lightdatasys.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class OutputUtility 
{
	public static <V> void writeCollection(ObjectOutputStream out, Collection<V> list)
		throws IOException
	{
		final long METHOD_VERSION = 2008062201L;
		out.writeLong(METHOD_VERSION);
		
		out.writeInt(list.size());
		
		Iterator<V> it = list.iterator();
		while(it.hasNext())
		{
			V val = it.next();
			
			out.writeObject(val);
		}			
	}
	
	public static <K, V> void writeMap(ObjectOutputStream out, Map<K,V> map)
		throws IOException
	{
		out.writeInt(map.size());
		
		Iterator<Entry<K,V>> it = map.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<K,V> field = it.next();
	
			out.writeObject(field.getKey());
			out.writeObject(field.getValue());
		}
	}
}
