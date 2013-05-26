package view;

import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

//Erzeugt das Hauptfenster mit Suchtab, Detailansichttab und Merklistentab.
public class MainWindow extends JFrame {

	private Connection con;

	private JPanel contentPane;
	private JTextField searchField;
	private JTextField textFieldConstraint1;
	private JButton btnSucheStarten;
	private JButton btnAddConstraint1;

	private JComboBox<String> modeSelector;
	private JList<String> listViewConstraints;
	private JScrollPane mainTableScrollPane;
	private JTable table;
	private JTextField textFieldConstraint2;

	private JButton btnAddConstraint2;

	private JComboBox<String> comparisonCombobox2;

	private JComboBox<String> constraintComboBox2;

	private JButton btnEinschrnkungEntfernen;

	private JComboBox<String> constraintComboBox1;

	private JComboBox<String> comparisonCombobox1;

	private DefaultListModel<String> listModel;
	private JLabel lblNewLabel_1;

	private JComboBox<String> constraint1AndOr;
	private JTable detailTable;
	private JLabel option1Label;
	private JLabel option2Label;
	private JLabel option3Label;

	private JTabbedPane tabPane;
	private JLabel detailTableTitle;
	private JScrollPane scrollPane_2;

	private JTable favouriteTable;

	private DefaultTableModel favouriteTablemodel;

	private JComboBox<String> constraint2AndOr;

	private JComboBox<String> favListSelector;

	private JButton btnVonListeEntfernen;

	private JLabel lblEinschrnkungen_1;
	private JButton addToFavList;

	private JLabel infoLabel;
	private JButton deleteCategory;

	private JLabel infoLabel2;
	/**
	 * Create the frame.
	 * @param con 
	 */
	public MainWindow() {
		setTitle("IMDB - Tool");
		init();
	}

	private void init() {
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 976, 746);

		tabPane = new JTabbedPane();

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		tabPane.addTab("Suchansicht" , contentPane);
		tabPane.addTab("DetailAnsicht", getDetailPanel());
		tabPane.addTab("Merkliste", getFavouriteListPanel());
		tabPane.setEnabledAt(1, false);
		setContentPane(tabPane);


		contentPane.setLayout(null);

		JLabel lblSuchfeld = new JLabel("Suchfeld:");
		lblSuchfeld.setBounds(12, 13, 87, 16);
		contentPane.add(lblSuchfeld);

		searchField = new JTextField();
		searchField.setBounds(111, 10, 196, 22);
		searchField.setColumns(10);
		contentPane.add(searchField);

		JLabel lblIchMchteSuchen = new JLabel("Ich m\u00F6chte suchen nach:");
		lblIchMchteSuchen.setBounds(539, 13, 175, 16);
		contentPane.add(lblIchMchteSuchen);

		modeSelector = new JComboBox<String>();
		modeSelector.setModel(new DefaultComboBoxModel<String>(new String[] {"Titel", "Company", "Person"}));
		modeSelector.setBounds(726, 10, 196, 22);
		contentPane.add(modeSelector);

		JLabel lblEinschrnkungen = new JLabel("Einschr\u00E4nkung (Typ1) hinzuf\u00FCgen: ");
		lblEinschrnkungen.setBounds(12, 81, 212, 16);
		contentPane.add(lblEinschrnkungen);

		constraintComboBox1 = new JComboBox<String>();
		constraintComboBox1.setModel(new DefaultComboBoxModel<String>(new String[]{"CompanyName", "CompanyType", "TitelType", "ProductionYear"}));
		constraintComboBox1.setBounds(229, 77, 160, 22);
		contentPane.add(constraintComboBox1);

		comparisonCombobox1 = new JComboBox<String>();
		comparisonCombobox1.setModel(new DefaultComboBoxModel(new String[] {"ist genau wie", "ist nicht wie", "enth\u00E4lt Teilstring"}));
		comparisonCombobox1.setBounds(401, 78, 115, 22);
		contentPane.add(comparisonCombobox1);

		textFieldConstraint1 = new JTextField();
		textFieldConstraint1.setBounds(528, 78, 212, 22);
		contentPane.add(textFieldConstraint1);
		textFieldConstraint1.setColumns(10);

		btnAddConstraint1 = new JButton("Add");
		btnAddConstraint1.setBounds(835, 77, 87, 25);
		contentPane.add(btnAddConstraint1);

		btnSucheStarten = new JButton("Suche starten");
		btnSucheStarten.setBounds(798, 291, 124, 25);
		contentPane.add(btnSucheStarten);

		listModel = new DefaultListModel<String>();
		listViewConstraints = new JList<String>(listModel);

