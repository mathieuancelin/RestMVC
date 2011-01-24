package cx.ath.mancel01.restmvc.data;

import cx.ath.mancel01.restmvc.FrameworkFilter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

public class DataHelper<T> {

    private final Class<T> clazz;
    private final String name;

    private EntityManager em() {
        return FrameworkFilter.currentEm.get();
    }

    private DataHelper(Class<T> clazz) {
        this.clazz = clazz;
        this.name = clazz.getSimpleName();
    }

    public static <T> DataHelper<T> forType(Class<T> clazz) {
        return new DataHelper<T>(clazz);
    }

    public List<T> all() {
        CriteriaQuery<T> query = em().getCriteriaBuilder().createQuery(clazz);
        query.from(clazz);
        return em().createQuery(query).getResultList();
        //return em().createQuery("select o from " + name + " o");
    }

    public long count() {
        return Long.parseLong(em().createQuery("select count(e) from " + name + " e").getSingleResult().toString());
    }

    public int deleteAll() {
        return em().createQuery("delete from " + name).executeUpdate();
    }

    public T delete(T o) {
        em().remove(o);
        return (T) o;
    }

    public T findById(Object primaryKey) {
        return em().find(clazz, primaryKey);
    }

    public Query execDo(String criteria, Object... args) {
        final String queryName = name + "." + criteria;
        final Query query = em().createNamedQuery(queryName);
        for (int i = 0; i < args.length; i++) {
            query.setParameter(i + 1, args[i]);
        }
        return query;
    }

    public T refresh(Object o) {
        em().refresh(o);
        return (T) o;
    }

    public T save(T o) {
        if (em().contains(o)) {
            return em().merge(o);
        }
        em().persist(o);
        return (T) o;
    }

    public T deleteById(final Long id) {
        T object = findById(id);
        delete(object);
        return object;
    }

    public <T> List<T> findByNamedQuery(final String namedQueryName) {
        return em().createNamedQuery(namedQueryName).getResultList();
    }

    public <T> List<T> findByNamedQuery(final String namedQueryName, final Object... params) {
        Query query = em().createNamedQuery(namedQueryName);
        int i = 1;
        for (Object p : params) {
            query.setParameter(i++, p);
        }
        return query.getResultList();
    }

    public <T> T findUniqueByNamedQuery(final String namedQueryName)  {
        return (T) em().createNamedQuery(namedQueryName).getSingleResult();
    }

    public <T> T findUniqueByNamedQuery(final String namedQueryName, final Object... params) {
        Query query = em().createNamedQuery(namedQueryName);
        int i = 1;
        for (Object p : params) {
            query.setParameter(i++, p);
        }
        return (T) query.getSingleResult();
    }

    public void rollback() {
        em().getTransaction().rollback();
    }
}
