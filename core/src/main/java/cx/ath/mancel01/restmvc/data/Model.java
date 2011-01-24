package cx.ath.mancel01.restmvc.data;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class Model<T> {
    
    @Transient
    private transient DataHelper helper;

    @Transient
    private transient Class clazz;

    public Model() {
        if (helper == null) {
            clazz = getClass();
            helper = DataHelper.forType(clazz);
        }
    }

    public T delete() {
        return (T) helper.delete(this);
    }

    public T refresh() {
        return (T) helper.refresh(this);
    }

    public T save() {
        return (T) helper.save(this);
    }

    public T merge() {
        return (T) helper.merge(this);
    }
}
