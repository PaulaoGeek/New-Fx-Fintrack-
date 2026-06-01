package fintrack.controller;

import java.io.IOException;

import fintrack.model.Receita;
import fintrack.model.Transacao;
import fintrack.repository.TransacaoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class PrincipalController {
    @FXML private TableView<Transacao> tableTransactions;
    @FXML private TableColumn<Transacao, String> colDate;
    @FXML private TableColumn<Transacao, String> colDescription;
    @FXML private TableColumn<Transacao, Double> colValue;
    @FXML private TableColumn<Transacao, String> colKind;
    @FXML private Label lblBalance;

    private TransacaoDAO dao = new TransacaoDAO();

    @FXML
    public void initialize() {
        dao.createTable();
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate().toString()));
        colKind.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKind()));
        refreshScreen();
    }

   public void refreshScreen() {
    // Agora que o DAO retorna List<Transacao>, o setAll vai funcionar perfeitamente
    tableTransactions.getItems().setAll(dao.listAll());
    double balance = 0;
    for (Transacao t : tableTransactions.getItems()) {
        if (t instanceof Receita) balance += t.getValue();
        else balance -= t.getValue();
    }
    lblBalance.setText(String.format("R$ %.2f", balance));
}

@FXML
private void openForm() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fintrack/view/formulario.fxml"));
    Stage stage = new Stage();
    stage.setScene(new Scene(loader.load()));
    stage.setTitle("Nova Transação");
    
    // Certifique-se de que a importação do FormularioController está correta no topo do arquivo
    FormularioController controller = loader.getController();
    controller.setMainController(this);
    stage.show();
}

    @FXML
    private void removeTransaction() {
        Transacao selected = tableTransactions.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.delete(selected.getDescription(), selected.getValue());
            refreshScreen();
        }
    }
}
