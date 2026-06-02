package fintrack.model;

import java.time.LocalDate;

public abstract class Transacao {
    private String description;
    private double value;
    private LocalDate date;
    private String category; // <- Adicione esta propriedade

    public Transacao(String description, double value, LocalDate date, String category) {
        this.description = description;
        this.value = value;
        this.date = date;
        this.category = category; // <- Inicialize aqui
    }

    public String getDescription() { return description; }
    public double getValue() { return value; }
    public LocalDate getDate() { return date; }
    
    // <- Adicione este método getter essencial
    public String getCategory() { return category; } 
    
    public abstract String getKind();
}