		JScrollPane listScrollPane = new JScrollPane(listViewConstraints);
		listScrollPane.setBounds(31, 214, 605, 120);
		contentPane.add(listScrollPane);

		table = new JTable();

		mainTableScrollPane = new JScrollPane(getTable());
		mainTableScrollPane.setBounds(31, 385, 891, 284);
		contentPane.add(mainTableScrollPane);

		JLabel lblNewLabel = new JLabel("Einschr\u00E4nkung (Typ2) hinzuf\u00FCgen:");
		lblNewLabel.setBounds(12, 135, 212, 14);
		contentPane.add(lblNewLabel);

		textFieldConstraint2 = new JTextField();
		textFieldConstraint2.setBounds(229, 132, 160, 20);
		contentPane.add(textFieldConstraint2);
		textFieldConstraint2.setColumns(10);

		comparisonCombobox2 = new JComboBox<String>();
		comparisonCombobox2.setModel(new DefaultComboBoxModel(new String[] {"ist ein", "ist kein"}));
		comparisonCombobox2.setBounds(401, 132, 115, 20);
		contentPane.add(comparisonCombobox2);

		constraintComboBox2 = new JComboBox<String>();
		constraintComboBox2.setModel(new DefaultComboBoxModel<String>(new String[] {"Schauspieler /actor", "Schauspielerin /actress", "Produzent / producer", "Drehbuchautor / writer", "Kameramann / cinematographer", "Komponist / composer", "Kost\u00FCmdesigner / costume designer", "Regisseur / director", "Cutter / editor", "Filmcrew / miscellaneous crew", "Produktionsdesigner / production designer", "Gast / guest"}));
		constraintComboBox2.setBounds(528, 132, 212, 20);
		contentPane.add(constraintComboBox2);

		btnAddConstraint2 = new JButton("Add");
		btnAddConstraint2.setBounds(835, 131, 87, 23);
		contentPane.add(btnAddConstraint2);

		btnEinschrnkungEntfernen = new JButton("Einschr\u00E4nkung entfernen");
		btnEinschrnkungEntfernen.setBounds(30, 347, 194, 25);
		contentPane.add(btnEinschrnkungEntfernen);

		lblEinschrnkungen_1 = new JLabel("Gib mir Titel, f\u00FCr die gilt:");
		lblEinschrnkungen_1.setBounds(31, 185, 358, 16);
		contentPane.add(lblEinschrnkungen_1);

		constraint1AndOr = new JComboBox<String>();
		constraint1AndOr.setModel(new DefaultComboBoxModel<String>(new String[] {"AND"}));
		constraint1AndOr.setBounds(746, 78, 77, 22);
		contentPane.add(constraint1AndOr);

		constraint2AndOr = new JComboBox<String>();
		constraint2AndOr.setModel(new DefaultComboBoxModel<String>(new String[] {"AND"}));
		constraint2AndOr.setBounds(746, 131, 77, 22);
		contentPane.add(constraint2AndOr);
		
		infoLabel = new JLabel("\"Personennamen bitte mit 'Nachname, Vorname' angeben.\"");
		infoLabel.setForeground(Color.RED);
		infoLabel.setBounds(12, 42, 494, 16);
		contentPane.add(infoLabel);
		
