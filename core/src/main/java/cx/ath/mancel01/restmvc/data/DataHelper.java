/*
 *  Copyright 2010 Mathieu ANCELIN.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package cx.ath.mancel01.restmvc.data;

import cx.ath.mancel01.restmvc.FrameworkFilter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

/**
 *
 * @author Mathieu ANCELIN
 */
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
