package fintrack.controller;

import java.time.LocalDate;

import fintrack.model.Despesa;
import fintrack.model.Receita;
import fintrack.model.Transacao;
import fintrack.repository.TransacaoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormularioController {
    @FXML private TextField txtDescription;
    @FXML private TextField txtValue;
    @FXML private DatePicker dpDate;
    @FXML private CheckBox chkIncome;
    @FXML private CheckBox chkExpense;

    private PrincipalController mainController;
    private TransacaoDAO dao = new TransacaoDAO();

    public void setMainController(PrincipalController controller) { this.mainController = controller; }

    @FXML private void handleCheckIncome() { if (chkIncome.isSelected()) chkExpense.setSelected(false); }
    @FXML private void handleCheckExpense() { if (chkExpense.isSelected()) chkIncome.setSelected(false); }

    @FXML
private void saveTransaction() {
    String desc = txtDescription.getText();
    double val = Double.parseDouble(txtValue.getText());
    LocalDate dt = dpDate.getValue();
    
    Transacao nova;
    if (chkIncome.isSelected()) {
        nova = new Receita(desc, val, dt);
    } else {
        nova = new Despesa(desc, val, dt);
    }
    
    dao.save(nova);
    mainController.refreshScreen();
    closeWindow();
}

    @FXML private void cancel() { closeWindow(); }
    private void closeWindow() { ((Stage) txtDescription.getScene().getWindow()).close(); }
}
