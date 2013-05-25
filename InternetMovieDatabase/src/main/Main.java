package main;


import javax.swing.SwingUtilities;

import controller.Connector;



public class Main{
	
	private static final boolean isDebug = false;
	private static String id = "";
	
    public static void main(String[] args) {           
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {                                           
            	Connector connector = new Connector();
            	connector.connect();           	
            }
        });  
    }

	public static boolean isDebug() {
		return isDebug;
	}

	public static String getId() {
		return id;
	}

	public static void setId(String id) {
		Main.id = id;
	}
}