package View;

import java.sql.Connection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ListSelectionModel;
import javax.swing.Box;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;

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
	/**
	 * Create the frame.
	 * @param con 
	 */
	public MainWindow(Connection con) {
		init();
		this.con = con;
		
	}

	private void init() {
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 976, 728);
		
		tabPane = new JTabbedPane();
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		tabPane.addTab("Suchansicht" , contentPane);
		tabPane.addTab("DetailAnsicht", getDetailPanel());
		tabPane.addTab("Merkliste", getFavouriteListPanel());
		setContentPane(tabPane);
		
		
		contentPane.setLayout(null);
		
		JLabel lblSuchfeld = new JLabel("Suchfeld:");
		lblSuchfeld.setBounds(12, 13, 87, 16);
		contentPane.add(lblSuchfeld);
		
		setSearchField(new JTextField());
		getSearchField().setBounds(111, 10, 238, 22);
		contentPane.add(getSearchField());
		getSearchField().setColumns(10);
		
		JLabel lblIchMchteSuchen = new JLabel("Ich m\u00F6chte suchen nach:");
		lblIchMchteSuchen.setBounds(518, 13, 196, 16);
		contentPane.add(lblIchMchteSuchen);
		
		setModeSelector(new JComboBox<String>());
		getModeSelector().setModel(new DefaultComboBoxModel<String>(new String[] {"Titel", "Company", "Person"}));
		getModeSelector().setBounds(726, 10, 196, 22);
		contentPane.add(getModeSelector());
		
		JLabel lblEinschrnkungen = new JLabel("Einschr\u00E4nkung (Typ1) hinzuf\u00FCgen: ");
		lblEinschrnkungen.setBounds(12, 49, 226, 16);
		contentPane.add(lblEinschrnkungen);
		
		constraintComboBox1 = new JComboBox<String>();
		constraintComboBox1.setModel(new DefaultComboBoxModel(new String[]{"CompanyName", "CompanyType", "TitelType", "ProductionYear"}));
		constraintComboBox1.setBounds(250, 46, 160, 22);
		contentPane.add(constraintComboBox1);
		
		comparisonCombobox1 = new JComboBox<String>();
		comparisonCombobox1.setModel(new DefaultComboBoxModel<String>(new String[] {"==", "!=", "Like"}));
		comparisonCombobox1.setBounds(422, 46, 79, 22);
		contentPane.add(comparisonCombobox1);
		
		textFieldConstraint1 = new JTextField();
		textFieldConstraint1.setBounds(518, 46, 212, 22);
		contentPane.add(textFieldConstraint1);
		textFieldConstraint1.setColumns(10);
		
		btnAddConstraint1 = new JButton("Add");
		btnAddConstraint1.setBounds(825, 45, 97, 25);
		contentPane.add(btnAddConstraint1);
		
		btnSucheStarten = new JButton("Suche starten");
		btnSucheStarten.setBounds(750, 271, 124, 25);
		contentPane.add(btnSucheStarten);
		
		listModel = new DefaultListModel<String>();
		listViewConstraints = new JList<String>(listModel);
		
		JScrollPane listScrollPane = new JScrollPane(listViewConstraints);
		listScrollPane.setBounds(31, 176, 605, 120);
		contentPane.add(listScrollPane);
		
		setTable(new JTable());
		
		mainTableScrollPane = new JScrollPane(getTable());
		mainTableScrollPane.setBounds(31, 359, 891, 292);
		contentPane.add(mainTableScrollPane);
		
		JLabel lblNewLabel = new JLabel("Einschr\u00E4nkung (Typ2) hinzuf\u00FCgen:");
		lblNewLabel.setBounds(12, 103, 226, 14);
		contentPane.add(lblNewLabel);
		
		textFieldConstraint2 = new JTextField();
		textFieldConstraint2.setBounds(250, 100, 160, 20);
		contentPane.add(textFieldConstraint2);
		textFieldConstraint2.setColumns(10);
		
		comparisonCombobox2 = new JComboBox<String>();
		comparisonCombobox2.setModel(new DefaultComboBoxModel<String>(new String[] {"==", "!="}));
		comparisonCombobox2.setBounds(422, 100, 79, 20);
		contentPane.add(comparisonCombobox2);
		
		constraintComboBox2 = new JComboBox<String>();
		constraintComboBox2.setModel(new DefaultComboBoxModel<String>(new String[] {"Schauspieler /actor", "Schauspielerin /actress", "Produzent / producer", "Drehbuchautor / writer", "Kameramann / cinematographer", "Komponist / composer", "Kost\u00FCmdesigner / costume designer", "Regisseur / director", "Cutter / editor", "Filmcrew / miscellaneous crew", "Produktionsdesigner / production designer", "Gast / guest"}));
		constraintComboBox2.setBounds(518, 100, 212, 20);
		contentPane.add(constraintComboBox2);
		
		btnAddConstraint2 = new JButton("Add");
		btnAddConstraint2.setBounds(825, 99, 97, 23);
		contentPane.add(btnAddConstraint2);
		
		btnEinschrnkungEntfernen = new JButton("Einschr\u00E4nkung entfernen");
		btnEinschrnkungEntfernen.setBounds(31, 309, 194, 25);
		contentPane.add(btnEinschrnkungEntfernen);
		
		JLabel lblEinschrnkungen_1 = new JLabel("Einschr\u00E4nkungen:");
		lblEinschrnkungen_1.setBounds(31, 147, 173, 16);
		contentPane.add(lblEinschrnkungen_1);
		
		constraint1AndOr = new JComboBox<String>();
		constraint1AndOr.setModel(new DefaultComboBoxModel(new String[] {"AND"}));
		constraint1AndOr.setBounds(736, 46, 77, 22);
		contentPane.add(constraint1AndOr);
		
		constraint2AndOr = new JComboBox<String>();
		constraint2AndOr.setModel(new DefaultComboBoxModel(new String[] {"AND"}));
		constraint2AndOr.setBounds(736, 99, 77, 22);
		contentPane.add(constraint2AndOr);
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
		option1Label.setBounds(77, 57, 777, 22);
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

		return detailPanel;
	}
	
	/**
	 * building the 3nd Tab for the JTabbedPane <p>
	 * @return
	 * favouriteListPanel - JPanel
	 */
	private JPanel getFavouriteListPanel() {
		JPanel favouriteListPanel = new JPanel();
		favouriteListPanel.setLayout(null);
		
		favouriteTablemodel = new DefaultTableModel();
		favouriteTable = new JTable(favouriteTablemodel);
		
		scrollPane_2 = new JScrollPane(favouriteTable);
		scrollPane_2.setBounds(29, 30, 896, 317);
		favouriteListPanel.add(scrollPane_2);
		
		return favouriteListPanel;
	}

	public JTextField getTextFieldConstraint1() {
		return textFieldConstraint1;
	}

	private void setTextFieldConstraint1(JTextField jTextField) {
		textFieldConstraint1 = jTextField;
		
	}

	public JButton getSearchButton(){
		return btnSucheStarten;
	}

	public JComboBox<String> getModeSelector() {
		return modeSelector;
	}

	private void setModeSelector(JComboBox<String> modeSelector) {
		this.modeSelector = modeSelector;
	}

	public JTable getTable() {
		return table;
	}

	private void setTable(JTable table) {
		this.table = table;
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public JTextField getSearchField() {
		return searchField;
	}

	public void setSearchField(JTextField searchField) {
		this.searchField = searchField;
	}

	public JButton getBtnAddConstraint1() {
		return btnAddConstraint1;
	}

	private void setBtnAddConstraint1(JButton btnAddConstraint1) {
		this.btnAddConstraint1 = btnAddConstraint1;
	}

	public JButton getBtnAddConstraint2() {
		return btnAddConstraint2;
	}

	private void setBtnAddConstraint2(JButton btnAddConstraint2) {
		this.btnAddConstraint2 = btnAddConstraint2;
	}

	public JComboBox<String> getComparisonCombobox2() {
		return comparisonCombobox2;
	}

	private void setComparisonCombobox2(JComboBox<String> comparisonCombobox2) {
		this.comparisonCombobox2 = comparisonCombobox2;
	}

	public JComboBox<String> getConstraintComboBox2() {
		return constraintComboBox2;
	}

	private void setConstraintComboBox2(JComboBox<String> constraintComboBox2) {
		this.constraintComboBox2 = constraintComboBox2;
	}

	public JButton getBtnEinschrnkungEntfernen() {
		return btnEinschrnkungEntfernen;
	}

	private void setBtnEinschrnkungEntfernen(JButton btnEinschrnkungEntfernen) {
		this.btnEinschrnkungEntfernen = btnEinschrnkungEntfernen;
	}

	public JComboBox<String> getConstraintComboBox1() {
		return constraintComboBox1;
	}

	private void setConstraintComboBox1(JComboBox<String> constraintComboBox1) {
		this.constraintComboBox1 = constraintComboBox1;
	}

	public JComboBox<String> getComparisonCombobox1() {
		return comparisonCombobox1;
	}

	public void setComparisonCombobox1(JComboBox<String> comparisonCombobox1) {
		this.comparisonCombobox1 = comparisonCombobox1;
	}

	public DefaultListModel<String> getListModel() {
		return listModel;
	}

	private void setListModel(DefaultListModel<String> listModel) {
		this.listModel = listModel;
	}

	public JList<String> getListViewConstraints() {
		return listViewConstraints;
	}

	public void setListViewConstraints(JList<String> listViewConstraints) {
		this.listViewConstraints = listViewConstraints;
	}

	public JTextField getTextFieldConstraint2() {
		return textFieldConstraint2;
	}

	public void setTextFieldConstraint2(JTextField textFieldConstraint2) {
		this.textFieldConstraint2 = textFieldConstraint2;
	}

	public JComboBox<String> getConstraint1AndOr() {
		return constraint1AndOr;
	}

	public void setConstraint1AndOr(JComboBox<String> constraint1AndOr) {
		this.constraint1AndOr = constraint1AndOr;
	}

	public JLabel getOption1Label() {
		return option1Label;
	}

	public void setOption1Label(JLabel option1Label) {
		this.option1Label = option1Label;
	}

	public JLabel getOption2Label() {
		return option2Label;
	}

	public void setOption2Label(JLabel option2Label) {
		this.option2Label = option2Label;
	}

	public JLabel getOption3Label() {
		return option3Label;
	}

	public void setOption3Label(JLabel option3Label) {
		this.option3Label = option3Label;
	}

	public JTabbedPane getTabPane() {
		return tabPane;
	}

	public void setTabPane(JTabbedPane tabPane) {
		this.tabPane = tabPane;
	}

	public JTable getDetailTable() {
		return detailTable;
	}

	public void setDetailTable(JTable detailTable) {
		this.detailTable = detailTable;
	}

	public JLabel getDetailTableTitle() {
		return detailTableTitle;
	}

	public void setDetailTableTitle(JLabel detailTableTitle) {
		this.detailTableTitle = detailTableTitle;
	}

	public DefaultTableModel getFavouriteTablemodel() {
		return favouriteTablemodel;
	}

	public void setFavouriteTablemodel(DefaultTableModel favouriteTablemodel) {
		this.favouriteTablemodel = favouriteTablemodel;
	}

	public JTable getFavouriteTable() {
		return favouriteTable;
	}

	public void setFavouriteTable(JTable favouriteTable) {
		this.favouriteTable = favouriteTable;
	}

	public JComboBox<String> getConstraint2AndOr() {
		return constraint2AndOr;
	}

	public void setConstraint2AndOr(JComboBox<String> constraint2AndOr) {
		this.constraint2AndOr = constraint2AndOr;
	}
}
