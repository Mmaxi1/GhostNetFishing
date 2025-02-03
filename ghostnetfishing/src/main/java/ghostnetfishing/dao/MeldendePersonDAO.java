package ghostnetfishing.dao;

import ghostnetfishing.model.MeldendePerson;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class MeldendePersonDAO implements Serializable {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(MeldendePerson meldendePerson) {
        em.persist(meldendePerson);
    }

    @Transactional
    public void update(MeldendePerson meldendePerson) {
        em.merge(meldendePerson);
    }

    @Transactional
    public void delete(MeldendePerson meldendePerson) {
        MeldendePerson toDelete = em.merge(meldendePerson);
        em.remove(toDelete);
    }

    public MeldendePerson findById(Long id) {
        return em.find(MeldendePerson.class, id);
    }

    public List<MeldendePerson> findAll() {
        return em.createQuery("SELECT m FROM MeldendePerson m", MeldendePerson.class).getResultList();
    }
}
