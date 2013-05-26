package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import main.Main;

import view.LoginWindow;


public class Connector {
	private String url;
	private Connection con;
	private String id;
	private String passwort;
	private LoginWindow logWin;
	private ActionListener actionListener;

	public Connector(){
		this.url = "jdbc:oracle:thin:@dbvm07.iai.uni-bonn.de:1521:lehre";
		this.con = null;
		id = null;
		passwort = null;
		logWin = new LoginWindow();
	}

	// Baut Verbindung mit Hilfe des Oracle Treibers auf.
	private void createConnection() throws SQLException{
		con = DriverManager.getConnection(url, id, passwort);
	}

	// Verbindet die Buttons des Loginfensters mit Aktionen
	public void connect(){
		
		logWin.getLoginButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				startLogin();
			}
		});


		// ActionListener to break up loginoperation.
		logWin.getBtnAbbruch().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {                                           
						logWin.dispose();
					}
				});
			}
		});                

		logWin.getJPasswordField().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// do nothing
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// do nothing
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					startLogin();
			}
		});

	}

	private void startLogin() {
		final JLabel lblIdOderPasswort = logWin.getLblIdOderPasswort();
		lblIdOderPasswort.setText("Logging In.");
		lblIdOderPasswort.setVisible(true);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {                                           
				id = logWin.getID();
				passwort = logWin.getPasswort();
				try {
					
					//Stellt Verbindung her.
					createConnection();
					
					if (Main.isDebug())
						System.out.println("Die Verbindung wurde hergestellt.");
					
					//Contrsoller wird erzeugt. Damit startet auch das Hauptfenster
					new Controller(con);
						
					//Das Loginfesnter wird geschlossen.
					logWin.dispose();
					
				} catch (SQLException e) {
					if (1017 == e.getErrorCode()){
						lblIdOderPasswort.setText("ID/Password mismatch.");
						lblIdOderPasswort.setVisible(true);    					
					}
				}
			}
		});
	}

	// Getter-Methoden
	
	public Connection getConnection(){
		return con;
	}

	public LoginWindow getLogWin(){
		return logWin;
	}
}
