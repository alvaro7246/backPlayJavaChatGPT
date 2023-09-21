package models;

import javax.persistence.*;

@Entity
@Table(name = "tu_tabla")
public class DBModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public String campo1;
    public String campo2;
    // Otros campos

    // Métodos getter y setter
}