package fintrack.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    // IMPORTANTE: Agora apontamos para um arquivo físico permanente no seu computador
    private static final String PROD_URL = "jdbc:sqlite:fintrack.db";
    private static Connection testConnection = null;

    public static void setTestConnection(Connection connection) { 
        testConnection = connection; 
    }

    public static Connection getConnection() throws SQLException {
        // Se o JUnit 5 injetar um banco em memória RAM para os testes, usamos ele
        if (testConnection != null && !testConnection.isClosed()) {
            return testConnection;
        }
        
        // Se for você jogando no programa real, ele cria e salva direto no seu HD/SSD de forma definitiva!
        return DriverManager.getConnection(PROD_URL);
    }
}
