package model;

import java.util.LinkedList;
import java.util.List;

public class Constraint {

	private List<String> tableNames = new LinkedList<String>();
	private String statementName; // presenting name in mainWindow
	private String statement;

	public Constraint(String tableName, String statementName, String statement) {
		super();
		this.tableNames.add(tableName);
		this.statementName = statementName;
		this.statement = statement;
	}
	
	// fügt ein weiteres Statement, dass per OR verknüpft wird, dem bestehenden Statement hinzu.
	public void addOrConstraint(Constraint orConstraint){
		this.statementName = this.statementName + " oder " + orConstraint.getStatementName();
		this.statement = this.statement + " OR " + orConstraint.getStatementWithoutBracket();
		this.tableNames.addAll(orConstraint.getTableNames());
	}
	
	
	//Getter-Methoden
	
	public List<String> getTableNames() {
		return tableNames;
	}
	
	public String getStatement() {
		return "(" + statement + ")";
	}
	
	public String getStatementWithoutBracket() {
		return statement;
	}

	public String getStatementName() {
		return statementName;
	}

}

