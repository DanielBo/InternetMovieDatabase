package controller.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import main.Main;
import model.Constraint;


public class QueryBuilder {
	private static final String PERSON_BASE = "IMDB.cast_info join IMDB.name on imdb.cast_info.person_id = imdb.name.id join imdb.role_type on imdb.role_type.id = imdb.cast_info.role_id";
	private static final String COMPANIES_BASE = "IMDB.movie_companies join IMDB.company_name on imdb.company_name.id = imdb.movie_companies.company_id join imdb.company_type on imdb.company_type.id = imdb.movie_companies.company_type_id";
	private static final String TITLE_BASE = "IMDB.title join IMDB.kind_type on imdb.title.kind_id = imdb.kind_type.id";
	private String basicStatement,constraintStatement, basicConstraint, basicSelectFrom;
	private String searchValue;
	private ArrayList<Constraint> constraints;
	private HashSet<String> usedTables = new HashSet<String>();
	
	/*Diese Hashmap ordnet den auf der GUIL auswählbaren Eigenschaften, wie "TitleType", die entsprechenden TeilSQL Anfragen zu.
	 * Die Eigenschaften befinden sich dabei im key als Array, während das entsprechene SQL-Anfrage der Valuewert ist.
	 */
	private HashMap<String[], String> map = new HashMap<String[], String>();

	/**
	 * Used to build a SQL Query
	 * @param mode
	 * @param constraints
	 * @param searchValue
	 */
	public QueryBuilder(int mode, ArrayList<Constraint> constraints,  String searchValue){
		this.searchValue = searchValue;
		this.constraints = constraints;

		constraintStatement = " Where ";

		// Die Variablen für die drei Suchtypen (Titel, Company, Person) werden gesetzt.
		switch (mode) {
		case 0:
			//Titelsuche
			basicStatement =TITLE_BASE;
			usedTables.add("TitelType");
			usedTables.add("Titel");
			usedTables.add("ProductionYear");
			
			basicConstraint = " lower(IMDB.title.title) Like ";
			basicSelectFrom = "Select Distinct IMDB.title.id, IMDB.title.title Titel, IMDB.kind_type.kind Typ, IMDB.title.production_year Produktionsjahr From ";
			break;
		case 1:
			//Compansuche
			basicStatement = COMPANIES_BASE;
			usedTables.add("CompanyName");
			usedTables.add("CompanyType");
			
			basicConstraint = " lower(IMDB.company_name.name) Like ";
			basicSelectFrom = "Select Distinct Imdb.company_name.id, imdb.company_name.name Name, imdb.company_type.kind Typ From ";
			map.put(new String[]{"TitelType", "Titel", "ProductionYear", "ConstraintType2", "PersonenName"}, "join (" + TITLE_BASE + ") on imdb.movie_companies.movie_id = imdb.title.id");
			break;
		case 2 :
			//Personensuche
			basicStatement = PERSON_BASE;
			usedTables.add("PersonenName");
			usedTables.add("RollenType");
			
			basicConstraint = " lower(IMDB.name.name) Like ";
			basicSelectFrom = "Select Distinct IMDB.name.id, IMDB.name.name Name, IMDB.role_type.role Rolle From ";
			map.put(new String[]{"TitelType", "Titel", "ProductionYear", "ConstraintType2", "CompanyName", "CompanyType"}, "join (" + TITLE_BASE + ") on imdb.cast_info.movie_id = imdb.title.id");
			break;
		default:
			throw new RuntimeException("Wrong mode");
		}
		
		// verschiedene join statements, die das basicstatement erweitern
		map.put(new String[]{"CompanyName","CompanyType"}, "join (" + COMPANIES_BASE + ") on imdb.movie_companies.movie_id = imdb.title.id");
		map.put(new String[]{"PersonenName", "RollenType"}, "join (" + PERSON_BASE +") on imdb.cast_info.movie_id = imdb.title.id");
		map.put(new String[]{"RollenName"}, "join imdb.char_name on imdb.char_name.id = imdb.cast_info.person_role_id");
	}


	//Fügt Einschränkungen (Where-Teil) und notwendige Tables per Join dem basicStatement hinzu.
	private void appendBasicStatement(){
		if(constraints.size() > 0){
			for (Constraint c : constraints){
				for(String tableName : c.getTableNames()){
					if(tableName != null){
						if (!usedTables.contains(tableName)){
							usedTables.add(tableName);
							basicStatement += " " + getAppendStatement(tableName);
						}
					}
				}
				// Hängt den Where-Teil an die Abfrage
				constraintStatement += c.getStatement() + " AND ";
			}
		}
	}


	//Fügt notwendige Tabellen per Join hinzu.
	private String getAppendStatement(String tableName) {
		String appendStatement = "";
		for (Map.Entry<String[], String> e : map.entrySet()) {
			for (String s :e.getKey())
				if (tableName.equals(s)){
					if (Main.isDebug())
						System.out.println("Test: " + e.getValue());
					appendStatement += " " + e.getValue();
				}
		}
		return appendStatement;
	}


	//Setzt die entgültige SQL-Abfrage zusammen und gibt die als String zurück.
	public String getStatement(){
		appendBasicStatement();
		
		String string = basicSelectFrom + basicStatement + constraintStatement + basicConstraint + " '%" + searchValue + "%'";
		if (Main.isDebug())
			System.out.println(string);

		return string;
	}
}
