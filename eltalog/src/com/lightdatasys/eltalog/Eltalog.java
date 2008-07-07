package com.lightdatasys.eltalog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.lightdatasys.eltalog.gui.FilterPanel;
import com.lightdatasys.eltalog.gui.LibraryTablePanel;
import com.lightdatasys.gui.AppDialog;
import com.lightdatasys.gui.AppWindow;
import com.lightdatasys.gui.QuitHandler;
import com.lightdatasys.gui.WindowUtil;

public class Eltalog extends AppWindow implements QuitHandler
{
	private static final long serialVersionUID = 200806181106L;


	private JTabbedPane libraryTabs;
	private JPanel buttonPane;

	private JButton newButton;
	private JButton editButton;
	private JButton deleteButton;
	
	private Map<String,Library<?>> libraries;
	private String[] libraryFiles = {"movies.elt"};//, "books.elt", "music.elt", "games.elt"};
	private Library<?>[] defaultLibraries = {new MovieLibrary(), new BookLibrary(), new MusicLibrary(), new GameLibrary()};
	
	
	
	public static void main(String[] args) 
	{
		Eltalog catalog = new Eltalog();
		if(catalog == null)
			return;
	}
	
	
	
	
	
	public Eltalog()
	{
		super("Eltalog");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		libraries = new HashMap<String,Library<?>>();
		for(int i = 0; i < libraryFiles.length; i++)
		{
			String libFile = libraryFiles[i];
			
			try
			{
				System.out.println("Loading library file: " + libFile);
				libraries.put(libFile, defaultLibraries[0]);
				Library.loadLibrary(libFile);
				System.out.println("Library file loaded");
				System.out.println(libraries.get(libFile).getTablePanel().getName());
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				System.out.println("Library file could not be opened");
				e.printStackTrace();
				
				//*
				Library<Movie> library = (Library<Movie>)defaultLibraries[i];
				/*Movie m;
				for(int j = 0; j < 1000; j++)
				{
					m = new Movie("Hello");
					m.setReleaseDate((new GregorianCalendar(2008, 0, 35)).getTime());
					library.add(m);
					m = new Movie("Test 123");
					m.setReleaseDate((new GregorianCalendar()).getTime());
					library.add(m);
					m = new Movie("Test 12443");
					m.setReleaseDate((new GregorianCalendar()).getTime());
					library.add(m);
				}*/

				libraries.put(libFile, library);
				//*/
				/*
				try
				{
					FileOutputStream fileOut = new FileOutputStream(libFile);
					ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
					objOut.writeObject(library);
					objOut.close();
					fileOut.close();
					
				}
				catch(Exception e2)
				{
					e2.printStackTrace();
				}
				//*/
			}
		}

		JSplitPane splitLibrariesMain;
		splitLibrariesMain = new JSplitPane();
		splitLibrariesMain.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitLibrariesMain.setContinuousLayout(true);
		
		JTree libTree = new JTree();
		libTree.setBackground(Color.BLACK);
		libTree.removeAll();
		splitLibrariesMain.setLeftComponent(libTree);

		JPanel libraryButtonPane = new JPanel(new BorderLayout());
		
		JSplitPane splitFilterTable;
		{
			splitFilterTable = new JSplitPane();
			splitFilterTable.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitFilterTable.setContinuousLayout(true);
			
			splitFilterTable.setTopComponent(new FilterPanel());

			/*
			Movie m;
			for(int i = 0; i < 1000; i++)
			{
				m = new Movie("Hello");
				m.setReleaseDate((new GregorianCalendar(2008, 0, 35)).getTime());
				library.add(m);
				m = new Movie("Test 123");
				m.setReleaseDate((new GregorianCalendar()).getTime());
				library.add(m);
			}
			//*/
			
			/*
			try
			{
				FileOutputStream fileOut = new FileOutputStream("movies.elt");
				ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
				objOut.writeObject(library);
				objOut.close();
				fileOut.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			//*/
			//*
			
			
			//JComponent libraryTable = new LibraryTablePanel<Movie>(library);
			libraryTabs = new JTabbedPane()
			{
			/*	public void add(LibraryTablePanel<?> panel)
				{
					add(panel.getName(), panel);
				}*/
			};
			Iterator<Entry<String,Library<?>>> it = libraries.entrySet().iterator();
			while(it.hasNext())
			{
				Library<?> lib = it.next().getValue();
				//*
				LibraryTablePanel<?> libPanel = lib.getTablePanel();
				libraryTabs.add(libPanel);
				libPanel.addListSelectionListener
				(
					new ListSelectionListener()
					{
						public void valueChanged(ListSelectionEvent e)
						{		
							buttonPane.validate();
						}
					}
				);
				//*/
			}
			/*LibraryTablePanel<Movie> movieLibrary = new LibraryTablePanel<Movie>(library);
			libraryTabs.add(movieLibrary);
			movieLibrary.addListSelectionListener
			(
					new ListSelectionListener()
					{
						public void valueChanged(ListSelectionEvent e)
						{		
							buttonPane.validate();
						}
					}
			);
			libraryTabs.add("Books", new LibraryTablePanel<Movie>(library));
			libraryTabs.add("Music", new LibraryTablePanel<Movie>(library));
			libraryTabs.add("Games", new LibraryTablePanel<Movie>(library));
			*/
			libraryTabs.addChangeListener
			(
					new ChangeListener()
					{
						public void stateChanged(ChangeEvent e)
						{
							if(e.getSource().equals(libraryTabs))
							{
								buttonPane.validate();
							}
						}
					}
			);
			splitFilterTable.setBottomComponent(libraryTabs);
		}
		splitLibrariesMain.setRightComponent(splitFilterTable);
		libraryButtonPane.add(splitFilterTable, BorderLayout.CENTER);
		
		{
			newButton = new JButton("New");
			newButton.addActionListener
			(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						AppDialog dialog = new AppDialog(Eltalog.this);
						dialog.setSize(300, 200);
						dialog.setVisible(true);
					}
				}
			);
			
