package fintrack.model;

import java.time.LocalDate;

public abstract class Transacao {
    private String description;
    private double value;
    private LocalDate date;

    public Transacao(String description, double value, LocalDate date) {
        this.description = description;
        this.value = value;
        this.date = date;
    }

    public String getDescription() { return description; }
    public double getValue() { return value; }
    public LocalDate getDate() { return date; }
    public abstract String getKind();
}

