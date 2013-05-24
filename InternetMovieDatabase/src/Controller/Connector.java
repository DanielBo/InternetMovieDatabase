package Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import View.LoginWindow;

public class Connector {
	private String url;
	private Connection con;
	private String id;
	private String passwort;
	private LoginWindow logWin;
	private ActionListener actionListener;
	private String favouritesListName = "MERKLISTE"; // darf nur aus Gro�buchstaben bestehen!

	public Connector(){
		this.url = "jdbc:oracle:thin:@dbvm07.iai.uni-bonn.de:1521:lehre";
		this.con = null;
		id = null;
		passwort = null;
		logWin = new LoginWindow();
	}

	private void createConnection() throws SQLException{
		con = DriverManager.getConnection(url, id, passwort);
	}

	public void connect(){
		actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				startLogin();
			}
		};

		logWin.getLoginButton().addActionListener(actionListener);
		logWin.setVisible(true);
		


		// ActionListener to break up loginoperation.
		actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {                                           
						logWin.dispose();
					}
				});
			}
		};                
		logWin.getBtnAbbruch().addActionListener(actionListener);

		JPasswordField jpf = logWin.getJPasswordField();

		jpf.addKeyListener(new KeyListener() {

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
					createConnection();
					System.out.println("Die Verbindung wurde hergestellt.");
					preparing();
					Controller controller = new Controller(con);
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
	
	// Erzeugt eine Tabelle namens Merliste, falls sie noch nicht vorhanden ist.
	private void preparing(){
		try {
			String existenceTest = "SELECT COUNT(*)  FROM user_tables WHERE TABLE_NAME = '" + favouritesListName + "'";
			Statement testStmt;
				testStmt = con.createStatement();
			ResultSet result = testStmt.executeQuery(existenceTest);
			result.next();
			if(result.getInt(1) == 0){
				String newFavoutiteListStatement = "CREATE TABLE " + favouritesListName + " (title_id Number(4) Not Null)";
				Statement newListStmt = con.createStatement();
				newListStmt.executeUpdate(newFavoutiteListStatement);
			}
			result.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Connection getConnection(){
		return con;
	}

	public LoginWindow getLogWin(){
		return logWin;
	}
}
