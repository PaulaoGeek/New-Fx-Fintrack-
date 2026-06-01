package fintrack.model;

import java.time.LocalDate;

public class Receita extends Transacao {
    public Receita(String description, double value, LocalDate date) {
        super(description, value, date);
    }
    @Override
    public String getKind() { return "Receita"; }
}
