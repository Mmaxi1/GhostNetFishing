package ghostnetfishing.controller;

import ghostnetfishing.dao.BergendePersonDAO;
import ghostnetfishing.dao.GeisternetzDAO;
import ghostnetfishing.dao.MeldendePersonDAO;
import ghostnetfishing.model.Geisternetz;
import ghostnetfishing.model.GeisternetzStatus;
import ghostnetfishing.model.MeldendePerson;
import ghostnetfishing.model.BergendePerson;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Named("geisternetzBean")
@SessionScoped
public class GeisternetzBean implements Serializable {

    private List<Geisternetz> alleGeisternetze;
    private List<Geisternetz> gefilterteGeisternetze;
    private String filterStatus;
    private List<String> statusOptions;
    private List<BergendePerson> alleBergendenPersonen;

    private Geisternetz geisternetz;
    private Geisternetz selectedGeisternetz;
    private MeldendePerson meldendePerson;
    private boolean anonymMelden;
    private boolean editMode;

    @Inject
    private BergendePersonDAO bergendePersonDAO;

    private BergendePerson selectedBergendePerson = new BergendePerson();

    private Long selectedBergendePersonId;

    @Inject
    private GeisternetzDAO geisternetzDAO;

    @Inject
    private MeldendePersonDAO meldendePersonDAO;

    @PostConstruct
    public void init() {
        System.out.println("DEBUG: Initialisierung von GeisternetzBean gestartet.");
        try {
            alleBergendenPersonen = bergendePersonDAO.findAll(); // ✅ Liste wird beim Start geladen
            System.out.println("DEBUG: Geladene Personen: " + alleBergendenPersonen.size());
            selectedGeisternetz = new Geisternetz();  // Initialisiere das Objekt, um NullPointerException zu vermeiden

            alleGeisternetze = geisternetzDAO.findAll();
            gefilterteGeisternetze = alleGeisternetze;
            statusOptions = Arrays.stream(GeisternetzStatus.values())
                    .map(Enum::name)
                    .toList();
            geisternetz = new Geisternetz();
            meldendePerson = new MeldendePerson();
            selectedBergendePerson = new BergendePerson();
            anonymMelden = false;
            editMode = false;

            System.out.println("DEBUG: GeisternetzBean erfolgreich initialisiert.");
        } catch (Exception e) {
            System.err.println("ERROR: Fehler bei der Initialisierung von GeisternetzBean: " + e.getMessage());
        }
    }

