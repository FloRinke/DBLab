import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * JDBC Aufgabe 3d
 *
 * Aktualisieren des Datenbankschemas.
 *
 * Ziele
 * - Arbeiten mit DDL-Befehlen
 * - Erstellen von überwachten Fremdschlüsselbeziehungen
 * - Erste Heranführung an das Thema Normalisierung, hier besonders deren technische Umsetzung
 *
 * In dieser Datei sollen Sie:
 * Redundanzen in der Tabelle Teilestamm, die in der Spalte 'farbe' zu finden sind,
 * vermeiden. Dazu soll eine neue Tabelle 'farbe' angelegt werden.
 * Die Tabelle Teilestamm soll dann für die neue Tabelle 'farbe' einen
 * Fremdschlüssel verwenden.
 * Bei dieser Gelegenheit werden die Farben in der Tabelle 'farbe' um
 * zusätzliche Informationen ergänzt.
 *
 * Die Tabelle 'farbe' bekommt das folgende physikalische Datenmodell:
 *
 * 	Spalte	Beschreibung
 * 	nr		Der Type ist INTEGER, automatisches Hochzählen ist erlaubt, aber
 *  		nicht notwendig (es gibt leider keine einheitliche Syntax, die zwischen Oracle, MySQL
 *  		und PostgreSQL kompatibel ist). Diese Spalte bildet den
 *  		Primärschlüssel.
 *	name	Hat die gleichen Eigenschaften wie 'teilestamm.farbe', aber die Spalte
 *			darf keine Duplikate enthalten.
 *	rot, gruen, blau
 *			Diese Spalten sind vom Typ REAL in einem Wertebereich von
 *			[0.0; 1.0], der sichergestellt werden muss.
 *			Der Standardwert ist 0.
 *
 * Test Ausgabe
 *
 * Die folgende Ausgabe sollte auf System.out erscheinen, wenn die main()
 * Methode zum ersten mal aufgerufen wird:
 * Updating database layout ...
 * Table 'farbe' created.
 * Added 3 rows to 'farbe'
 * Column 'farbnr' added to table 'teilestamm'
 * Set 'teilestamm.farbnr' in 34 rows
 * Column 'farbe' removed from 'teilestamm'
 *
 * Hinweis:
 * Setzen Sie die Methode LabUtilities.reInitializeDB() ein,
 * um die Datenbank immer wieder neu aufzusetzen beim Testen.
 */
public class SQLUpdateManager  {

    /**
     * Die verwendete SQL Verbindung.
     */
    private Connection connection;

    /**
     * Der Konstruktor, löst den Update-Vorgang aus.
     * <p>
     * Stellt die Verbindung zur Datenbank her und schließt diese auch wieder.
     *
     * @throws SQLException Wird geworfen, wenn die Datenbankverbindung oder ein
     *                      Statement scheitert
     */
    public SQLUpdateManager() throws SQLException {
        // TO DO begin
        connection = SQLConnector.getTestInstance().getConnection();
        // TO DO end

        if (!hasTable("farbe")) {
            update();
        } else {
            String err = "Table 'farbe' already created!";
            System.err.println(err);
            throw new SQLException(err);
        }
        // TO DO begin
        connection.close();
        // TO DO end
    }

    /**
     * Prüft, ob eine Tabelle existiert.
     *
     * @param table Die zu prüfende Tabelle
     * @return True, falls die Tabelle existiert, sonst False
     * @throws SQLException Im Fall von Verbindungsproblemen
     */
    private boolean hasTable(String table) throws SQLException {
        // TO DO begin
        ResultSet rs = connection.getMetaData().getTables(null, null, "%", null);
        while (rs.next()) {
            //System.out.println(rs.getString(3));
            if (rs.getString(3).equals(table)) {
                return true;
            }
        }
        return false;
        // TO DO end
    }

