package fintrack.repository;

import fintrack.model.*;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TransacaoDAOTest {
    private static Connection conexaoEmMemoria;
    private TransacaoDAO dao;

    @BeforeEach
    public void setUp() throws SQLException {
        conexaoEmMemoria = DriverManager.getConnection("jdbc:sqlite::memory:");
        Conexao.setTestConnection(conexaoEmMemoria);
        dao = new TransacaoDAO();
        dao.createTable();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (conexaoEmMemoria != null) conexaoEmMemoria.close();
    }

    @Test
    public void deveSalvarEListarTransacoes() {
        dao.save(new Receita("Freelance", 3000.0, LocalDate.now()));
        List<Transacao> resultado = dao.listAll();
        assertEquals(1, resultado.size());
        assertEquals("Freelance", resultado.get(0).getDescription());
    }

    @Test
    public void deveLancarExcecaoComDadosInvalidos() {
        assertThrows(RuntimeException.class, () -> dao.save(null));
    }
}
