package fintrack.repository;

import fintrack.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAO {
    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                description TEXT NOT NULL,
                value REAL NOT NULL,
                date TEXT NOT NULL,
                kind TEXT NOT NULL
            );
            """;
        try (Connection conn = Conexao.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao iniciar tabela", e);
        }
    }

    public void save(Transacao t) {
        if (t == null) throw new RuntimeException("Transação nula");
        String sql = "INSERT INTO transactions (description, value, date, kind) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, t.getDescription());
            pstm.setDouble(2, t.getValue());
            pstm.setString(3, t.getDate().toString());
            pstm.setString(4, t.getKind());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

   public List<Transacao> listAll() {
    List<Transacao> list = new ArrayList<>();
    String sql = "SELECT * FROM transactions";
    try (Connection conn = Conexao.getConnection(); 
         PreparedStatement pstm = conn.prepareStatement(sql); 
         ResultSet rs = pstm.executeQuery()) {
        while (rs.next()) {
            String desc = rs.getString("description");
            double val = rs.getDouble("value");
            LocalDate dt = LocalDate.parse(rs.getString("date"));
            String kd = rs.getString("kind");
            if ("Receita".equalsIgnoreCase(kd)) {
                list.add(new Receita(desc, val, dt));
            } else {
                list.add(new Despesa(desc, val, dt));
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    return list;
}

    public void delete(String description, double value) {
        String sql = "DELETE FROM transactions WHERE description = ? AND value = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, description);
            pstm.setDouble(2, value);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
