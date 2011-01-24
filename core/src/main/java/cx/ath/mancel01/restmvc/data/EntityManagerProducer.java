package cx.ath.mancel01.restmvc.data;

import cx.ath.mancel01.restmvc.FrameworkFilter;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

@RequestScoped
public class EntityManagerProducer implements Serializable {

    private static final long serialVersionUID = -5267593171036179836L;

    @Produces
    public EntityManager create() {
        return FrameworkFilter.currentEm.get();
    }

    public void close(@Disposes EntityManager em) {
        em.close();
    }
}
