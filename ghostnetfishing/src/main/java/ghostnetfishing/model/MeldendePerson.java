package ghostnetfishing.model;

import jakarta.persistence.*;

@Entity
public class MeldendePerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true) // NULL-Werte erlaubt
    private String name;

    @Column(nullable = true) // NULL-Werte erlaubt
    private String telefonnummer;

    public MeldendePerson() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name != null && name.trim().isEmpty()) ? null : name;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = (telefonnummer != null && telefonnummer.trim().isEmpty()) ? null : telefonnummer;
    }

    @Override
    public String toString() {
        return "MeldendePerson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", telefonnummer='" + telefonnummer + '\'' +
                '}';
    }
}
