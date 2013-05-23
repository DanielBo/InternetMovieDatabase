package Controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import Model.Constraint;

public class StatementExecuter {
	private static final String PERSON_BASE = "IMDB.cast_info join IMDB.name on imdb.cast_info.person_id = imdb.name.id";
	private static final String COMPANIES_BASE = "IMDB.movie_companies join IMDB.company_name on imdb.company_name.id = imdb.movie_companies.company_id join imdb.company_type on imdb.company_type.id = imdb.movie_companies.company_type_id";
	private static final String TITLE_BASE = "IMDB.title join IMDB.kind_type on imdb.title.kind_id = imdb.kind_type.id";
	private Connection con;
	private String basicStatement,constraintStatement, basicConstraint, basicSelectFrom;
	private String searchValue;
	private ArrayList<Constraint> constraints;
	private HashSet<String> usedTables = new HashSet<String>();
	private HashMap<String[], String> map = new HashMap<String[], String>();

	public StatementExecuter(Connection con, int mode, ArrayList<Constraint> constraints,  String searchValue){
		this.con = con;
		this.searchValue = searchValue;
		this.constraints = constraints;

		constraintStatement = " Where ";

		// 3 different searchmodes (Titel, Company, Person)
		switch (mode) {
		case 0:
			basicStatement =TITLE_BASE;
			usedTables.add("TitelType");
			usedTables.add("Titel");
			usedTables.add("ProductionYear");
			
			basicConstraint = " IMDB.title.title Like ";
			basicSelectFrom = "Select Distinct IMDB.title.id, IMDB.title.title, IMDB.kind_type.kind Typ, IMDB.title.production_year From ";
			break;
		case 1:
			basicStatement = COMPANIES_BASE;
			usedTables.add("CompanyName");
			usedTables.add("CompanyType");
			
			basicConstraint = " IMDB.company_name.name Like ";
			basicSelectFrom = "Select Distinct Imdb.company_name.id, imdb.company_name.name, imdb.company_type.kind Typ From ";
			map.put(new String[]{"TitelType", "Titel", "ProductionYear", "ConstraintType2", "PersonenName"}, "join (" + TITLE_BASE + ") on imdb.movie_companies.movie_id = imdb.title.id");
			break;
		case 2 :
			basicStatement = PERSON_BASE;
			usedTables.add("PersonenName");
			
			basicConstraint = " IMDB.name.name Like ";
			basicSelectFrom = "Select Distinct IMDB.name.id, IMDB.name.name From ";
			map.put(new String[]{"TitelType", "Titel", "ProductionYear", "ConstraintType2", "CompanyName", "CompanyType", "RollenType", "RollenName"}, "join (" + TITLE_BASE + ") on imdb.cast_info.movie_id = imdb.title.id");
			break;
		default:
			throw new RuntimeException("Wrong mode");
		}
		
		// verschiedene join statements, die das basicstatement erweitern
		map.put(new String[]{"CompanyName","CompanyType"}, "join (" + COMPANIES_BASE + ") on imdb.movie_companies.movie_id = imdb.title.id");
		map.put(new String[]{"PersonenName"}, "join (" + PERSON_BASE +") on imdb.cast_info.movie_id = imdb.title.id");
		map.put(new String[]{"RollenType"}, "join (imdb.cast_info join imdb.role_type on imdb.role_type.id = imdb.cast_info.role_id ) on imdb.cast_info.movie_id = imdb.title.id");
		map.put(new String[]{"RollenName"}, "join (imdb.cast_info join imdb.char_name on imdb.char_name.id = imdb.cast_info.person_role_id) on imdb.cast_info.movie_id = imdb.title.id");
	}


	// Methode, um das basicstatement zu erweitern
	public void appendBasicStatement(){
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
				// H�ngt den Where-Teil an die Abfrage
				constraintStatement += c.getStatement() + " AND ";
			}
		}
	}


	// Methode, um die passenden Statements auszuw�hlen
	private String getAppendStatement(String tableName) {
		String appendStatement = "";
		for (Map.Entry<String[], String> e : map.entrySet()) {
			for (String s :e.getKey())
				if (tableName.equals(s)){
					System.out.println("Test: " + e.getValue());
					appendStatement += " " + e.getValue();
				}
		}
		return appendStatement;
	}





	// Methode, die die fertige Abfrage ausf�hrt und das ResultSet zur�ckgibt.
	public ResultSet executeStatement()throws SQLException{
		appendBasicStatement();
		ResultSet result = null;

		Statement stmt = con.createStatement();
		
		String string = basicSelectFrom + basicStatement + constraintStatement + basicConstraint + " '%" + searchValue + "%'";
		System.out.println(string);
		result = stmt.executeQuery(string);

		return result;
	}
}