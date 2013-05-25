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
import javax.swing.JTable;
import javax.swing.SwingUtilities;
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
	Constraint lastConstraintType1 = null;
	Constraint lastConstraintType2 = null;
	private Favorites favs;
	private JTable favTable;

	public Controller(Connection connection){
		this.con = connection;
		if (mainWindow == null)
			this.mainWindow = new MainWindow(con);

		if(consBuilder == null)
			this.consBuilder = new ConstraintBuilder();

		if (favs == null)
			this.favs = new Favorites(con);
		connectActions();
		initFavTab();
	}

	private void initFavTab() {
		JComboBox<String> favListSelector = mainWindow.getFavListSelector();
		ArrayList<String> categories = null;

		try {
			categories = favs.getCategories();
			favListSelector.removeAllItems();

			if (categories != null)
				for (String s : categories)
					favListSelector.addItem(s);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void connectActions(){

		mainWindow.getSearchButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						String query = new QueryBuilder(selectedMode, constraints, mainWindow.getSearchField().getText()).getStatement();
						StatementExecuter stmtExe = new StatementExecuter(con, query);
						try {
							System.out.println("F�hre Anfrage aus.");
							ResultSet result = stmtExe.executeStatement();
							final JTable table = mainWindow.getTable();

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
										System.out.println("Doubleclick noticed on Row: " + table.getSelectedRow());
										System.out.println("The ID for " + table.getModel().getValueAt(table.getSelectedRow(), 1) + " is " + table.getModel().getValueAt(table.getSelectedRow(), 0)); // Selected MovieID
										mainWindow.getTabPane().setSelectedIndex(1);

										selectedMode = mainWindow.getModeSelector().getSelectedIndex();	

										String id = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
										Main.setId(id);
										DetailStatementBuilder dtBuilder = new DetailStatementBuilder((selectedMode+1), Integer.valueOf(id), con, mainWindow);
										try {
											dtBuilder.executeStatement();
										} catch (SQLException e1) {
											e1.printStackTrace();
										}

									}
								}
							});

							System.out.println("Führe Metadatenabfrage aus.");
							ResultSetMetaData metaData = result.getMetaData();
							int columnNumber = metaData.getColumnCount();
							String[] columnNames = new String[columnNumber];

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

							System.out.println("Fülle Tabelle auf.");

							// holt sich die Daten aus dem ResultSet
							while(result.next()){
								System.out.println("Next");
								Object[] objects = new Object[columnNumber]; // stellt einen Datensatz dar.


								for(int i = 1; i <= columnNumber; i++){
									objects[i-1] = result.getObject(i); // wir schreiben die id nicht mit in das ergebnis
								}

								tModel.addRow(objects);
							}

							TableColumn columnToRemove = table.getColumnModel().getColumn(0);
							table.getColumnModel().removeColumn(columnToRemove);

							//							TableColumn column0afterRemove = table.getColumnModel().getColumn(0);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});

		mainWindow.getAddToFavList().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddToFavDialog atfd = new AddToFavDialog(mainWindow,favs,Main.getId());
			}
		});

		favTable = mainWindow.getFavouriteTable();
		JComboBox<String> favListSelector = mainWindow.getFavListSelector(); // Selected Favorite Category

		favListSelector.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED){
					System.out.println(e.getItem() + " ");
					try {
						reloadTableContents((String)e.getItem());
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				}
			}
		});


		mainWindow.getBtnVonListeEntfernen().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (favTable.getSelectedRow() != -1)
					favs.removeIdFromFavorites((String)favTable.getModel().getValueAt(favTable.getSelectedRow(), 0));
			}
		});// remove selected from table

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
					System.out.println("Doubleclick noticed on Row: " + favTable.getSelectedRow());
					System.out.println("The ID for " + favTable.getModel().getValueAt(favTable.getSelectedRow(), 1) + " is " + favTable.getModel().getValueAt(favTable.getSelectedRow(), 0)); // Selected MovieID
					mainWindow.getTabPane().setSelectedIndex(1);

					selectedMode = mainWindow.getModeSelector().getSelectedIndex();	

					DetailStatementBuilder dtBuilder = new DetailStatementBuilder((selectedMode+1), Integer.valueOf(favTable.getModel().getValueAt(favTable.getSelectedRow(), 0).toString()), con, mainWindow);
					try {
						dtBuilder.executeStatement();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				}
			}
		});

		// ActionListener f�r das Hinzuf�gen von Constraints des Typ 1
		mainWindow.getBtnAddConstraint1().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent) {
				Constraint constraint = null;

				/* Entweder wird an eine bestehendes Constraint mit oder ein weiteres angeh�ngt ODER 
				 * Es wird ein einzelnes Constraint erzeugt, dass sp�ter per "AND" mit weiteren Constraints verbunden wird.
				 */
				if(mainWindow.getConstraint1AndOr().getSelectedIndex() == 1 && lastConstraintType1 != null){
					constraint = consBuilder.createORConstraintType1(mainWindow.getConstraintComboBox1(), mainWindow.getComparisonCombobox1(), mainWindow.getTextFieldConstraint1(), lastConstraintType1);

					// Letztes Constraint wird gel�scht und weiter unten durch das Neue ersetzt.
					constraints.remove(lastConstraintType1);
					DefaultListModel<String> listModel = mainWindow.getListModel();
					listModel.removeElementAt(listModel.size() - 1);
				} else {
					constraint = consBuilder.createConstraintType1(mainWindow.getConstraintComboBox1(), mainWindow.getComparisonCombobox1(), mainWindow.getTextFieldConstraint1());
				}

				//Das Constraint wird zur ArrayList "constraints" und zur listView in MainWindow hinzugef�gt.
				DefaultListModel<String> listModel = mainWindow.getListModel();
				listModel.addElement(constraint.getStatementName());
				constraints.add(constraint);
				lastConstraintType1 = constraint;
				mainWindow.getConstraint1AndOr().setModel(new DefaultComboBoxModel<String>(new String[] {"AND", "OR"}));
				mainWindow.getConstraint2AndOr().setModel(new DefaultComboBoxModel<String>(new String[] {"AND"}));
			}
		});

		// ActionListener f�r das Hinzuf�gen von Constraints des Typ 2
		mainWindow.getBtnAddConstraint2().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent) {
				Constraint constraint = null;

				/* Entweder wird an eine bestehendes Constraint mit oder ein weiteres angeh�ngt ODER 
				 * Es wird ein einzelnes Constraint erzeugt, dass sp�ter per "AND" mit weiteren Constraints verbunden wird.
				 */
				if(mainWindow.getConstraint2AndOr().getSelectedIndex() == 1 && lastConstraintType2 != null){
					constraint = consBuilder.createORConstraintType2(mainWindow.getTextFieldConstraint2(), mainWindow.getComparisonCombobox2(), mainWindow.getConstraintComboBox2(), lastConstraintType2);

					// Letztes Constraint wird gel�scht und weiter unten durch das Neue ersetzt.
					constraints.remove(lastConstraintType2);
					DefaultListModel<String> listModel = mainWindow.getListModel();
					listModel.removeElementAt(listModel.size() - 1);
				} else {
					constraint = consBuilder.createConstraintType2(mainWindow.getTextFieldConstraint2(), mainWindow.getComparisonCombobox2(), mainWindow.getConstraintComboBox2());
				}

				//Das Constraint wird zur ArrayList "constraints" und zur listView in MainWindow hinzugef�gt.
				DefaultListModel<String> listModel = mainWindow.getListModel();
				listModel.addElement(constraint.getStatementName());
				constraints.add(constraint);
				lastConstraintType2 = constraint;
				mainWindow.getConstraint2AndOr().setModel(new DefaultComboBoxModel(new String[] {"AND", "OR"}));
			}
		});

		// Entfernt das in der listView ausgew�hlte Constraint.
		mainWindow.getBtnEinschrnkungEntfernen().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int constraintId = mainWindow.getListViewConstraints().getSelectedIndex();
						System.out.println(constraintId);
						DefaultListModel<String> listModel = mainWindow.getListModel();
						listModel.removeElementAt(constraintId);
						constraints.remove(constraintId);
						System.out.println(constraints);
					}
				});
			}
		});

		/* ActionListener der ModeSelector Combobox, mit der man den Suchmodus ausw�hlt,
		 * also (Titel, Company oder Person).
		 * Je nach Auswahl werden die Auswahlm�glichkeiten f�r das Constraint vom Typ 1 ver�ndert.
		 */
		mainWindow.getModeSelector().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				constraints.clear();
				DefaultListModel<String> listModel = mainWindow.getListModel();
				listModel.clear();

				selectedMode = mainWindow.getModeSelector().getSelectedIndex();	
				String[] choice;

				// Auswahlm�glichkeiten f�r die erste Combobox der Einschr�nkung vom Typ 1
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

		/* Wird die Auswahl der Constraint1Combobox ver�ndert, l�sst sich als Verbindungsoperator nur noch AND ausw�hlen
		 * Erst wenn eine Constraint vom Typ 1 hinzugef�gt wird, l�sst sich auch OR ausw�hlen. (Siehe weiter Oben)
		 */
		mainWindow.getConstraintComboBox1().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow.getConstraint1AndOr().setModel(new DefaultComboBoxModel(new String[] {"AND"}));
			}
		});
	}

	private void enableConstraintType2(boolean value){
		mainWindow.getTextFieldConstraint2().setEnabled(value);
		mainWindow.getComparisonCombobox2().setEnabled(value);
		mainWindow.getConstraintComboBox2().setEnabled(value);
		mainWindow.getConstraint2AndOr().setEnabled(value);
		mainWindow.getBtnAddConstraint2().setEnabled(value);
	}

	protected void reloadTableContents(String category) throws SQLException{
		ResultSet result = favs.getFavByCategory(category);
		ArrayList<String> results = new ArrayList<String>();

		while (result.next()){
			results.add(result.getString(1));
		}

		DefaultTableModel model = null;
		boolean headerSet = false;

		for ( String s : results ){
			ResultSet result2 = con.createStatement().executeQuery("Select IMDB.title.title, IMDB.kind_type.kind Typ, IMDB.title.production_year From IMDB.title Where imdb.title.id = " + s);
			int columnCount = result2.getMetaData().getColumnCount();

			if (!headerSet){
				String[] columnNames = new String[columnCount];
				for (int i = 1; i < columnCount; i++){
					columnNames[i-1] = result2.getMetaData().getColumnLabel(i);
				}
				model = new DefaultTableModel(columnNames,0) {
					@Override
					public boolean isCellEditable(int rowIndex, int columnIndex) {
						return false;
					}
				};
				favTable.setModel(model);
			}

			Object[] newRow = null;

			while (result2.next()){
				newRow = new Object[columnCount];
				for (int i = 1; i < columnCount; i++){
					newRow[i-1] = result.getObject(i);
				}
			}

			if (Main.isDebug())
				System.out.println("adding new row to favTable");
			model.addRow(newRow);
		}


	}

}
