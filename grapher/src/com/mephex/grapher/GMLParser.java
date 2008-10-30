package com.mephex.grapher;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class GMLParser 
{
	protected SAXBuilder builder;
	protected Document doc;
	
	protected Graph graph;
	protected DefaultGraphDataModel model;
	
	
	protected GMLParser(SAXBuilder builder, Document doc)
	{
		this.builder = builder;
		this.doc = doc;
	}

	
	public static Graph loadGraphDataModel(File file)
		throws FileNotFoundException
	{
		return loadGraphDataModel(new FileInputStream(file));
	}
	
	public static Graph loadGraphDataModel(String file)
		throws FileNotFoundException
	{
		return loadGraphDataModel(new FileInputStream(file));
	}
	
	public static Graph loadGraphDataModel(InputStream inStream)
	{
		if(inStream == null)
			throw new NullPointerException();
		
	    try 
	    {
	    	SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(inStream);
	    	GMLParser parser = new GMLParser(builder, doc);
	    	
	    	return parser.generateGraph();
	    }
	    catch (JDOMException e) 
	    {
	    	e.printStackTrace();
	    }
	    catch (IOException e) 
	    { 
	    	e.printStackTrace();
	    }
	    
	    return null;
	}
	
	
	protected Graph generateGraph()
	{
		graph = new Graph();
		processDocument();
		
		return graph;
	}
	
	
	protected void processDocument()
	{	
        List<?> children = doc.getContent();

        for(Object child : children)
        {
        	if(child instanceof Element)
        	{
        		Element element = (Element)child;
        		
        		if(element.getName().equals("graph"))
        		{
        			processGraph(element);
        		}
        		else
        		{
        			System.out.println("Unknown element: " + element.getName());
        		}
        	}
        }
	}
	
	protected void processGraph(Element element)
	{
        List<?> children;
        
        children = element.getChildren("data-set");
        for(Object dataSet : children)
        {
        	if(dataSet instanceof Element)
        	{
            	model = new DefaultGraphDataModel();
        		processDataSet((Element)dataSet);
            	
            	if(model.getCount() > 0)
            		graph.addModel(model);

            	Attribute visibleAttr = ((Element)dataSet).getAttribute("visible");
            	if(visibleAttr != null)
            	{
            		String visibleVal = visibleAttr.getValue().toLowerCase();
            		if(visibleVal.equals("false") || visibleVal.equals("no")
        				|| visibleVal.equals("0"))
            			model.setVisible(false);
            	}
        	}
        }
	}
	
	protected void processDataSet(Element dataSet)
	{
        Element child;
        child = dataSet.getChild("data");
        if(child != null)
    	{
    		processData((Element)child);
    	}
        
        child = dataSet.getChild("line-style");
        if(child != null)
    	{
        	child = child.getChild("color");
        	if(child != null)
        	{
            	Color color = null;
        		String colorVal = child.getValue();
        		if(colorVal.matches("#[0-9a-fA-F]{6}"))
        		{
        			String r = colorVal.substring(1, 3);
        			String g = colorVal.substring(3, 5);
        			String b = colorVal.substring(5, 7);
        			
        			color = new Color
    				(
    					Integer.parseInt(r, 16),
    					Integer.parseInt(g, 16),
    					Integer.parseInt(b, 16)
					);
        			
        			model.setColor(color);
        		}
        	}
    	}
	}
	
	protected void processData(Element data)
	{
        List<?> children;
        
        children = data.getChildren("point");
        for(Object point : children)
        {
        	if(point instanceof Element)
        	{
        		processPoint((Element)point);
        	}
        }
	}
	
	protected void processPoint(Element point)
	{
        Object temp;
        Double x = null, y = null;
        Element xElem = null, yElem = null;
        
        temp = point.getChild("x");
        if(temp instanceof Element)
        	xElem = (Element)temp;
        
        temp = point.getChild("y");
        if(temp instanceof Element)
        	yElem = (Element)temp;
        
        if(xElem != null && yElem != null)
        {
        	x = Double.parseDouble(xElem.getText());
        	y = Double.parseDouble(yElem.getText());
        	
        	Color color = null;
        	Attribute colorAttr = point.getAttribute("color");
        	if(colorAttr != null)
        	{
        		String colorVal = colorAttr.getValue();
        		if(colorVal.matches("#[0-9a-fA-F]{6}"))
        		{
        			String r = colorVal.substring(1, 3);
        			String g = colorVal.substring(3, 5);
        			String b = colorVal.substring(5, 7);
        			
        			color = new Color
    				(
    					Integer.parseInt(r, 16),
    					Integer.parseInt(g, 16),
    					Integer.parseInt(b, 16)
					);
        		}
        	}
        	
        	model.addPoint(new GraphPoint(x, y, color));
        }
	}
}
