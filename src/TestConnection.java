import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            System.out.println("MySQL Connected Successfully!");
            System.out.println("Database: " + connection.getCatalog());
            System.out.println("Connection valid: " + connection.isValid(3));
        } catch (SQLException e) {
            System.err.println("MySQL connection failed: " + e.getMessage());
            if (e.getCause() != null) System.err.println("Cause: " + e.getCause().getMessage());
            System.err.println("Check MySQL service, db.properties, database name, and Connector/J dependency.");
        }
    }
}
