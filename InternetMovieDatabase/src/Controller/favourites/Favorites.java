package controller.favourites;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Favorites {

	private Connection con;

	public Favorites() {
		intializeTables();
	}

	private void intializeTables() {
		String createTableFavorites = "CREATE TABLE favourites ( ID INTEGER(10) NOT NULL, CATEGORY  VARCHAR(255)  NOT NULL);";
		try {
			con.createStatement().executeQuery(createTableFavorites);
		} catch (SQLException e) {
			e.getStackTrace();
		}
	}

	public ArrayList<String> getCategories() throws SQLException{
		String getCategories ="Select Distinct Category from favourites";
		ArrayList<String> cats = new ArrayList<String>();
		ResultSet result;

		result = con.createStatement().executeQuery(getCategories);
		while (result.next()){
			cats.add(result.getString(1));
		}
		return cats;
	}

	public ResultSet getFavByCategory(String cat) throws SQLException {
		ResultSet result = null;
		String getFavByCategory = "Select id from favourites where Category = '" + cat + "'";
		result = con.createStatement().executeQuery(getFavByCategory);
		return result;
	}

	public void addIdToFavorites(String id, String category) throws SQLException{
		String inputStatement = "INSERT INTO favourites VALUES (" + id + ", '" + category + "')";
		con.createStatement().executeQuery(inputStatement);
	}

}

