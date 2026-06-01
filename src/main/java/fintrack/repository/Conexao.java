package fintrack.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String PROD_URL = "jdbc:sqlite:fintrack.db";
    private static Connection testConnection = null;

    public static void setTestConnection(Connection connection) { testConnection = connection; }

    public static Connection getConnection() throws SQLException {
        if (testConnection != null && !testConnection.isClosed()) {
            return testConnection;
        }
        return DriverManager.getConnection(PROD_URL);
    }
}
