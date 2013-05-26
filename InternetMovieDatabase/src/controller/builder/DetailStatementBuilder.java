package controller.builder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

import main.Main;

import view.MainWindow;


public class DetailStatementBuilder {
	private int id;
	private Connection con;
	private MainWindow mainWindow;
	private String query1;
	private String query2;
	private String option1LabelValue, option2LabelValue, option3LabelValue;
	private String tableTitle;
	
	public DetailStatementBuilder(int typ, int id, Connection con, MainWindow mainWindow){
		this.id = id;
		this.con = con;
		this.mainWindow = mainWindow;
		
		//Entscheidet welcher Suchtyp vorliegt.
		switch(typ){
		case 1:
			setTitelDetailStatement();
			break;
		case 2:
			setCompanyDetailStatement();
			break;
		case 3:
			setPersonDetailStatement();
			break;	
		default:
			throw new RuntimeException("Wrong type");
		}	
		
			
	}
	
	//Suchtyp 1 (Titel)
	private void setTitelDetailStatement(){
		// Zum Abfragen der Einzelinformationen (Titel, Produktionsjahr, Typ)
		query1 = "Select Distinct imdb.title.title, imdb.title.production_year, imdb.kind_type.kind" +
						" From imdb.title join imdb.kind_type on imdb.title.kind_id = imdb.kind_type.id Where imdb.title.id = ";
		
		// Zum Abfragen des Casts.
		query2 = "Select Distinct imdb.name.name Name, imdb.role_type.role Typ From imdb.name " +
						" join imdb.cast_info on imdb.name.id = imdb.cast_info.person_id" +
						" join imdb.role_type on imdb.cast_info.role_id = imdb.role_type.id" +
						" join imdb.title on imdb.title.id = imdb.cast_info.movie_id Where imdb.title.id = ";
		
		option1LabelValue = "Titel: ";
		option2LabelValue = "Produktionjahr: ";
		option3LabelValue = "Typ: ";
		tableTitle = "Cast:";
		mainWindow.getAddToFavList().setVisible(true);
	}
	
	//Suchtyp 2 (Company)
	private void setCompanyDetailStatement(){
		// Zum Abfragen der Einzelinformationen (Companyname, Typ, Countrycode)
		query1 = "Select Distinct imdb.company_name.name, imdb.company_type.kind, imdb.company_name.country_code From imdb.company_name" +
						" join imdb.movie_companies on imdb.movie_companies.company_id = imdb.company_name.id" +
						" join imdb.company_type on imdb.company_type.id = imdb.movie_companies.company_type_id" +
						" Where imdb.company_name.id = ";
		query2 = "Select Distinct imdb.title.title Titel, imdb.kind_type.kind Typ, imdb.title.production_year Produktionsjahr From imdb.title" +
				" join imdb.kind_type on imdb.kind_type.id = imdb.title.kind_id" +
				" join imdb.movie_companies on imdb.movie_companies.movie_id = imdb.title.id" +
				" join imdb.company_name on imdb.company_name.id = imdb.movie_companies.company_id" +
				" join imdb.company_type on imdb.company_type.id = imdb.movie_companies.company_type_id Where imdb.company_name.id = ";
		
		option1LabelValue = "Companyname: ";
		option2LabelValue = "Typ: ";
		option3LabelValue = "Countrycode: ";
		tableTitle = "Filmproduktionen:";
		mainWindow.getAddToFavList().setVisible(false);
	}
	
	//Suchtyp 3 (Person)
	private void setPersonDetailStatement(){
		// Zum Abfragen der Einzelninformationen (Name, Geschlecht)
		query1 = "Select Distinct imdb.name.name, imdb.name.gender From imdb.name Where imdb.name.id = ";
		
		// Zum Abfragen der Filme, in denen die Person mitgewirkt hat.
		query2 = "Select Distinct imdb.title.title Titel, imdb.role_type.role Rolle From imdb.name" +
						" join imdb.cast_info on imdb.name.id = imdb.cast_info.person_id" +
						" join imdb.role_type on imdb.role_type.id = imdb.cast_info.role_id" +
						" join imdb.title on imdb.title.id = imdb.cast_info.movie_id Where imdb.name.id = ";
		
		option1LabelValue = "Name: ";
		option2LabelValue = "Geschlecht: ";
		option3LabelValue = "";
		tableTitle = "Beteiligte Filmproduktionen:";
		mainWindow.getAddToFavList().setVisible(false);
	}
	
	// Führt die beiden notwendigen Abfragen für die Detailansicht aus.
	public void executeStatement() throws SQLException{
		
		//Die Labels werden auf ihre Anfangswerte gesetzt.
		mainWindow.getDetailTableTitle().setText(tableTitle);
		mainWindow.getOption1Label().setText("");
		mainWindow.getOption2Label().setText("");
		mainWindow.getOption3Label().setText("");
		
		//----Abfrage 1----
		Statement stmt1 = con.createStatement();
		ResultSet result1 = stmt1.executeQuery(query1 + " " + id);
		ResultSetMetaData resultMetaData = result1.getMetaData();
		int columnNumber = resultMetaData.getColumnCount();
		
		result1.next();
		if(columnNumber >= 1)
			mainWindow.getOption1Label().setText(option1LabelValue + result1.getString(1));
		if(columnNumber >= 2)
			mainWindow.getOption2Label().setText(option2LabelValue + result1.getString(2));
		if(columnNumber >= 3)
			mainWindow.getOption3Label().setText(option3LabelValue + result1.getString(3));
		result1.close();
		
		//----Abfrage 1---ENDE----
		
		//----Abfrage 2-----
		Statement stmt2 = con.createStatement();
		ResultSet result2 = stmt2.executeQuery(query2 + " " + id);
		
		ResultSetMetaData resultmetaData2 = result2.getMetaData();
		int columnNumber2 = resultmetaData2.getColumnCount();
		String[] columnNames = new String[columnNumber2];
		if (Main.isDebug())
			System.out.println("Frage Tabellennamen ab.");
		for(int i = 1; i <= columnNumber2; i++){
			columnNames[i-1] = resultmetaData2.getColumnName(i);
		}
		DefaultTableModel tModel = new DefaultTableModel(columnNames, 0){
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		mainWindow.getDetailTable().setModel(tModel);
		tModel.setColumnIdentifiers(columnNames);
		if (Main.isDebug())
			System.out.println("Fülle Tabelle auf.");
		
		// holt sich die Daten aus dem ResultSet
		while(result2.next()){
			if (Main.isDebug())
				System.out.println("Next");
			Object[] objects = new Object[columnNumber2]; // stellt einen Datensatz dar.
			
			for(int i = 1; i <= columnNumber2; i++){
				objects[i-1] = result2.getObject(i);
			}
			tModel.addRow(objects);
		}
		result2.close();
		
		//-----Abfrage 2---ENDE
		
	}
	
	
	
	
}
