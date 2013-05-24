package main;


import javax.swing.SwingUtilities;

import controller.Connector;


public class Main{
	
	public static final boolean isDebug = true;
	
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
}