    public void speichernOderAktualisieren() {
        System.out.println("DEBUG: Speichern oder Aktualisieren gestartet.");

        if (selectedGeisternetz == null) {
            System.out.println("ERROR: selectedGeisternetz ist null! Es wird ein neues Objekt erstellt.");
            selectedGeisternetz = new Geisternetz();  // Fallback, falls es null ist
        }

        try {
            if (selectedGeisternetz.getId() != null) {
                geisternetzDAO.update(selectedGeisternetz);
                System.out.println("DEBUG: Update erfolgreich!");
            } else {
                geisternetzDAO.save(selectedGeisternetz);
                System.out.println("DEBUG: Neues Geisternetz gespeichert!");
            }

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz erfolgreich gespeichert!"));

            aktualisiereGeisternetzListe();
            PrimeFaces.current().executeScript("PF('editDialog').hide()");
        } catch (Exception e) {
            System.err.println("ERROR: Fehler beim Speichern oder Aktualisieren: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Das Geisternetz konnte nicht gespeichert werden."));
        }
    }

    public void meldeGeisternetz() {
        System.out.println("DEBUG: Neue Meldung eines Geisternetzes gestartet.");

        if (geisternetz == null) {
            geisternetz = new Geisternetz();
        }

        geisternetz.setMeldendePerson(meldendePerson);
        geisternetz.setStatus(GeisternetzStatus.GEMELDET);

        try {
            geisternetzDAO.save(geisternetz);
            System.out.println("DEBUG: Neues Geisternetz erfolgreich gemeldet!");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz erfolgreich gemeldet!"));

            // 🚀 Tabelle mit den neuen Daten aktualisieren!
            aktualisiereGeisternetzListe();

            // Formular zurücksetzen
            geisternetz = new Geisternetz();
            meldendePerson = new MeldendePerson();

            PrimeFaces.current().ajax().update("geisternetzForm", "messages");

        } catch (Exception e) {
            System.err.println("ERROR: Fehler beim Melden des Geisternetzes: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Das Geisternetz konnte nicht gemeldet werden."));
        }
    }

    // 📌 STATUSÄNDERUNG: BERGUNG BEVORSTEHEND
    public void setStatusToBergungBevorstehend(Geisternetz netz) {
        System.out.println("DEBUG: setStatusToBergungBevorstehend() aufgerufen für Geisternetz ID: " + (netz != null ? netz.getId() : "null"));

        if (selectedBergendePersonId == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte eine bergende Person auswählen."));
            System.out.println("DEBUG: Keine bergende Person ausgewählt.");
            return;
        }

        // Neue Bergende Person finden
        BergendePerson neuePerson = bergendePersonDAO.findById(selectedBergendePersonId);
        if (neuePerson == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Ausgewählte Person nicht gefunden."));
            System.out.println("DEBUG: Keine BergendePerson mit ID " + selectedBergendePersonId + " gefunden.");
            return;
        }

        if (netz == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Geisternetz ist null."));
            System.out.println("DEBUG: Geisternetz ist null.");
            return;
        }

        // Prüfen, ob eine Person bereits zugewiesen war und überschreiben
        if (netz.getBergendePerson() != null) {
            System.out.println("DEBUG: Geisternetz ID " + netz.getId() + " war reserviert durch Person ID: "
                    + netz.getBergendePerson().getId() + ". Wird jetzt überschrieben mit Person ID: "
                    + neuePerson.getId());
        }

        // ✅ Bergende Person aktualisieren & Status setzen
        netz.setBergendePerson(neuePerson);
        netz.setStatus(GeisternetzStatus.BERGUNG_BEVORSTEHEND);

        // ✅ In der Datenbank speichern
        try {
            geisternetzDAO.update(netz);
            System.out.println("DEBUG: Geisternetz ID " + netz.getId() + " wurde erfolgreich aktualisiert mit neuer Person ID: "
                    + neuePerson.getId());

            // ✅ UI aktualisieren
            aktualisiereGeisternetzListe();
            PrimeFaces.current().ajax().update("geisternetzForm");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz wurde erfolgreich der neuen Person zugewiesen."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Fehler beim Speichern der Änderung."));
            System.out.println("DEBUG: Fehler beim Überschreiben von Geisternetz ID " + netz.getId());
            e.printStackTrace();
        }
    }

    // 📌 STATUSÄNDERUNG: GEBORGEN
    public void setStatusToGeborgen(Geisternetz netz) {
        if (netz.getBergendePerson() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Dieses Geisternetz wurde noch keiner bergenden Person zugewiesen."));
            return;
        }

        netz.setStatus(GeisternetzStatus.GEBORGEN);
        geisternetzDAO.update(netz);
        aktualisiereGeisternetzListe();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz wurde als geborgen markiert."));
        System.out.println("DEBUG: Geisternetz ID " + netz.getId() + " wurde als geborgen markiert.");
    }

    public void loescheGeisternetz(Geisternetz netz) {
        if (netz == null) {
            System.err.println("ERROR: Das zu löschende Geisternetz ist NULL! Methode wird nicht ausgeführt.");
            return;
        }

        if (netz.getId() == null) {
            System.err.println("ERROR: Geisternetz hat keine gültige ID! Methode wird nicht ausgeführt.");
            return;
        }

        System.out.println("DEBUG: Löschen gestartet für Geisternetz mit ID: " + netz.getId());

        try {
            geisternetzDAO.delete(netz);
            System.out.println("DEBUG: Geisternetz mit ID " + netz.getId() + " wurde erfolgreich gelöscht.");

            aktualisiereGeisternetzListe();
            PrimeFaces.current().ajax().update("geisternetzForm messages");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz wurde erfolgreich gelöscht."));

        } catch (Exception e) {
            System.err.println("ERROR: Fehler beim Löschen des Geisternetzes: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Löschen fehlgeschlagen."));
        }
    }

    private void aktualisiereGeisternetzListe() {
        alleGeisternetze = geisternetzDAO.findAll();
        gefilterteGeisternetze = alleGeisternetze;
        PrimeFaces.current().ajax().update("geisternetzForm");
    }

    private void resetForm() {
        geisternetz = new Geisternetz();
        meldendePerson = new MeldendePerson();
        anonymMelden = false;
        editMode = false;
    }

    public void updateGefilterteGeisternetze() {
        if (filterStatus == null || filterStatus.isEmpty()) {
            gefilterteGeisternetze = alleGeisternetze;
        } else {
            gefilterteGeisternetze = alleGeisternetze.stream()
                    .filter(netz -> netz.getStatus().name().equalsIgnoreCase(filterStatus))
                    .toList();
        }
    }

    public void toggleAnonymMelden() {
        if (anonymMelden) {
            meldendePerson.setName("");
            meldendePerson.setTelefonnummer("");
        }
    }

    // Getter und Setter

    public boolean isEditMode() {
        return editMode;
    }

    public List<BergendePerson> getAlleBergendenPersonen() {
        return bergendePersonDAO.findAll();
    }

    public BergendePerson getSelectedBergendePerson() {
        return selectedBergendePerson;
    }

    public void setSelectedBergendePerson(BergendePerson selectedBergendePerson) {
        this.selectedBergendePerson = selectedBergendePerson;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public Geisternetz getSelectedGeisternetz() {
        return selectedGeisternetz;
    }

    public void setSelectedGeisternetz(Geisternetz selectedGeisternetz) {
        this.selectedGeisternetz = selectedGeisternetz;
    }

    // Getter und Setter für die Variable
    public Long getSelectedBergendePersonId() {
        return selectedBergendePersonId;
    }

    public void setSelectedBergendePersonId(Long selectedBergendePersonId) {
        this.selectedBergendePersonId = selectedBergendePersonId;
    }

    public MeldendePerson getMeldendePerson() {
        if (meldendePerson == null) {
            meldendePerson = new MeldendePerson();
        }
        return meldendePerson;
    }

    public void setMeldendePerson(MeldendePerson meldendePerson) {
        this.meldendePerson = meldendePerson;
    }

    public List<Geisternetz> getAlleGeisternetze() {
        return alleGeisternetze;
    }

    public List<Geisternetz> getGefilterteGeisternetze() {
        return gefilterteGeisternetze;
    }

    public List<String> getStatusOptions() {
        return statusOptions;
    }

    public Geisternetz getGeisternetz() {
        return geisternetz;
    }

    public boolean isAnonymMelden() {
        return anonymMelden;
    }

    public void setAnonymMelden(boolean anonymMelden) {
        this.anonymMelden = anonymMelden;
    }

    public List<Geisternetz> getGeisternetzeGemeldet() {
        return alleGeisternetze.stream()
                .filter(netz -> netz.getStatus() == GeisternetzStatus.GEMELDET)
                .collect(Collectors.toList());
    }

    public List<Geisternetz> getGeisternetzeReserviert() {
        return alleGeisternetze.stream()
                .filter(netz -> netz.getStatus() == GeisternetzStatus.BERGUNG_BEVORSTEHEND)
                .collect(Collectors.toList());
    }

    public void addBergendePerson() {
        if (selectedBergendePerson == null) {
            selectedBergendePerson = new BergendePerson();
        }

        if (selectedBergendePerson.getName() == null || selectedBergendePerson.getName().trim().isEmpty() ||
                selectedBergendePerson.getTelefonnummer() == null || selectedBergendePerson.getTelefonnummer().trim().isEmpty()) {
            System.out.println("DEBUG: Name oder Telefonnummer fehlt! Speichern abgebrochen.");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte Name und Telefonnummer eingeben."));
            return;
        }

        try {
            System.out.println("DEBUG: Speichern der Person gestartet: " + selectedBergendePerson);
            bergendePersonDAO.save(selectedBergendePerson);
            System.out.println("DEBUG: Speichern erfolgreich für: " + selectedBergendePerson);

            // 🔍 Überprüfe, ob die Liste aktualisiert wird
            List<BergendePerson> aktualisierteListe = bergendePersonDAO.findAll();
            System.out.println("DEBUG: Anzahl der bergenden Personen nach Speichern: " + aktualisierteListe.size());

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Bergende Person erfolgreich hinzugefügt."));

            // Formular zurücksetzen
            selectedBergendePerson = new BergendePerson();

            // 🔍 Überprüfe, ob das UI-Update wirklich aufgerufen wird
            System.out.println("DEBUG: PrimeFaces AJAX Update für bergendePersonForm wird aufgerufen.");
            PrimeFaces.current().ajax().update("bergendePersonForm", "addPersonForm");

        } catch (Exception e) {
            System.err.println("ERROR: Fehler beim Speichern der bergenden Person: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Fehler beim Speichern der Person."));
        }
    }
}
