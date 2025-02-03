package ghostnetfishing.dao;

import ghostnetfishing.model.Geisternetz;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class GeisternetzDAO implements Serializable {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ghostnet_fishing");

    public void save(Geisternetz geisternetz) {
        System.out.println("DEBUG: Starting save operation for Geisternetz with ID: " + geisternetz.getId());
        EntityManager em = emf.createEntityManager(); // EntityManager hier erzeugen
        try {
            em.getTransaction().begin();
            System.out.println("DEBUG: Persisting Geisternetz: " + geisternetz);
            em.persist(geisternetz);
            em.getTransaction().commit();
            System.out.println("DEBUG: Geisternetz successfully saved.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                System.err.println("ERROR: Transaction rolled back due to an error.");
            }
            System.err.println("ERROR: Exception during save operation: " + e.getMessage());
            throw e;
        } finally {
            em.close();
            System.out.println("DEBUG: EntityManager closed after save operation.");
        }
    }

    public void update(Geisternetz geisternetz) {
        System.out.println("DEBUG: Starting update operation for Geisternetz with ID: " + geisternetz.getId());
        EntityManager em = emf.createEntityManager(); // EntityManager hier erzeugen
        try {
            em.getTransaction().begin();
            System.out.println("DEBUG: Merging Geisternetz: " + geisternetz);
            em.merge(geisternetz);
            em.getTransaction().commit();
            System.out.println("DEBUG: Geisternetz successfully updated.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                System.err.println("ERROR: Transaction rolled back due to an error.");
            }
            System.err.println("ERROR: Exception during update operation: " + e.getMessage());
            throw e;
        } finally {
            em.close();
            System.out.println("DEBUG: EntityManager closed after update operation.");
        }
    }

    public void delete(Geisternetz geisternetz) {
        System.out.println("DEBUG: Starting delete operation for Geisternetz with ID: " + geisternetz.getId());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            System.out.println("DEBUG: Merging Geisternetz before deletion: " + geisternetz);
            Geisternetz toDelete = em.merge(geisternetz);
            em.remove(toDelete);
            em.getTransaction().commit();
            System.out.println("DEBUG: Geisternetz successfully deleted.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                System.err.println("ERROR: Transaction rolled back due to an error.");
            }
            System.err.println("ERROR: Exception during delete operation: " + e.getMessage());
            throw e;
        } finally {
            em.close();
            System.out.println("DEBUG: EntityManager closed after delete operation.");
        }
    }

    public Geisternetz findById(Long id) {
        System.out.println("DEBUG: Starting findById operation for Geisternetz with ID: " + id);
        EntityManager em = emf.createEntityManager();
        try {
            Geisternetz result = em.find(Geisternetz.class, id);
            System.out.println("DEBUG: Found Geisternetz: " + result);
            return result;
        } finally {
            em.close();
            System.out.println("DEBUG: EntityManager closed after findById operation.");
        }
    }

    public List<Geisternetz> findAll() {
        System.out.println("DEBUG: Starting findAll operation.");
        EntityManager em = emf.createEntityManager();
        try {
            List<Geisternetz> results = em.createQuery("SELECT g FROM Geisternetz g", Geisternetz.class).getResultList();
            System.out.println("DEBUG: Found " + results.size() + " Geisternetze.");
            return results;
        } finally {
            em.close();
            System.out.println("DEBUG: EntityManager closed after findAll operation.");
        }
    }
}
