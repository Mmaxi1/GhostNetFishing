package ghostnetfishing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Geisternetz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GeisternetzStatus status = GeisternetzStatus.GEMELDET;

    @Column
    private int groesse;

    @Column(length = 500)
    private String beschreibung;

    @Column(precision = 10, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6, nullable = false)
    private BigDecimal longitude;

    @ManyToOne
    @JoinColumn(name = "bergendePerson_id")
    private BergendePerson bergendePerson;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "meldendePerson_id")
    private MeldendePerson meldendePerson;

    public Geisternetz() {
        this.status = GeisternetzStatus.GEMELDET;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GeisternetzStatus getStatus() {
        return status;
    }

    public void setStatus(GeisternetzStatus status) {
        this.status = status; // Ermöglicht Änderungen am Status
    }

    public int getGroesse() {
        return groesse;
    }

    public void setGroesse(int groesse) {
        this.groesse = groesse;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BergendePerson getBergendePerson() {
        return bergendePerson;
    }

    public void setBergendePerson(BergendePerson bergendePerson) {
        this.bergendePerson = bergendePerson;
    }

    public MeldendePerson getMeldendePerson() {
        return meldendePerson;
    }

    public void setMeldendePerson(MeldendePerson meldendePerson) {
        this.meldendePerson = meldendePerson;
    }

    @Override
    public String toString() {
        return "Geisternetz{" +
                "id=" + id +
                ", status=" + status +
                ", groesse=" + groesse +
                ", beschreibung='" + beschreibung + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", bergendePerson=" + bergendePerson +
                ", meldendePerson=" + meldendePerson +
                '}';
    }
}
