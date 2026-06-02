package fintrack.model;
import java.time.LocalDate;

public class Receita extends Transacao {
    public Receita(String description, double value, LocalDate date, String category) {
        super(description, value, date, category);
    }
    @Override
    public String getKind() { return "Receita"; }
}
