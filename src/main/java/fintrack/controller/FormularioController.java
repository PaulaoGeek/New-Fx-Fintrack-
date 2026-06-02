package fintrack.controller;

import java.time.LocalDate;

import fintrack.model.Despesa;
import fintrack.model.Receita;
import fintrack.model.Transacao;
import fintrack.repository.TransacaoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormularioController {
    @FXML private TextField txtDescription;
    @FXML private TextField txtValue;
    @FXML private DatePicker dpDate;
    @FXML private CheckBox chkIncome;
    @FXML private CheckBox chkExpense;
    @FXML private ComboBox<String> cbCategory;

    private PrincipalController mainController;
    private TransacaoDAO dao = new TransacaoDAO();
    private String activeUser;
    
    private boolean isEditMode = false;
    private String oldDescription;
    private double oldValue;

    public void setMainController(PrincipalController controller) { 
        this.mainController = controller; 
    }
    
    public void setActiveUser(String user) {
        this.activeUser = user;
    }

    @FXML
    public void initialize() {
        cbCategory.getItems().addAll("Alimentação", "Lazer", "Transporte", "Moradia", "Salário/Renda", "Saúde", "Outros");
        cbCategory.setValue("Outros");
    }

    public void prepareEditMode(Transacao t) {
        this.isEditMode = true;
        this.oldDescription = t.getDescription();
        this.oldValue = t.getValue();

        txtDescription.setText(t.getDescription());
        txtValue.setText(String.valueOf(t.getValue()));
        dpDate.setValue(t.getDate());
        cbCategory.setValue(t.getCategory());
        
        if (t instanceof Receita) {
            chkIncome.setSelected(true);
            chkExpense.setSelected(false);
        } else {
            chkExpense.setSelected(true);
            chkIncome.setSelected(false);
        }
    }

    @FXML private void handleCheckIncome() { if (chkIncome.isSelected()) chkExpense.setSelected(false); }
    @FXML private void handleCheckExpense() { if (chkExpense.isSelected()) chkIncome.setSelected(false); }

    @FXML
    private void saveTransaction() {
        String desc = txtDescription.getText();
        double val = Double.parseDouble(txtValue.getText());
        LocalDate dt = dpDate.getValue();
        String cat = cbCategory.getValue();
        
        Transacao nova;
        if (chkIncome.isSelected()) {
            nova = new Receita(desc, val, dt, cat); // <- Atualizado com 4 parâmetros
        } else {
            nova = new Despesa(desc, val, dt, cat); // <- Atualizado com 4 parâmetros
        }
        
        if (isEditMode) {
            dao.update(nova, oldDescription, oldValue);
        } else {
            dao.saveWithUser(nova, activeUser);
        }
        
        mainController.refreshScreen();
        closeWindow();
    }

    @FXML private void cancel() { closeWindow(); }
    private void closeWindow() { ((Stage) txtDescription.getScene().getWindow()).close(); }
}
