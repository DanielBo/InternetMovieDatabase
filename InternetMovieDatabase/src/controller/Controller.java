package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import main.Main;
import model.Constraint;
import view.AddToFavDialog;
import view.MainWindow;
import controller.builder.ConstraintBuilder;
import controller.builder.DetailStatementBuilder;
import controller.builder.QueryBuilder;
import controller.favourites.Favorites;

public class Controller {
	private int selectedMode = 0; // currently set searchmode
	private MainWindow mainWindow = null;
	private ConstraintBuilder consBuilder = null;
	private Connection con;
	private ArrayList<Constraint> constraints = new ArrayList<Constraint>();
	private Constraint lastConstraintType1 = null;
	private Constraint lastConstraintType2 = null;
	private Favorites favs;
	private JTable favTable;

	public Controller(Connection connection){
		this.con = connection;

		if (mainWindow == null)
			this.mainWindow = new MainWindow();

		if(consBuilder == null)
			this.consBuilder = new ConstraintBuilder();

		if (favs == null)
			this.favs = new Favorites(con);

		//Verbindet Buttons mit Aktionen.
		connectActions();
	}


	private void connectActions(){

		//Dieser ActionListener startet den Suchvorgang.
		mainWindow.getSearchButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {

						// Die Query wird erzeugt und an den StatementExecuter übergeben.
						String searchFieldText = mainWindow.getSearchFieldText();
						if (searchFieldText.contains("'")){
							JOptionPane.showMessageDialog(mainWindow,
									"Das Zeichen ' darf nicht Bestandteil der Suche sein.",
									"ERROR!",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						String query = new QueryBuilder(selectedMode, constraints, searchFieldText).getStatement();
						StatementExecuter stmtExe = new StatementExecuter(con, query);
						//----------------------------------------------------------------------


						try {
							if (Main.isDebug())
								System.out.println("Führe Anfrage aus.");

							// Die SQL-Abfrage wird an die Datenbank geshickt.
							ResultSet result = stmtExe.executeStatement();


							final JTable table = mainWindow.getTable();

							//MouseListener für das öffnen der Detailansicht.
							table.addMouseListener(new MouseListener() {

								@Override
								public void mouseReleased(MouseEvent e) {
									//do nothing
								}

								@Override
								public void mousePressed(MouseEvent e) {
									//do nothing
								}

								@Override
								public void mouseExited(MouseEvent e) {
									//do nothing									
								}

								@Override
								public void mouseEntered(MouseEvent e) {
									//do nothing
								}

								@Override
								public void mouseClicked(MouseEvent e) {
									if (e.getClickCount() == 2){
										if (Main.isDebug()){
											System.out.println("Doubleclick noticed on Row: " + table.getSelectedRow());
											System.out.println("The ID for " + table.getModel().getValueAt(table.getSelectedRow(), 1) + " is " + table.getModel().getValueAt(table.getSelectedRow(), 0)); // Selected MovieID
										}
										mainWindow.getTabPane().setEnabledAt(1, true);
										mainWindow.getTabPane().setSelectedIndex(1);

										selectedMode = mainWindow.getModeSelector().getSelectedIndex();	

										String id = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
										Main.setId(id);
										DetailStatementBuilder dtBuilder = new DetailStatementBuilder((selectedMode+1), Integer.valueOf(id), con, mainWindow);
										try {
											dtBuilder.executeStatement();
										} catch (SQLException e1) {
											mainWindow.getInfoLabel2().setText("Bei der Abfrage ist ein Fehler aufgetreten.");
											if(Main.isDebug())
												e1.printStackTrace();
										}

									}
								}
							});

							//Die Daten werden aus dem ResultSet geladen und dem Tablemodel der Ergebnistabelle übergeben
							if (Main.isDebug())
								System.out.println("Führe Metadatenabfrage aus.");
							ResultSetMetaData metaData = result.getMetaData();
							int columnNumber = metaData.getColumnCount();
							String[] columnNames = new String[columnNumber];

							if (Main.isDebug())
								System.out.println("Frage Tabellennamen ab.");
							for(int i = 1; i <= columnNumber; i++){
								columnNames[i-1] = metaData.getColumnName(i);
							}

							DefaultTableModel tModel = new DefaultTableModel(columnNames, 0){
								@Override
								public boolean isCellEditable(int row, int column) {
									return false;
								}
							};
							table.setModel(tModel);
							tModel.setColumnIdentifiers(columnNames);

							if (Main.isDebug())
								System.out.println("Fülle Tabelle auf.");

							// holt sich die Daten aus dem ResultSet
							while(result.next()){
								if (Main.isDebug())
									System.out.println("Next");
								Object[] objects = new Object[columnNumber]; // stellt einen Datensatz dar.


								for(int i = 1; i <= columnNumber; i++){
									objects[i-1] = result.getObject(i); // wir schreiben die id nicht mit in das ergebnis
								}
								tModel.addRow(objects);
							}

							// Die ID Spalte soll nicht angezeigt werden.
							TableColumn columnToRemove = table.getColumnModel().getColumn(0);
							table.getColumnModel().removeColumn(columnToRemove);

						} catch (SQLException e) {
							if(Main.isDebug())
								e.printStackTrace();
						}
					}
				});
			}
		});

		//-----------------ANFANG---Aktionen für die Merkliste---ANFANG--------------------------

		/* ChangeListener für die Tabs.
		 * initialisiert die Merklistenansicht, wenn die aufgerufen wird.
		 */
		mainWindow.getTabPane().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// Die Tabelle der Merkliste wird erst gefüllt, wenn das Merklistentab zum ersten mal aufgerufen wird.
				if(mainWindow.getTabPane().getSelectedIndex() == 2){
					initFavTab();
				}
			}

		});

		//Öffnet für den aktuellen Eintrag der Detailansicht den "AddToFavDialog" zum hinzufügen zur Merkliste.
		mainWindow.getAddToFavList().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final AddToFavDialog atfd = new AddToFavDialog(mainWindow,favs,Main.getId());
				final String id = Main.getId();

				//Fügt den aktuellen Eintrag zur aktuell ausgwählten Kategorie/Merkliste hinzu.
				atfd.getBtnHinzufgen().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (id != null)
							try {
								if(atfd.getFavCategorieComboBox().getSelectedItem() != null){
									String categorie = atfd.getFavCategorieComboBox().getSelectedItem().toString();
									favs.addIdToFavorites(id, categorie);
									atfd.getInfoLabel().setText("Eintrag hinzugefügt!");
								} else {
									atfd.getInfoLabel().setText("Es wurde keine Kategorie ausgewählt.");
								}
							} catch (SQLException e1) {
								if (00001 == e1.getErrorCode()){
									atfd.getInfoLabel().setText("Eintrag schon vorhanden!");  					
								}
							}
					}
				});

				//Legt eine neue Merkliste mit angegebenem Namen an.
				atfd.getBtnErtstellen().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						atfd.getFavCategorieComboBox().addItem(atfd.getTextField().getText());
						atfd.getInfoLabel().setText("Kategorie hinzugefügt!");
					}
				});

				//Holt die verfügbaren Merklisten aus der Datenbank.
				try {
					ArrayList<String> listCategories = favs.getCategories();
					String[] categories = new String[listCategories.size()];
					favs.getCategories().toArray(categories);
					atfd.getFavCategorieComboBox().setModel(new DefaultComboBoxModel<String>(categories));
				} catch (SQLException e1) {
					if(Main.isDebug())
						e1.printStackTrace();
				}
			}
		});

		favTable = mainWindow.getFavouriteTable();
		final JComboBox<String> favListSelector = mainWindow.getFavListSelector(); // Selected Favorite Category

		//ItemLisenter zum Auswählen der Kategorie in der Merkliste
		favListSelector.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED){
					if (Main.isDebug())
						System.out.println(e.getItem() + " ");
					try {
						reloadTableContents((String)e.getItem());
					} catch (SQLException e1) {
						if(Main.isDebug())
							e1.printStackTrace();
					}

				}
			}
		});

		//Aktion zum Entfernen von Titeln aus der Merkliste.
		mainWindow.getBtnVonListeEntfernen().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (favTable.getSelectedRow() != -1) {
					String valueAt = favTable.getModel().getValueAt(favTable.getSelectedRow(), 0).toString();

					if (Main.isDebug())
						System.out.println(valueAt);

					favs.removeIdFromFavorites(valueAt,(String)favListSelector.getSelectedItem());
					mainWindow.getFavouriteTable().setModel(new DefaultTableModel());
					initFavTab();
				}
			}
		});// remove selected from table

		//Aktion zum Löschen der aktuellen Merkliste.
		mainWindow.getDeleteCategoryBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				favs.removeCatFromFavorites((String)favListSelector.getSelectedItem());
				mainWindow.getFavouriteTable().setModel(new DefaultTableModel());
				initFavTab();
			}
		});


		//MouseListener für die Merklistentabelle
		favTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				//do nothing
			}

			@Override
			public void mousePressed(MouseEvent e) {
				//do nothing
			}

			@Override
			public void mouseExited(MouseEvent e) {
				//do nothing									
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				//do nothing
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2){
					if (Main.isDebug()){
						System.out.println("Doubleclick noticed on Row: " + favTable.getSelectedRow());
						System.out.println("The ID for " + favTable.getModel().getValueAt(favTable.getSelectedRow(), 1) + " is " + favTable.getModel().getValueAt(favTable.getSelectedRow(), 0)); // Selected MovieID
					}
					mainWindow.getTabPane().setSelectedIndex(1);

					selectedMode = mainWindow.getModeSelector().getSelectedIndex();	

					DetailStatementBuilder dtBuilder = new DetailStatementBuilder((selectedMode+1), Integer.valueOf(favTable.getModel().getValueAt(favTable.getSelectedRow(), 0).toString()), con, mainWindow);
					try {
						dtBuilder.executeStatement();
					} catch (SQLException e1) {
						if(Main.isDebug())
							e1.printStackTrace();
					}

				}
			}
		});

		//-----------------------------ENDE---Aktionen für die Merkliste---ENDE-------------------------------------

		//---------------ANFANG---Aktionen für das Hinzufügen und Entfernen von Einschränkungen---ANFANG------------

		// ActionListener für das Hinzufügen von Constraints des Typ 1
		mainWindow.getBtnAddConstraint1().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent) {
				Constraint constraint = null;

				/* Entweder wird an eine bestehendes Constraint mit oder ein weiteres angehängt ODER 
				 * Es wird ein einzelnes Constraint erzeugt, dass später per "AND" mit weiteren Constraints verbunden wird.
				 */
				if(mainWindow.getConstraint1AndOr().getSelectedIndex() == 1 && lastConstraintType1 != null){
					constraint = consBuilder.createORConstraintType1(mainWindow.getConstraintComboBox1(), mainWindow.getComparisonCombobox1(), mainWindow.getTextFieldConstraint1Text(), lastConstraintType1);

					// Letztes Constraint wird gelöscht und weiter unten durch das Neue ersetzt.
					constraints.remove(lastConstraintType1);
					DefaultListModel<String> listModel = mainWindow.getListModel();
					listModel.removeElementAt(listModel.size() - 1);
				} else {
					constraint = consBuilder.createConstraintType1(mainWindow.getConstraintComboBox1(), mainWindow.getComparisonCombobox1(), mainWindow.getTextFieldConstraint1Text());
				}

				//Das Constraint wird zur ArrayList "constraints" und zur listView in MainWindow hinzugefügt.
				DefaultListModel<String> listModel = mainWindow.getListModel();
				listModel.addElement(constraint.getStatementName());
				constraints.add(constraint);
				lastConstraintType1 = constraint;
				mainWindow.getConstraint1AndOr().setModel(new DefaultComboBoxModel<String>(new String[] {"AND", "OR"}));
				mainWindow.getConstraint2AndOr().setModel(new DefaultComboBoxModel<String>(new String[] {"AND"}));
			}
		});

		// ActionListener für das Hinzufügen von Constraints des Typ 2
		mainWindow.getBtnAddConstraint2().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent) {
				Constraint constraint = null;

				/* Entweder wird an eine bestehendes Constraint mit oder ein weiteres angehängt ODER 
				 * Es wird ein einzelnes Constraint erzeugt, dass später per "AND" mit weiteren Constraints verbunden wird.
				 */
				if(mainWindow.getConstraint2AndOr().getSelectedIndex() == 1 && lastConstraintType2 != null){
					constraint = consBuilder.createORConstraintType2(mainWindow.getTextFieldConstraint2Text(), mainWindow.getComparisonCombobox2(), mainWindow.getConstraintComboBox2(), lastConstraintType2);

					// Letztes Constraint wird gelöscht und weiter unten durch das Neue ersetzt.
					constraints.remove(lastConstraintType2);
					DefaultListModel<String> listModel = mainWindow.getListModel();
					listModel.removeElementAt(listModel.size() - 1);
				} else {
					constraint = consBuilder.createConstraintType2(mainWindow.getTextFieldConstraint2Text(), mainWindow.getComparisonCombobox2(), mainWindow.getConstraintComboBox2());
				}

				//Das Constraint wird zur ArrayList "constraints" und zur listView in MainWindow hinzugefügt.
				DefaultListModel<String> listModel = mainWindow.getListModel();
				listModel.addElement(constraint.getStatementName());
				constraints.add(constraint);
				lastConstraintType2 = constraint;
				mainWindow.getConstraint2AndOr().setModel(new DefaultComboBoxModel(new String[] {"AND", "OR"}));
			}
		});

		// Entfernt das in der "listView" ausgewählte Constraint.
		mainWindow.getBtnEinschrnkungEntfernen().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int constraintId = mainWindow.getListViewConstraints().getSelectedIndex();
						if (Main.isDebug())
							System.out.println(constraintId);
						DefaultListModel<String> listModel = mainWindow.getListModel();
						listModel.removeElementAt(constraintId);
						constraints.remove(constraintId);
						if (Main.isDebug())
							System.out.println(constraints);

						mainWindow.getConstraint1AndOr().setModel(new DefaultComboBoxModel(new String[] {"AND"}));
						mainWindow.getConstraint2AndOr().setModel(new DefaultComboBoxModel(new String[] {"AND"}));
					}
				});
			}
		});

		/* Wird die Auswahl der Constraint1Combobox verändert, lässt sich als Verbindungsoperator nur noch AND auswählen
		 * Erst wenn eine Constraint vom Typ 1 hinzugefügt wird, lässt sich auch OR auswählen. (Siehe weiter Oben)
		 */
		mainWindow.getConstraintComboBox1().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow.getConstraint1AndOr().setModel(new DefaultComboBoxModel(new String[] {"AND"}));
			}
		});

		//-------------ENDE---Aktionen für das Hinzufügen und Entfernen von Einschränkungen---ENDE-----------------


		/* ActionListener der ModeSelector Combobox, mit der man den Suchmodus auswählt,
		 * also (Titel, Company oder Person).
		 * Je nach Auswahl werden die Auswahlmöglichkeiten für das Constraint vom Typ 1 verändert.
		 */
		mainWindow.getModeSelector().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				constraints.clear();
				DefaultListModel<String> listModel = mainWindow.getListModel();
				listModel.clear();

				selectedMode = mainWindow.getModeSelector().getSelectedIndex();	
				String[] choice;

				// Auswahlmöglichkeiten für die erste Combobox der Einschr�nkung vom Typ 1
				switch (selectedMode){
				case 0:
					//Titel
					choice = new String[]{"CompanyName", "CompanyType", "TitelType", "ProductionYear"};
					mainWindow.getLblEinschrnkungen_1().setText("Gib mir Titel, für die gilt:");

					enableConstraintType2(true);
					break;
				case 1:
					// Company
					choice = new String[]{"Titel", "TitelType", "CompanyType", "ProductionYear"};
					mainWindow.getLblEinschrnkungen_1().setText("Gib mir Unternehmen, für die gilt:");
					enableConstraintType2(true);
					break;
				case 2:
					//Person
					choice = new String[]{"RollenName", "RollenType", "Titel", "TitelType"};
					mainWindow.getLblEinschrnkungen_1().setText("Gib mir Personen, für die gilt:");
					enableConstraintType2(false);
					break;
				default:
					throw new RuntimeException("Wrong mode");
				}
				mainWindow.getConstraintComboBox1().setModel(new DefaultComboBoxModel(choice));
			}
		});

	}

	//------------Weitere Methoden für Controller----------------

	// Aktiviert und Deaktiviert die Bedienelemente für Einschränkungen vom Typ 2
	private void enableConstraintType2(boolean value){
		mainWindow.getTextFieldConstraint2().setEnabled(value);
		mainWindow.getComparisonCombobox2().setEnabled(value);
		mainWindow.getConstraintComboBox2().setEnabled(value);
		mainWindow.getConstraint2AndOr().setEnabled(value);
		mainWindow.getBtnAddConstraint2().setEnabled(value);
	}

	//Initialisiert die Elemente der Merklistenansicht.
	private void initFavTab() {
		JComboBox<String> favListSelector = mainWindow.getFavListSelector();
		ArrayList<String> categories = null;

		try {
			categories = favs.getCategories();
			favListSelector.removeAllItems();

			if (categories != null){
				if(categories.size() > 0)
					reloadTableContents(categories.get(0));
				for (String s : categories){
					favListSelector.addItem(s);
				}
			}


		} catch (SQLException e) {
			if(Main.isDebug())
				e.printStackTrace();
		}
	}

	//Füllt die Merkliste mit Inhalt aus der Datenbank
	private void reloadTableContents(String category) throws SQLException{
		ResultSet result = favs.getFavByCategory(category);
		ArrayList<String> results = new ArrayList<String>();

		while (result.next()){
			results.add(result.getString(1));
		}

		DefaultTableModel model = null;

		model = new DefaultTableModel(new String[]{"Titel", "Typ", "Produktionsjahr"},0) {
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
		};
		favTable.setModel(model);

		for ( String s : results ){
			String query = "Select IMDB.title.id, IMDB.title.title, IMDB.kind_type.kind Typ, IMDB.title.production_year From IMDB.title" +
					" join IMDB.kind_type on imdb.title.kind_id = imdb.kind_type.id Where imdb.title.id = " + s;
			if (Main.isDebug())
				System.out.println(query);
			ResultSet result2 = con.createStatement().executeQuery(query);
			int columnCount = result2.getMetaData().getColumnCount();

			Object[] newRow = null;

			while (result2.next()){
				newRow = new Object[columnCount];
				for (int i = 1; i <= columnCount; i++){
					newRow[i-1] = result2.getObject(i);
				}
			}

			if (Main.isDebug())
				System.out.println("adding new row to favTable");

			model.addRow(newRow);
		}
		TableColumn columnToRemove = favTable.getColumnModel().getColumn(0);
		favTable.getColumnModel().removeColumn(columnToRemove);


	}

}