		infoLabel2 = new JLabel("");
		infoLabel2.setBounds(401, 356, 521, 16);
		contentPane.add(infoLabel2);
	}

	/**
	 * building the 2nd Tab for the JTabbedPane <p>
	 * @return
	 * mainPanel - JPanel
	 */
	private JPanel getDetailPanel() {
		JPanel detailPanel = new JPanel();
		detailPanel.setLayout(null);

		detailTable = new JTable();

		JScrollPane detailScrollPane = new JScrollPane(detailTable);
		detailScrollPane.setBounds(77, 252, 777, 399);
		detailPanel.add(detailScrollPane);

		option1Label = new JLabel("Name");
		option1Label.setFont(new Font("Tahoma", Font.PLAIN, 18));
		option1Label.setBounds(77, 58, 777, 22);
		detailPanel.add(option1Label);

		option2Label = new JLabel("");
		option2Label.setFont(new Font("Tahoma", Font.PLAIN, 18));
		option2Label.setBounds(77, 92, 777, 22);
		detailPanel.add(option2Label);

		option3Label = new JLabel("");
		option3Label.setFont(new Font("Tahoma", Font.PLAIN, 18));
		option3Label.setBounds(77, 127, 777, 22);
		detailPanel.add(option3Label);

		detailTableTitle = new JLabel("");
		detailTableTitle.setFont(new Font("Tahoma", Font.PLAIN, 15));
		detailTableTitle.setBounds(77, 210, 777, 29);
		detailPanel.add(detailTableTitle);

		addToFavList = new JButton("Zu Merkliste hinzuf\u00FCgen");
		addToFavList.setBounds(657, 17, 197, 29);
		detailPanel.add(addToFavList);

		return detailPanel;
	}

	/**
	 * building the 3rd Tab for the JTabbedPane <p>
	 * @return
	 * favouriteListPanel - JPanel
	 */
	private JPanel getFavouriteListPanel() {
		JPanel favouriteListPanel = new JPanel();
		favouriteListPanel.setLayout(null);

		favouriteTablemodel = new DefaultTableModel();
		favouriteTable = new JTable(favouriteTablemodel);

		scrollPane_2 = new JScrollPane(favouriteTable);
		scrollPane_2.setBounds(24, 93, 896, 317);
		favouriteListPanel.add(scrollPane_2);
		
		favListSelector = new JComboBox<String>();
		favListSelector.setBounds(203, 16, 181, 18);
		favouriteListPanel.add(favListSelector);
		
		JLabel lblNewLabel_2 = new JLabel("MerkListe auswählen:");
		lblNewLabel_2.setBounds(24, 16, 167, 16);
		favouriteListPanel.add(lblNewLabel_2);
		
		btnVonListeEntfernen = new JButton("Von Liste entfernen");
		btnVonListeEntfernen.setBounds(753, 55, 167, 29);

		favouriteListPanel.add(btnVonListeEntfernen);
		
		deleteCategory = new JButton("Liste Löschen");
		deleteCategory.setBounds(753, 11, 167, 29);
		favouriteListPanel.add(deleteCategory);

		return favouriteListPanel;
	}
	public JButton getSearchButton(){
		return btnSucheStarten;
	}

	public String getTextFieldConstraint1Text() {
		return textFieldConstraint1.getText().toLowerCase(getLocale());
	}
	
	public JTextField getTextFieldConstraint2(){
		return textFieldConstraint2;
	}

	public JComboBox<String> getModeSelector() {
		return modeSelector;
	}

	public JTable getTable() {
		return table;
	}

	public String getSearchFieldText() {
		return searchField.getText().toLowerCase(getLocale());
	}

	public JButton getBtnAddConstraint1() {
		return btnAddConstraint1;
	}

	public JButton getBtnAddConstraint2() {
		return btnAddConstraint2;
	}

	public JComboBox<String> getComparisonCombobox2() {
		return comparisonCombobox2;
	}

	public JComboBox<String> getConstraintComboBox2() {
		return constraintComboBox2;
	}

	public JButton getBtnEinschrnkungEntfernen() {
		return btnEinschrnkungEntfernen;
	}

	public JComboBox<String> getConstraintComboBox1() {
		return constraintComboBox1;
	}

	public JComboBox<String> getComparisonCombobox1() {
		return comparisonCombobox1;
	}

	public DefaultListModel<String> getListModel() {
		return listModel;
	}

	public JList<String> getListViewConstraints() {
		return listViewConstraints;
	}

	public String getTextFieldConstraint2Text() {
		return textFieldConstraint2.getText().toLowerCase(getLocale());
	}

	public JComboBox<String> getConstraint1AndOr() {
		return constraint1AndOr;
	}

	public JLabel getOption1Label() {
		return option1Label;
	}

	public JLabel getOption2Label() {
		return option2Label;
	}

	public JLabel getOption3Label() {
		return option3Label;
	}

	public JTabbedPane getTabPane() {
		return tabPane;
	}

	public JTable getDetailTable() {
		return detailTable;
	}

	public JLabel getDetailTableTitle() {
		return detailTableTitle;
	}

	public DefaultTableModel getFavouriteTablemodel() {
		return favouriteTablemodel;
	}

	public JTable getFavouriteTable() {
		return favouriteTable;
	}

	public JComboBox<String> getConstraint2AndOr() {
		return constraint2AndOr;
	}

	public JComboBox<String> getFavListSelector() {
		return favListSelector;
	}

	public JButton getBtnVonListeEntfernen() {
		return btnVonListeEntfernen;
	}

	public JLabel getLblEinschrnkungen_1() {
		return lblEinschrnkungen_1;
	}

	public JButton getAddToFavList() {
		return addToFavList;
	}

	public JLabel getInfoLabel() {
		return infoLabel;
	}
	
	public JButton getDeleteCategoryBtn(){
		return deleteCategory;
	}

	public JLabel getInfoLabel2() {
		return infoLabel2;
	}

	public void setInfoLabel2(JLabel infoLabel2) {
		this.infoLabel2 = infoLabel2;
	}
}
