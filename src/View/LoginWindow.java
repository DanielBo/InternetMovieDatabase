package View;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField idField;
	private JButton btnLogin;
	private JPasswordField passwordField;
	private JLabel lblIdOderPasswort;
	private JButton btnAbbruch;
	/**
	 * Create the frame.
	 */
	public LoginWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 425, 301);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblLogin = new JLabel("Login");
		lblLogin.setBounds(168, 24, 63, 16);
		contentPane.add(lblLogin);
		
		JLabel lblId = new JLabel("ID: ");
		lblId.setBounds(83, 68, 39, 16);
		contentPane.add(lblId);
		
		idField = new JTextField();
		idField.setBounds(195, 65, 159, 22);
		contentPane.add(idField);
		idField.setColumns(10);
		
		JLabel lblPasswort = new JLabel("Passwort:");
		lblPasswort.setBounds(65, 125, 79, 16);
		contentPane.add(lblPasswort);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(195, 122, 159, 22);
		contentPane.add(passwordField);
		
		btnLogin = new JButton("Login");
		btnLogin.setBounds(104, 216, 79, 25);
		contentPane.add(btnLogin);
		
		setBtnAbbruch(new JButton("Abbruch"));
		getBtnAbbruch().setBounds(216, 216, 87, 25);
		contentPane.add(getBtnAbbruch());
		
		setLblIdOderPasswort(new JLabel("ID oder Passwort falsch!"));
		getLblIdOderPasswort().setFont(new Font("Tahoma", Font.PLAIN, 15));
		getLblIdOderPasswort().setForeground(Color.RED);
		getLblIdOderPasswort().setBounds(100, 162, 239, 25);
		getLblIdOderPasswort().setVisible(false);
		contentPane.add(getLblIdOderPasswort());
	}
	
	public JButton getLoginButton(){
		return btnLogin;
	}
	
	public String getID(){
		return idField.getText();
	}
	
	public String getPasswort(){
		return new String(passwordField.getPassword());
	}

	public JLabel getLblIdOderPasswort() {
		return lblIdOderPasswort;
	}

	public void setLblIdOderPasswort(JLabel lblIdOderPasswort) {
		this.lblIdOderPasswort = lblIdOderPasswort;
	}

	public JButton getBtnAbbruch() {
		return btnAbbruch;
	}

	public void setBtnAbbruch(JButton btnAbbruch) {
		this.btnAbbruch = btnAbbruch;
	}
	
	public JPasswordField getJPasswordField(){
		return passwordField;
	}
}