			editButton = new JButton("Edit");
			editButton.addActionListener
			(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						AppDialog dialog = new AppDialog(Eltalog.this);
						dialog.setSize(300, 200);
						dialog.setVisible(true);
					}
				}
			);
			
			deleteButton = new JButton("Delete");
			deleteButton.addActionListener
			(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						int confirm = JOptionPane.showConfirmDialog(Eltalog.this, "Are you sure you want to delete the selected item?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						
						if(confirm == JOptionPane.YES_OPTION)
						{
							System.out.println("Delete item code here");
						}
					}
				}
			);
			
			buttonPane = new JPanel(new FlowLayout())
			{
				private static final long serialVersionUID = 1L;

				public void validate()
				{
					super.validate();

					LibraryTablePanel<?> library = (LibraryTablePanel<?>)libraryTabs.getSelectedComponent();
					
					if(library != null)
					{
						ArrayList<?> selected = library.getSelectedItems();
	
						editButton.setEnabled(selected.size() == 1);
						deleteButton.setEnabled(selected.size() >= 1);
					}
				}
			};
			buttonPane.add(newButton);
			buttonPane.add(editButton);
			buttonPane.add(deleteButton);
			buttonPane.validate();
		}
		libraryButtonPane.add(buttonPane, BorderLayout.PAGE_END);
		
		add(libraryButtonPane);
		
		WindowUtil.centerWindow(this);

		splitLibrariesMain.setDividerSize(4);		
		splitFilterTable.setDividerLocation(150);

		splitLibrariesMain.setDividerSize(3);
		splitLibrariesMain.setDividerLocation(150);

		this.setVisible(true);

		splitFilterTable.setDividerLocation(.3);
		
		splitFilterTable.revalidate();
		splitFilterTable.repaint();
		
		/*
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }

		
		try {
		    Connection conn = 
		        DriverManager.getConnection("jdbc:mysql://localhost/litesign_alpha?" + 
		                                    "user=litesign_mlight&password=***REMOVED***");

		     // Do something with the Connection
		    
		    conn.close();

		 } catch (SQLException ex) {
		     // handle any errors
		     System.out.println("SQLException: " + ex.getMessage());
		     System.out.println("SQLState: " + ex.getSQLState());
		     System.out.println("VendorError: " + ex.getErrorCode());
		 }
		 //*/
	}
	
	
	public void writeLibraries()
	{
		Iterator<Entry<String,Library<?>>> it = libraries.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String,Library<?>> entry = it.next();
			
			String libFile = entry.getKey();
			MovieLibrary library345 = (MovieLibrary)entry.getValue();
			System.out.println(library345);
			MovieLibrary testLibrary = new MovieLibrary();
			System.out.println(testLibrary);

			Method[] methods;
			methods = library345.getClass().getDeclaredMethods();
			for(Method m : methods)
				System.out.println(m.getName() + " " + m.getModifiers());
			
			try
			{
				System.out.println("Writing library file: " + libFile);

				if(library345 != null)
				{
					FileOutputStream fileOut = new FileOutputStream(libFile);
					ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
					objOut.writeObject(library345);
					objOut.close();
					fileOut.close();
				
					System.out.println("Library file written");
				}
				else
					System.out.println("Library is null");
			}
			catch(Exception e)
			{
				System.out.println("Library file could not be written");
				System.out.println(library345.getName() + " " + library345.getClass().getName() + " " + library345.getClass().getCanonicalName());
				e.printStackTrace();
			}
		}
	}
	
	
	public void handleClose()
	{
        System.out.println("Close Handled");
        
        handleQuit();
	}
    
    public void handleQuit()
    {
        System.out.println("Quit Handled");
        
        writeLibraries();
        
        dispose();
    } 
}
