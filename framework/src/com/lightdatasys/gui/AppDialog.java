package com.lightdatasys.gui;

import javax.swing.JDialog;

public class AppDialog extends JDialog
{
	private static final long serialVersionUID = 200806181106L;
	
	
    //protected T obj;

    public AppDialog(AppWindow parent, String title)//, T o)
    {
        super(parent, title, true);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        //this.obj = o;

        WindowUtil.centerWindow(this, (AppWindow)getParent());
    }

    /*public AppDialog(AppWindow parent, String title)
    {
        this(parent, title, null);
    }*/

    public AppDialog(AppWindow parent)
    {
        this(parent, null);//, null);
    }
    
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        WindowUtil.centerWindow(this, (AppWindow)getParent());
    }
}
