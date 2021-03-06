﻿import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * JDBC Aufgabe 3b
 * 
 * Diese Klasse tellt einfache Ausgabefunktionen für ResultSet-Instanzen zur Verfügung.
 *
 * Ziele
 * - Erstes Arbeiten mit Connection, Statement und ResultSet
 * - Aktives Arbeiten mit Metadaten
 * 
 * In dieser Datei sollen Sie: 
 * 1. Die Methode printResultTable ändern, so dass auch die Typnamen mit ausgegeben werden.
 * 
 *   PERSNR                 | NAME                           | ORT                  | AUFGABE            
 *   NUMBER                 | CHAR                           | CHAR                 | CHAR               
 *  ------------------------+--------------------------------+----------------------+---------
 *                        1 | Maria Forster                  | Regensburg           | Manager            
 *                        2 | Anna Kraus                     | Regensburg           | Vertreter          
 *                      ...      
 *                        9 | Ernst Pach                     | Stuttgart            | Azubi
 * 2. Weiter soll die Methode resultToCsv() zum Export ins CSV Format implementiert werden.
 * 
 *    Beispielausgabe:
 *     "nr";"name";"strasse";"plz";"ort";"sperre"
 *     1;"Fahrrad Shop";"Obere Regenstr. 4";93059;"Regensburg";"0"
 *     2;"Zweirad-Center Staller";"Kirschweg 20";44267;"Dortmund";"0"
 *     3;"Maier Ingrid";"Universitätsstr. 33";93055;"Regensburg";"1"
 *     4;"Rafa - Seger KG";"Liebigstr. 10";10247;"Berlin";"0"
 *     5;"Biker Ecke";"Lessingstr. 37";22087;"Hamburg";"0"
 *     6;"Fahrräder Hammerl";"Schindlerplatz 7";81739;"München";"0"* 
 *     
 * Zum Testen bitte jeweils die main-Methode ausführen.
 *
  */
public class Output {

	/**
	 * Die maximale Spaltenbreite für formatierte Ausgaben.
	 */
	private static final int MAX_COL_WIDTH = 30;

	/**
	 * Gibt zurück, ob ein Typ (aus java.sql.Types) in Anführungszeichen
	 * dargestellt werden sollte.
	 *
	 * @param type Alle aus Types.*
	 * @return False numerische Werte, für alles andere true
	 */
	private static boolean isQuotedType(int type) {
		// Numerische Werte nicht in Anführungszeichen
		switch (type) {
			case Types.BIT:
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.FLOAT:
			case Types.REAL:
			case Types.DOUBLE:
			case Types.NUMERIC:
			case Types.DECIMAL:
			case Types.NULL:
			case Types.BOOLEAN:
				return false;
		}
		// Alles andere in Anführungszeichen
		return true;
	}
	/**
	 * Gibt den Names eines Typs (aus java.sql.Types) zurück.
	 *
	 * @param type Alle aus Types.*
	 * @return Name des Typs.
	 */
	private static final String getTypeName(int type) {
		switch (type) {
			case Types.ARRAY:
				return "ARRAY";
			case Types.BINARY:
				return "BINARY";
			case Types.BLOB:
				return "BLOB";
			case Types.TINYINT:
				return "TINYINT";
			case Types.SMALLINT:
				return "SMALLINT";
			case Types.INTEGER:
				return "INTEGER";
			case Types.BIGINT:
				return "BIGINT";
			case Types.FLOAT:
				return "FLOAT";
			case Types.REAL:
				return "REAL";
			case Types.DOUBLE:
				return "DOUBLE";
			case Types.NUMERIC:
				return "NUMERIC";
			case Types.DECIMAL:
				return "DECIMAL";
			case Types.NULL:
				return "NULL";
			case Types.CLOB:
				return "CLOB";
			case Types.CHAR:
				return "CHAR";
			case Types.DATALINK:
				return "DATALINK";
			case Types.DISTINCT:
				return "DISTINCT";
			case Types.JAVA_OBJECT:
				return "JAVA_OBJECT";
			case Types.LONGVARCHAR:
				return "LONGVARCHAR";
			case Types.LONGVARBINARY:
				return "LONGVARBINARY";
			case Types.LONGNVARCHAR:
				return "LONGNVARCHAR";
			case Types.NCHAR:
				return "NCHAR";
			case Types.NCLOB:
				return "NCLOB";
			case Types.NVARCHAR:
				return "NVARCHAR";
			case Types.OTHER:
				return "OTHER";
			case Types.REF:
				return "REF";
			case Types.ROWID:
				return "ROWID";
			case Types.SQLXML:
				return "SQLXML";
			case Types.STRUCT:
				return "STRUCT";
			case Types.TIME:
				return "TIME";
			case Types.TIMESTAMP:
				return "TIMESTAMP";
			case Types.VARBINARY:
				return "VARBINARY";
			case Types.VARCHAR:
				return "VARCHAR";
		}
		return "Unknown";
	}

