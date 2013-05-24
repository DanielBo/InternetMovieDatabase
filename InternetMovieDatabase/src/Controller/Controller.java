package Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import Controller.builder.ConstraintBuilder;
import Controller.builder.DetailStatementBuilder;
import Controller.builder.QueryBuilder;

import Model.Constraint;
import View.MainWindow;

public class Controller {
	private int selectedMode = 0; // currently set searchmode
	private MainWindow mainWindow = null;
	private ConstraintBuilder consBuilder = null;
	private Connection con;
	private ArrayList<Constraint> constraints = new ArrayList<Constraint>();
	Constraint lastConstraintType1 = null;
	Constraint lastConstraintType2 = null;

	public Controller(Connection connection){
		this.con = connection;
		if (mainWindow == null)
			this.mainWindow = new MainWindow(con);

		if(consBuilder == null)
			this.consBuilder = new ConstraintBuilder();
		connectActions();
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
							System.out.println("Führe Anfrage aus.");
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

										DetailStatementBuilder dtBuilder = new DetailStatementBuilder((selectedMode+1), Integer.valueOf(table.getModel().getValueAt(table.getSelectedRow(), 0).toString()), con, mainWindow);
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

		// ActionListener für das Hinzufügen von Constraints des Typ 1
		mainWindow.getBtnAddConstraint1().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent) {
				Constraint constraint = null;

				/* Entweder wird an eine bestehendes Constraint mit oder ein weiteres angehängt ODER 
				 * Es wird ein einzelnes Constraint erzeugt, dass später per "AND" mit weiteren Constraints verbunden wird.
				 */
				if(mainWindow.getConstraint1AndOr().getSelectedIndex() == 1 && lastConstraintType1 != null){
					constraint = consBuilder.createORConstraintType1(mainWindow.getConstraintComboBox1(), mainWindow.getComparisonCombobox1(), mainWindow.getTextFieldConstraint1(), lastConstraintType1);

					// Letztes Constraint wird gelöscht und weiter unten durch das Neue ersetzt.
					constraints.remove(lastConstraintType1);
					DefaultListModel<String> listModel = mainWindow.getListModel();
					listModel.removeElementAt(listModel.size() - 1);
				} else {
					constraint = consBuilder.createConstraintType1(mainWindow.getConstraintComboBox1(), mainWindow.getComparisonCombobox1(), mainWindow.getTextFieldConstraint1());
				}

				//Das Constraint wird zur ArrayList "constraints" und zur listView in MainWindow hinzugefügt.
				DefaultListModel<String> listModel = mainWindow.getListModel();
				listModel.addElement(constraint.getStatementName());
				constraints.add(constraint);
				lastConstraintType1 = constraint;
				mainWindow.getConstraint1AndOr().setModel(new DefaultComboBoxModel(new String[] {"AND", "OR"}));
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
					constraint = consBuilder.createORConstraintType2(mainWindow.getTextFieldConstraint2(), mainWindow.getComparisonCombobox2(), mainWindow.getConstraintComboBox2(), lastConstraintType2);

					// Letztes Constraint wird gelöscht und weiter unten durch das Neue ersetzt.
					constraints.remove(lastConstraintType2);
					DefaultListModel<String> listModel = mainWindow.getListModel();
					listModel.removeElementAt(listModel.size() - 1);
				} else {
					constraint = consBuilder.createConstraintType2(mainWindow.getTextFieldConstraint2(), mainWindow.getComparisonCombobox2(), mainWindow.getConstraintComboBox2());
				}

				//Das Constraint wird zur ArrayList "constraints" und zur listView in MainWindow hinzugefügt.
				DefaultListModel<String> listModel = mainWindow.getListModel();
				listModel.addElement(constraint.getStatementName());
				constraints.add(constraint);
				lastConstraintType2 = constraint;
				mainWindow.getConstraint2AndOr().setModel(new DefaultComboBoxModel(new String[] {"AND", "OR"}));
			}
		});

		// Entfernt das in der listView ausgewählte Constraint.
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

				// Auswahlmöglichkeiten für die erste Combobox der Einschränkung vom Typ 1
				switch (selectedMode){
				case 0:
					choice = new String[]{"CompanyName", "CompanyType", "TitelType", "ProductionYear"};
					break;
				case 1:
					choice = new String[]{"Titel", "TitelType", "CompanyType", "ProductionYear"};
					break;
				case 2:
					choice = new String[]{"RollenName", "RollenType", "Titel", "TitelType"};
					break;
				default:
					throw new RuntimeException("Wrong mode");
				}
				mainWindow.getConstraintComboBox1().setModel(new DefaultComboBoxModel(choice));
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
	}

}
