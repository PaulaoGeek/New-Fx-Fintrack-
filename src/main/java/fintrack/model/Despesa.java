package fintrack.model;

import java.time.LocalDate;

public class Despesa extends Transacao {
    public Despesa(String description, double value, LocalDate date) {
        super(description, value, date);
    }
    @Override
    public String getKind() { return "Despesa"; }
}
