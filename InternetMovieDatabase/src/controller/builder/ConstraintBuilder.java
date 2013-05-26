package controller.builder;

import java.util.HashMap;

import javax.swing.JComboBox;

import main.Main;
import model.Constraint;


public class ConstraintBuilder {
	private HashMap<String, String> map;
	
	public ConstraintBuilder(){
		
		initMap();
	}

	private void initMap() {
		if (map != null)
			return;
		// map gibt an, für welche Spalte, die ausgewählt wird, welche Spalte in SQL verwendet wird.
		map = new HashMap<String, String>();
		map.put("TitelType", "lower(imdb.kind_type.kind)");
		map.put("Titel", "lower(imdb.title.title)");
		map.put("ProductionYear", "lower(imdb.title.production_year)");
		map.put("CompanyName", "lower(imdb.company_name.name)");
		map.put("CompanyType", "lower(imdb.company_type.kind)");
		map.put("PersonenName", "lower(imdb.name.name)");
		map.put("RollenType", "lower(imdb.role_type.role)");
		map.put("RollenName", "lower(imdb.char_name.name)");
	}
	
	// Fügt an ein bereits bestehendes Constraint ein Weiteres, das mit OR verbunden wird, hinzu. (Typ1 Constraint)
	public Constraint createORConstraintType1(JComboBox<String> constraintComboBox1, JComboBox<String> comparisonCombobox1, String textFieldConstraint1, Constraint lastConstraint){
		Constraint constraint = createConstraintType1(constraintComboBox1, comparisonCombobox1, textFieldConstraint1);
		lastConstraint.addOrConstraint(constraint);
		return lastConstraint;
	}
	
	// Fügt an ein bereits bestehendes Constraint ein Weiteres, das mit OR verbunden wird, hinzu. (Typ2 Constraint)
	public Constraint createORConstraintType2(String textFieldConstraint2, JComboBox<String> comparisonCombobox2, JComboBox<String> constraintComboBox2, Constraint lastConstraint){
		Constraint constraint = createConstraintType2(textFieldConstraint2, comparisonCombobox2, constraintComboBox2);
		lastConstraint.addOrConstraint(constraint);
		return lastConstraint;
	}
	
	// Erzeugt ein Constraint vom Typ 1
	public Constraint createConstraintType1(JComboBox<String> constraintComboBox1, JComboBox<String> comparisonCombobox1, String textFieldConstraint1){
		String columnName = constraintComboBox1.getSelectedItem().toString();
		if (Main.isDebug())
			System.out.println(columnName);
		String comparator, comparatorName;
		String value = textFieldConstraint1;
		int comparatorMode = comparisonCombobox1.getSelectedIndex();
		
		// Je nachdem welcher Vergleichsoperator ausgewählt wird, wird entscheiden, welcher Verbindungsstring verwendet wird.
		switch (comparatorMode) {
		case 0:
			comparatorName = "ist genau wie";
			comparator = "=";
			break;
		case 1:
			comparatorName = "ist nicht wie";
			comparator = "!=";
			break;
		case 2 :
			comparatorName = "enthält Teilstring";
			comparator = "Like";
			value = "%" + value + "%";
			break;
		default:
			throw new RuntimeException("Wrong mode");
		}
		
		// Der StatementName, der in der "listView" im GUI angezeigt wird und das Statement selbst, wird zusammengestellt.
		String statementName = columnName + " " + comparatorName + " " + value;
		String statement = map.get(columnName) + " " + comparator + " '" + value + "'";
		return new Constraint(columnName, statementName, statement);

	}
	
	// Erzeugt ein Constraint vom Typ 2
	public Constraint createConstraintType2 (String textFieldConstraint2, JComboBox<String> comparisonCombobox2, JComboBox<String> constraintComboBox2){
		int role = constraintComboBox2.getSelectedIndex() + 1;
		String value = textFieldConstraint2;
		String comparator, comparatorName;
		int comparatorMode = comparisonCombobox2.getSelectedIndex();
		
		// Je nachdem welcher Vergleichsoperator ausgewählt wird, wird entscheiden, welcher Verbindungsstring verwendet wird.
		switch (comparatorMode) {
		case 0:
			comparatorName = "ist ein";
			comparator = "Exists";
			break;
		case 1:
			comparatorName = "ist kein";
			comparator = "Not Exists";
			break;
		default:
			throw new RuntimeException("Wrong mode");
		}
		
		// Der StatementName, der in der "listView" im GUI angezeigt wird und das Statement selbst, wird zusammengestellt.
		String statementName = value + " " + comparatorName + " " + constraintComboBox2.getSelectedItem().toString();
		String statement =  comparator + " (Select imdb.cast_info.movie_id From imdb.name join imdb.cast_info on imdb.name.id = imdb.cast_info.person_id Where imdb.name.name = '" + value + "' AND imdb.cast_info.role_id = '" + role +"' and imdb.cast_info.movie_id = imdb.title.id)";
		
		return new Constraint("ConstraintType2", statementName, statement);

	}
}
