package Main;


import javax.swing.SwingUtilities;

import Controller.Connector;

public class Main{
    public static void main(String[] args) {           
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {                                           
            	Connector connector = new Connector();
            	connector.connect();           	
            }
        });  
    }
}