package main;


import javax.swing.SwingUtilities;

import controller.Connector;



public class Main{
	
	// Der Debug-Modus kann ein- und ausgeschaltet werden.
	private static final boolean isDebug = false;
	
	//Aktuelle ID der Detailansicht
	private static String id = "";
	
	//Startet das Programm.
    public static void main(String[] args) {           
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {                                           
            	Connector connector = new Connector();
            	connector.connect();           	
            }
        });  
    }

    //Fragt der Statuts des Debug-Modus ab
	public static boolean isDebug() {
		return isDebug;
	}
	
	//Getter und Setter Methoden
	
	
	public static String getId() {
		return id;
	}

	public static void setId(String id) {
		Main.id = id;
	}
}