package fintrack.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fintrack.model.Receita;
import fintrack.model.Transacao;
import fintrack.repository.TransacaoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class PrincipalController {
    @FXML private TableView<Transacao> tableTransactions;
    @FXML private TableColumn<Transacao, String> colDate;
    @FXML private TableColumn<Transacao, String> colDescription;
    @FXML private TableColumn<Transacao, Double> colValue;
    @FXML private TableColumn<Transacao, String> colKind;
    @FXML private Label lblBalance;
    @FXML private ComboBox<String> cbUsers;
    
    // NOVA INJEÇÃO: Componente visual do gráfico de pizza
    @FXML private PieChart pieChart;

    private TransacaoDAO dao = new TransacaoDAO();

    @FXML
    public void initialize() {
        dao.createTable();
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate().toString()));
        colKind.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKind()));
        
        reloadUserComboBox();
    }

    public void reloadUserComboBox() {
        List<String> usuarios = dao.listUsers();
        cbUsers.getItems().setAll(usuarios);
        if (!usuarios.isEmpty()) {
            cbUsers.setValue(usuarios.get(0));
            refreshScreen();
        }
    }

    public void refreshScreen() {
        String usuarioAtivo = cbUsers.getValue();
        if (usuarioAtivo == null) {
            tableTransactions.getItems().clear();
            lblBalance.setText("R$ 0,00");
            if (pieChart != null) pieChart.getData().clear();
            return;
        }

        tableTransactions.getItems().setAll(dao.listByByUser(usuarioAtivo));
        
        double balance = 0;
        for (Transacao t : tableTransactions.getItems()) {
            if (t instanceof Receita) balance += t.getValue();
            else balance -= t.getValue();
        }
        
        lblBalance.setText(String.format("R$ %.2f", balance));
        if (balance >= 0) {
            lblBalance.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
        } else {
            lblBalance.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
        }

        // Atualiza o gráfico automaticamente se a aba do gráfico estiver aberta de fundo
        updateChart();
    }

    // NOVA FERRAMENTA VISUAL: Calcula os totais e desenha o gráfico de pizza
        @FXML
            private void updateChart() {
            if (pieChart == null) return;
            
            // 📊 FILTRO INTELIGENTE: Pega apenas o que for "Despesa" na tabela
            List<Transacao> despesas = tableTransactions.getItems().stream()
                    .filter(t -> !"Receita".equalsIgnoreCase(t.getKind()))
                    .collect(Collectors.toList());

            // Se o usuário não tiver nenhuma despesa lançada, limpa o gráfico e encerra
            if (despesas.isEmpty()) {
                pieChart.getData().clear();
                return;
            }

            // 🗺️ MAPEAMENTO AUTOMÁTICO: Agrupa e soma os valores por Nome da Categoria (Tag)
                Map<String, Double> totaisPorCategoria = despesas.stream()
                    .collect(Collectors.groupingBy(
                        Transacao::getCategory, 
                        Collectors.summingDouble(Transacao::getValue)
                    ));

            // Monta a lista de fatias coloridas para injetar no PieChart
                ObservableList<PieChart.Data> dadosDoGrafico = FXCollections.observableArrayList();
            
            totaisPorCategoria.forEach((categoria, total) -> {
                dadosDoGrafico.add(new PieChart.Data(categoria + " (R$ " + total + ")", total));
            });

            // Atualiza o componente visual e desativa os labels antigos para evitar o bug de texto encavalado
                    pieChart.setData(dadosDoGrafico);
                    pieChart.setLabelsVisible(false); 
        }

    @FXML
    private void handleUserChange() {
        refreshScreen();
    }

    @FXML
    private void createNewUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Novo Perfil");
        dialog.setHeaderText("Cadastrar Nova Pessoa");
        dialog.setContentText("Digite o nome:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                dao.saveUser(name.trim());
                reloadUserComboBox();
                cbUsers.setValue(name.trim());
            }
        });
    }

    @FXML
    private void openForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fintrack/view/formulario.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Nova Transação");
        
        FormularioController controller = loader.getController();
        controller.setMainController(this);
        controller.setActiveUser(cbUsers.getValue());
        stage.show();
    }

    @FXML
    private void editTransaction() throws IOException {
        Transacao selected = tableTransactions.getSelectionModel().getSelectedItem();
        if (selected == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText("Nenhuma transação selecionada");
            alert.setContentText("Por favor, selecione uma linha na tabela para editar.");
            alert.showAndWait();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fintrack/view/formulario.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Editar Transação - FinTrack");
        
        FormularioController controller = loader.getController();
        controller.setMainController(this);
        controller.setActiveUser(cbUsers.getValue());
        controller.prepareEditMode(selected);
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
