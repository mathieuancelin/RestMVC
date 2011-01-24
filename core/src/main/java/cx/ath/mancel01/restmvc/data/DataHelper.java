package cx.ath.mancel01.restmvc.data;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class DataHelper<T> {

    private final Class<T> clazz;
    private final String name;

    private EntityManager em() {
        return null;
    }

    private DataHelper(Class<T> clazz) {
        this.clazz = clazz;
        this.name = clazz.getSimpleName();
    }

    public static <T> DataHelper<T> forType(Class<T> clazz) {
        return new DataHelper<T>(clazz);
    }

    public Query all() {
       return em().createQuery("select o from " + name + " o");
    }

    public long count() {
        return Long.parseLong(em().createQuery("select count(e) from " + name + " e").getSingleResult().toString());
    }

    public int deleteAll() {
        return em().createQuery("delete from " + name).executeUpdate();
    }

    public T delete(Object o) {
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

    public T save(Object o) {
        em().persist(o);
        return (T) o;
    }

    public T merge(Object o) {
        return (T) em().merge(o);
    }

}
