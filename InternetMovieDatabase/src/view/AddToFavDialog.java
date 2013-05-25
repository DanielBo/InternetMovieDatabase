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
import java.awt.Component;
import javax.swing.Box;

public class AddToFavDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MainWindow mainWindow;
	private JTextField textField;
	private Favorites favs;

	private JComboBox<String> favCategorieComboBox;

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
		lblEintragZurFavoritenliste.setBounds(119, 29, 244, 16);
		getContentPane().add(lblEintragZurFavoritenliste);

		favCategorieComboBox = new JComboBox<String>();
		favCategorieComboBox.setBounds(184, 75, 212, 28);
		getContentPane().add(favCategorieComboBox);

		textField = new JTextField();
		textField.setBounds(48, 236, 244, 28);
		getContentPane().add(textField);
		textField.setColumns(10);

		JButton btnHinzufgen = new JButton("Hinzuf\u00FCgen");
		btnHinzufgen.setBounds(175, 125, 117, 29);
		getContentPane().add(btnHinzufgen);

		JButton btnErtstellen = new JButton("Ertstellen");
		btnErtstellen.setBounds(315, 237, 117, 29);
		getContentPane().add(btnErtstellen);

		btnErtstellen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				favCategorieComboBox.addItem(textField.getText());
			}
		});

		btnHinzufgen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (id != null)
					try {
						favs.addIdToFavorites(id, (String)favCategorieComboBox.getSelectedItem());
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
			}
		});

		JLabel lblNeueListeHinzufgen = new JLabel("Neue Kategorie hinzufügen");
		lblNeueListeHinzufgen.setBounds(159, 208, 172, 16);
		getContentPane().add(lblNeueListeHinzufgen);
		
		JLabel lblKategorie = new JLabel("Kategorie:");
		lblKategorie.setBounds(48, 78, 130, 23);
		getContentPane().add(lblKategorie);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalGlue.setBounds(0, 179, 482, 16);
		getContentPane().add(horizontalGlue);

		this.setVisible(true);
		this.setSize(new Dimension(500,350));

	}

	public JComboBox<String> getFavCategorieComboBox() {
		return favCategorieComboBox;
	}
}
