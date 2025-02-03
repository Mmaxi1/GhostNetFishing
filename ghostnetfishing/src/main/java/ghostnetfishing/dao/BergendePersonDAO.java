package ghostnetfishing.dao;

import ghostnetfishing.model.BergendePerson;
import ghostnetfishing.util.JPAUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class BergendePersonDAO implements Serializable {

    private EntityManager em = JPAUtil.getEntityManager();

    public void save(BergendePerson bergendePerson) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(bergendePerson);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Fehler beim Speichern: " + e.getMessage(), e);
        }
    }

    public void update(BergendePerson bergendePerson) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(bergendePerson);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Fehler beim Aktualisieren: " + e.getMessage(), e);
        }
    }

    public void delete(BergendePerson bergendePerson) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            BergendePerson toDelete = em.merge(bergendePerson);
            em.remove(toDelete);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Fehler beim Löschen: " + e.getMessage(), e);
        }
    }

    public BergendePerson findById(Long id) {
        return em.find(BergendePerson.class, id);
    }

    public List<BergendePerson> findAll() {
        return em.createQuery("SELECT b FROM BergendePerson b", BergendePerson.class).getResultList();
    }
}
