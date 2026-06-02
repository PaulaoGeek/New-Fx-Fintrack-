package fintrack.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fintrack.model.Despesa;
import fintrack.model.Receita;
import fintrack.model.Transacao;

public class TransacaoDAO {

    public void createTable() {
        String sqlUser = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            );
            """;
            
        String sqlTransaction = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                description TEXT NOT NULL,
                value REAL NOT NULL,
                date TEXT NOT NULL,
                kind TEXT NOT NULL,
                category TEXT NOT NULL,
                user_id INTEGER,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """;
            
        try (Connection conn = Conexao.getConnection(); 
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sqlUser);
            stmt.execute(sqlTransaction);
            
            try { stmt.execute("ALTER TABLE transactions ADD COLUMN category TEXT DEFAULT 'Outros';"); } catch (SQLException e) {}
            try { stmt.execute("ALTER TABLE transactions ADD COLUMN user_id INTEGER;"); } catch (SQLException e) {}
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao iniciar tabelas relacionais.", e);
        }
    }

    public void saveUser(String name) {
        String sql = "INSERT INTO users (name) VALUES (?)";
        try (Connection conn = Conexao.getConnection(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, name);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar usuário.", e);
        }
    }

    public List<String> listUsers() {
        List<String> users = new ArrayList<>();
        String sql = "SELECT name FROM users ORDER BY name ASC";
        try (Connection conn = Conexao.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { users.add(rs.getString("name")); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return users;
    }

    public int getUserIdByName(String name) {
        String sql = "SELECT id FROM users WHERE name = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, name);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public void saveWithUser(Transacao t, String userName) {
        if (t == null) throw new RuntimeException("Transação nula.");
        int userId = getUserIdByName(userName);
        String sql = "INSERT INTO transactions (description, value, date, kind, category, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, t.getDescription());
            pstm.setDouble(2, t.getValue());
            pstm.setString(3, t.getDate().toString());
            pstm.setString(4, t.getKind());
            pstm.setString(5, t.getCategory());
            pstm.setInt(6, userId);
            pstm.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Transacao> listByByUser(String userName) {
        List<Transacao> list = new ArrayList<>();
        int userId = getUserIdByName(userName);
        if (userId == -1) return list;

        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC";
        try (Connection conn = Conexao.getConnection(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, userId);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    String desc = rs.getString("description");
                    double val = rs.getDouble("value");
                    LocalDate dt = LocalDate.parse(rs.getString("date"));
                    String kd = rs.getString("kind");
                    String cat = rs.getString("category"); // <- Captura a categoria do banco
                    
                    if ("Receita".equalsIgnoreCase(kd)) {
                        list.add(new Receita(desc, val, dt, cat)); // <- Passa 4 parâmetros
                    } else {
                        list.add(new Despesa(desc, val, dt, cat)); // <- Passa 4 parâmetros
                    }
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public void update(Transacao nova, String descAntiga, double valorAntigo) {
        String sql = "UPDATE transactions SET description = ?, value = ?, date = ?, kind = ?, category = ? WHERE description = ? AND value = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, nova.getDescription());
            pstm.setDouble(2, nova.getValue());
            pstm.setString(3, nova.getDate().toString());
            pstm.setString(4, nova.getKind());
            pstm.setString(5, nova.getCategory());
            pstm.setString(6, descAntiga);
            pstm.setDouble(7, valorAntigo);
            pstm.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(String description, double value) {
        String sql = "DELETE FROM transactions WHERE description = ? AND value = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, description);
            pstm.setDouble(2, value);
            pstm.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