	/**
	 * Gibt eine gegebene ResultSet-Instanz im CSV-Format aus.
	 * <p>
	 * Felder werden durch Semikolon getrennt, quotierte Typen werden
	 * "getrimmt" (String.trim()) und in doppelte Anführungszeichen gestellt.
	 *
	 * @param rs  Die ResultSet Instanz, die auszugeben ist
	 * @param out Die PrintStream Instanz, auf die ausgegeben wird, z.B. System.out
	 * @throws SQLException Im Falle von Verbindungsproblemen
	 */
	public static void resultToCsv(ResultSet rs, PrintStream out)
			throws SQLException {

		// Ausgabe der Überschriften
		ResultSetMetaData meta = rs.getMetaData();
		int columns = meta.getColumnCount();
		for (int column = 1; column <= columns; column++) {
			if (column != 1) {
				out.printf(";");
			}
			out.printf("\"%s\"", meta.getColumnLabel(column));
		}
		out.println();

		// Ausgabe aller Zeilen aus ResultSet
		int rows = 0;
		while (rs.next()) {
			for (int column = 1; column <= columns; column++) {
				if (column != 1) {
					out.printf(";");
				}
				String cell = rs.getString(column);
				if (isQuotedType(meta.getColumnType(column))) {
					out.printf("\"%s\"", (cell != null ? cell.trim() : ""));
				} else {
					out.printf("%s", (cell != null ? cell.trim() : ""));
				}
			}
			rows++;
			out.println();
		}
		out.printf("(%d rows)%n%n", rows);
	}

	/**
	 * Gibt ein ResultSet ähnlich wie ein SQL Kommandozeilen Client aus.
	 * <p>
	 * Numerische Datentypen sind rechtsbündig, andere (in Anführungszeichen
	 * stehende) linksbündig ausgerichtet.
	 * <p>
	 * Diese Funktion beachtet MAX_COL_WIDTH als Grenze für die maximale
	 * Spaltenbreite. Das heißt Felder, die breiter sind, werden bei der
	 * Darstellung abgeschnitten.
	 *
	 * @param rs  Eine ResultSet Instanz
	 * @param out Die PrintStream Instanz zur Ausgabe, z.B. System.out
	 * @throws SQLException Im Falle von Verbindungsproblemen
	 */
	public static void printResultTable(ResultSet rs, PrintStream out)
			throws SQLException {
		// Erstellen eines horizontalen Abstandhalters in ausreichender Länge
		String horizSeparator = "--------------------------------------------";
		while (horizSeparator.length() < Output.MAX_COL_WIDTH) {
			horizSeparator += horizSeparator;
		}

		// Ausgabe der Überschriften und Ausrichtung und Breite der Spalten ermitteln
		ResultSetMetaData meta = rs.getMetaData();
		int columns = meta.getColumnCount();
		int[] width = new int[columns];
		boolean[] leftAligned = new boolean[columns];
		for (int column = 1; column <= columns; column++) {
			leftAligned[column - 1] = isQuotedType(meta.getColumnType(column));
			width[column - 1] = meta.getColumnDisplaySize(column);
			width[column - 1] = width[column - 1] > Output.MAX_COL_WIDTH
					? Output.MAX_COL_WIDTH : width[column - 1];
			out.printf((column > 1 ? "| " : " ") + "%-" + width[column - 1]
					+ "." + width[column - 1] + "s ", meta.getColumnLabel(column));
		}
		out.println();

		// Zeile mit den Typen ausgeben
		for (int column = 1; column <= columns; column++) {
			String cell = getTypeName(meta.getColumnType(column));
			out.printf((column > 1 ? "| " : " ")
					+ "%-" + width[column - 1]
					+ "." + width[column - 1] + "s ", (cell != null ? cell : ""));
		}
		out.println();

		// Ausgabe horizontaler Abstandhalter
		for (int column = 1; column <= columns; column++) {
			out.printf((column > 1 ? "+-" : "-") + "%-" + width[column - 1]
					+ "." + width[column - 1] + "s-", horizSeparator);
		}
		out.println();

		// Ausgabe aller Zeilen aus ResultSet
		int rows = 0;
		while (rs.next()) {
			for (int column = 1; column <= columns; column++) {
				String cell = rs.getString(column);
				out.printf((column > 1 ? "| " : " ")
						+ (leftAligned[column - 1] ? "%-" : "%") + width[column - 1]
						+ "." + width[column - 1] + "s ", (cell != null ? cell : ""));
			}
			rows++;
			out.println();
		}
		out.printf("(%d rows)%n%n", rows);
	}

	/**
	 * Diese Methode wird zum Testen der Implementierung verwendet.
	 *
	 * @param unused Kommandozeilenargumente nicht verwendet
	 * @throws SQLException Bei jedem SQL Fehler
	 */
	public static void main(String[] unused) throws SQLException {
		Connection connection = SQLConnector.getTestInstance().getConnection();
		Statement statement = connection.createStatement();

		ResultSet resultset = statement.executeQuery(
				"SELECT persnr, name, ort, aufgabe FROM personal");
		Output.printResultTable(resultset, System.out);

		resultset = statement.executeQuery("SELECT * FROM kunde");
		Output.resultToCsv(resultset, System.out);

		connection.close();
		statement.close();
		connection.close();
	}
}
