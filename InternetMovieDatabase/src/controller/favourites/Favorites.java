package controller.favourites;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import main.Main;

public class Favorites {

	private Connection con;

	public Favorites(Connection con) {
		this.con = con;
		intializeTables();
	}

	private void intializeTables() {
		if (Main.isDebug())
			System.out.println("creating favourites table");
		String createTableFavorites = "CREATE TABLE favourites (ID number(10) NOT NULL, CATEGORY  VARCHAR(255) NOT NULL)";
		try {
			con.createStatement().executeQuery(createTableFavorites);
		} catch (SQLException e) {
			if (e.getErrorCode() == 955){
				//do nothing
			} else {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				if (Main.isDebug())
					System.out.println(sw.toString());
			}
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

	/**
	 * Will return all ID's associated with the Category given.
	 * @param cat
	 * @return
	 * @throws SQLException
	 */
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

	public boolean removeIdFromFavorites(String id,String cat){
		try{
			String removeIdStatement = "DELETE FROM favourites Where id = '" + id + "' AND Category = '" + cat + "'";
			con.createStatement().execute(removeIdStatement);
		} catch (SQLException e){
			e.getStackTrace();
			return false;
		}
		return true;
	}

}

