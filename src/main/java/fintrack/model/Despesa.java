package fintrack.model;
import java.time.LocalDate;

public class Despesa extends Transacao {
    public Despesa(String description, double value, LocalDate date, String category) {
        super(description, value, date, category);
    }
    @Override
    public String getKind() { return "Despesa"; }
}
