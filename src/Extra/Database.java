package Extra;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public Statement state;
    public ResultSet result;
    private final String driver = "org.mysql.Driver";
    private final String root = "jdbc:mysql://127.0.0.1/faceid_database";
    private final String user = "root";
    private final String pass = "";

    public Connection link;

    public void connect() {
        try {
            System.setProperty("jdbc.Driver", driver);
            link = DriverManager.getConnection(root, user, pass);
            System.out.println("successful");
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    public void disconnect() {
        try {
            link.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    public void execute(String SQL) {
        try {
            state = link.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = state.executeQuery(SQL);
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    //public static void main(String[] args) {
    //    database connect = new database();
    //   connect.connect();
    //}
}
