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
        try {
            alleBergendenPersonen = bergendePersonDAO.findAll(); // ✅ Liste wird beim Start geladen
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

        } catch (Exception e) {
            System.err.println("ERROR: Fehler bei der Initialisierung von GeisternetzBean: " + e.getMessage());
        }
    }

    public void speichernOderAktualisieren() {

        if (selectedGeisternetz == null) {
            selectedGeisternetz = new Geisternetz();
        }

        try {
            if (selectedGeisternetz.getId() != null) {
                geisternetzDAO.update(selectedGeisternetz);
            } else {
                geisternetzDAO.save(selectedGeisternetz);
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

        if (geisternetz == null) {
            geisternetz = new Geisternetz();
        }

        if (anonymMelden) {
            meldendePerson.setName(null);
            meldendePerson.setTelefonnummer(null);
        }

        geisternetz.setMeldendePerson(meldendePerson);
        geisternetz.setStatus(GeisternetzStatus.GEMELDET);

        try {
            geisternetzDAO.save(geisternetz);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz erfolgreich gemeldet!"));

            aktualisiereGeisternetzListe();

            geisternetz = new Geisternetz();
            meldendePerson = new MeldendePerson();

            PrimeFaces.current().ajax().update("geisternetzForm", "messages");

        } catch (Exception e) {
            System.err.println("ERROR: Fehler beim Melden des Geisternetzes: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Das Geisternetz konnte nicht gemeldet werden."));
        }
    }

    public void setStatusToBergungBevorstehend(Geisternetz netz) {

        if (selectedBergendePersonId == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte eine bergende Person auswählen."));
            return;
        }

        BergendePerson neuePerson = bergendePersonDAO.findById(selectedBergendePersonId);
        if (neuePerson == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Ausgewählte Person nicht gefunden."));
            return;
        }

        if (netz == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Geisternetz ist null."));
            return;
        }


        netz.setBergendePerson(neuePerson);
        netz.setStatus(GeisternetzStatus.BERGUNG_BEVORSTEHEND);

        try {
            geisternetzDAO.update(netz);

            aktualisiereGeisternetzListe();
            PrimeFaces.current().ajax().update("geisternetzForm");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz wurde erfolgreich der neuen Person zugewiesen."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Fehler beim Speichern der Änderung."));
            e.printStackTrace();
        }
    }

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

        try {
            geisternetzDAO.delete(netz);
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

    public void toggleAnonymMelden() {
        if (anonymMelden) {
            meldendePerson = new MeldendePerson(); // Setzt Name und Telefonnummer auf NULL
        }
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

    public Geisternetz getSelectedGeisternetz() {
        return selectedGeisternetz;
    }

    public void setSelectedGeisternetz(Geisternetz selectedGeisternetz) {
        this.selectedGeisternetz = selectedGeisternetz;
    }

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
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte Name und Telefonnummer eingeben."));
            return;
        }

        try {
            bergendePersonDAO.save(selectedBergendePerson);

            List<BergendePerson> aktualisierteListe = bergendePersonDAO.findAll();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Bergende Person erfolgreich hinzugefügt."));

            selectedBergendePerson = new BergendePerson();

            PrimeFaces.current().ajax().update("bergendePersonForm", "addPersonForm");

        } catch (Exception e) {
            System.err.println("ERROR: Fehler beim Speichern der bergenden Person: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Fehler beim Speichern der Person."));
        }
    }
}
