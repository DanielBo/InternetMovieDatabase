package view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import main.Main;
import controller.favourites.Favorites;

public class AddToFavDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MainWindow mainWindow;
	private JTextField textField;
	private Favorites favs;

	private JComboBox<String> comboBox;

	private String id;

	public AddToFavDialog(MainWindow mainWindow, Favorites favs, String id) {
		if (Main.isDebug())
			System.out.println("Building Dialog");
		this.mainWindow = mainWindow;
		this.favs = favs;
		this.id = id;
		init();
	}

	private void init() {
		getContentPane().setLayout(null);
		final Dimension d = this.getToolkit().getScreenSize(); 
	    this.setLocation((int) ((d.getWidth() - this.getWidth()) / 2), (int) ((d.getHeight() - this.getHeight()) / 2));

		JLabel lblEintragZurFavoritenliste = new JLabel("Eintrag zur Favoritenliste hinzuf\u00FCgen");
		lblEintragZurFavoritenliste.setBounds(102, 113, 272, 16);
		getContentPane().add(lblEintragZurFavoritenliste);

		comboBox = new JComboBox<String>();
		comboBox.setBounds(113, 141, 210, 16);
		getContentPane().add(comboBox);

		textField = new JTextField();
		textField.setBounds(40, 55, 244, 28);
		getContentPane().add(textField);
		textField.setColumns(10);

		JButton btnHinzufgen = new JButton("Hinzuf\u00FCgen");
		btnHinzufgen.setBounds(151, 169, 117, 29);
		getContentPane().add(btnHinzufgen);

		JButton btnErtstellen = new JButton("Ertstellen");
		btnErtstellen.setBounds(307, 56, 117, 29);
		getContentPane().add(btnErtstellen);

		btnErtstellen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comboBox.addItem(textField.getText());
			}
		});

		btnHinzufgen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (id != null)
					try {
						favs.addIdToFavorites(id, (String)comboBox.getSelectedItem());
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
			}
		});

		JLabel lblNeueListeHinzufgen = new JLabel("Neue Liste Hinzuf\u00FCgen");
		lblNeueListeHinzufgen.setBounds(151, 27, 172, 16);
		getContentPane().add(lblNeueListeHinzufgen);

		this.setVisible(true);
		this.setSize(new Dimension(500,350));

	}


}
