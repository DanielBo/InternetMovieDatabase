package controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Main.Main;

public class StatementExecuter {
	private Connection con;
	private String statement;

	public StatementExecuter(Connection con, String statement){
		this.con = con;
		this.statement = statement;
		}
		
	// Methode, die die fertige Abfrage ausführt und das ResultSet zurückgibt.
	public ResultSet executeStatement()throws SQLException{
		ResultSet result = null;

		Statement stmt = con.createStatement();
		if (Main.isDebug())
			System.out.println(statement);
		result = stmt.executeQuery(statement);

		return result;
	}
}