    /**
     * Aktualisiere das Datenbanklayout.<p>
     *
     * Führt die folgenden Aktionen aus:
     * - Geeignete Transaktions-Isolationsebene setzen ...
     * - Tabelle farbe anlegen
     * - Vorhandene Farben von teilestamm.farbe in farbe.name kopieren
     * - RGB Werte zu farbe Einträgen setzen
     * - In teilestamm die Spalte farbnr (als Foreign Key) anlegen
     * - Die Spalte teilestamm.farbnr mit Werten befüllen
     * - Die Spalte teilestamm.farbe entfernen
     * - Im Erfolgsfall Änderungen committen, sonst zurückrollen
     *
     * @throws SQLException Im Fall von Verbindungsproblemen
     */
    private void update() throws SQLException {
        System.out.println("Updating database layout ...");
        // TODO begin
        ResultSet rs;
        //Geeignete Transaktions-Isolationsebene setzen ...
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        //Tabelle farbe anlegen
        Statement stmt = connection.createStatement();
        String queryCreate = "CREATE TABLE farbe" +
                "(nr INTEGER NOT NULL," +
                " name CHAR(10) NOT NULL," +
                " rot REAL DEFAULT 0," +
                " gruen REAL DEFAULT 0," +
                " blau REAL DEFAULT 0," +
                " PRIMARY KEY (nr)," +
                " UNIQUE (name)," +
                " CHECK ( rot BETWEEN 0.0 AND 1.0)," +
                " CHECK ( gruen BETWEEN 0.0 AND 1.0)," +
                " CHECK ( blau BETWEEN 0.0 AND 1.0)" +
                ");";
        //System.out.println(queryCreate);
        stmt.execute(queryCreate);
        stmt.execute("CREATE SEQUENCE farbe_nr_seq;");
        stmt.execute("ALTER TABLE farbe ALTER nr SET DEFAULT NEXTVAL('farbe_nr_seq');");

        //Vorhandene Farben von teilestamm.farbe in farbe.name kopieren
        //RGB Werte zu farbe Einträgen setzen
        rs = stmt.executeQuery("SELECT DISTINCT farbe FROM teilestamm WHERE NOT farbe IS NULL");
        LinkedList<String> colors = new LinkedList<String>();
        while(rs.next()) {
            //System.out.println(rs.getString(1));
            colors.add(rs.getString(1).trim());
        }
        rs.close();
        for (String col : colors) {
            String queryInsert = "INSERT INTO farbe (name) VALUES ('" + col + "');";
            System.out.println(queryInsert);
            stmt.execute(queryInsert);
            String queryUpdate = null;
            if (col.equals("rot")) {
                System.out.println("Updating rot");
                queryUpdate = "UPDATE farbe SET rot=1.0 WHERE name='rot'";
                System.out.println(queryUpdate);
                stmt.execute(queryUpdate);
            } else if (col.equals("blau")) {
                System.out.println("Updating blau");
                queryUpdate = "UPDATE farbe SET blau=1.0 WHERE name='blau'";
                System.out.println(queryUpdate);
                stmt.execute(queryUpdate);
            }
            if (queryUpdate == null) {
                stmt.execute(queryUpdate);
            }
        }

        //In teilestamm die Spalte farbnr (als Foreign Key) anlegen
        System.out.println("Adding foreign key column");
        String queryUpdate = "ALTER TABLE teilestamm ADD farbnr INTEGER;" +
                             "ALTER TABLE teilestamm ADD CONSTRAINT FK_farbnr FOREIGN KEY (farbnr) REFERENCES farbe (nr);";
        stmt.execute(queryUpdate);

        //Die Spalte teilestamm.farbnr mit Werten befüllen
        System.out.println("Filling foreign key column with data");
        HashMap<String, Integer> fkmap = new HashMap<String, Integer>();
        rs = stmt.executeQuery("SELECT nr, name FROM farbe");
        while(rs.next()) {
            System.out.println("add " + rs.getString(2).trim() + ": " + rs.getInt(1));
            fkmap.put(rs.getString(2).trim(), rs.getInt(1));
        }
        rs.close();
        rs = stmt.executeQuery("SELECT teilnr,farbe FROM teilestamm WHERE NOT farbe IS NULL");
        PreparedStatement stmtUpdate = connection.prepareStatement("UPDATE teilestamm SET farbnr=? WHERE teilnr=?;");
        int result = 0;
        while(rs.next()) {
            //System.out.println("id(" + rs.getInt(1) +
            //                  ")->farbnr(" + rs.getString(2).trim() +
            //                  "," + fkmap.get(rs.getString(2).trim()) +
            //                  ");");
            stmtUpdate.setInt(1, fkmap.get(rs.getString(2).trim()));
            stmtUpdate.setInt(2, rs.getInt(1));
            result = stmtUpdate.executeUpdate();
            if (result != 1) {
                throw new SQLException("Unexpected number of results, aborting");
            }
            //System.out.println(result);
        }

        //Die Spalte teilestamm.farbe entfernen
        System.out.println("Remove old column");
        stmt.execute("ALTER TABLE teilestamm DROP COLUMN farbe;");

        //Im Erfolgsfall Änderungen committen, sonst zurückrollen
        connection.commit();
        // TODO end
    }

    /**
     * Diese Methode wird zum Testen der Implementierung verwendet.
     *
     * @param args Kommandozeilenargumente, nicht verwendet
     * @throws SQLException Bei jedem SQL Fehler
     */
    public static void main(String[] args) throws SQLException {
        new SQLUpdateManager();
    }
}
