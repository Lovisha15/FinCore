import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Central JDBC connection. Reads environment variables first, then ./db.properties. */
public final class DBConnection {
    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        Properties p = new Properties();
        Path configPath = Path.of(System.getProperty("fincore.config", "db.properties"));
        if (Files.isRegularFile(configPath)) {
            try (InputStream in = Files.newInputStream(configPath)) {
                p.load(in);
            } catch (IOException e) {
                throw new SQLException("Could not read " + configPath.toAbsolutePath() + ". Check file permissions and format.", e);
            }
        }

        String url = setting("DB_URL", "db.url", p, "jdbc:mysql://localhost:3306/bank?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String user = setting("DB_USER", "db.user", p, "root");
        String password = setting("DB_PASSWORD", "db.password", p, "");
        if (password.isBlank() || password.contains("YOUR_MYSQL_PASSWORD") || password.contains("PUT_YOUR")) {
            throw new SQLException("MySQL password is not configured. Open db.properties in the project root and set db.password to your local MySQL password.");
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Connector/J is missing. Open this project as a Maven project and reload Maven dependencies.", e);
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String setting(String env, String property, Properties p, String fallback) {
        String value = System.getenv(env);
        if (value != null && !value.isBlank()) return value.trim();
        value = p.getProperty(property);
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
